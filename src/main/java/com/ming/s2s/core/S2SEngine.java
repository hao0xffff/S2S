package com.ming.s2s.core;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.ming.s2s.common.NamingUtils;
import com.ming.s2s.core.converter.TypeConverter;
import com.ming.s2s.core.parser.SqlParser;
import com.ming.s2s.model.context.ColumnContext;
import com.ming.s2s.model.context.ProjectContext;
import com.ming.s2s.model.context.TableContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class S2SEngine {

    @Autowired
    private SqlParser sqlParser;

    @Autowired
    private Configuration freemarkerConfig;

    /**
     * 生成全套代码，包括环境文件
     */
    public Map<String, String> generateAll(String sql, ProjectContext project) throws Exception {
        Object result = sqlParser.parse(sql);
        if (!(result instanceof MySqlCreateTableStatement stmt)) {
            throw new RuntimeException("目前仅支持 MySql 的 CREATE TABLE 语句");
        }

        TableContext context = new TableContext();
        String rawTableName = stmt.getTableName().toString().replace("`", "");
        context.setTableName(rawTableName);
        context.setClassName(NamingUtils.toClassName(rawTableName));
        context.setTableComment(stmt.getComment() != null ? stmt.getComment().toString().replace("'", "") : "");

        // 默认主键类型
        context.setPrimaryKeyType("Long");

        List<ColumnContext> cols = new ArrayList<>();
        for (SQLTableElement element : stmt.getTableElementList()) {
            if (element instanceof SQLColumnDefinition colDef) {
                ColumnContext col = new ColumnContext();
                String rawColName = colDef.getName().toString().replace("`", "");
                col.setColumnName(rawColName);
                col.setPropertyName(NamingUtils.toPropertyName(rawColName));

                String javaType = TypeConverter.convert(colDef.getDataType().getName());
                col.setJavaType(javaType);

                // 动态抓取主键类型：如果包含 PRIMARY KEY 关键字
                if (colDef.toString().toUpperCase().contains("PRIMARY KEY")) {
                    context.setPrimaryKeyType(javaType);
                }

                String importPath = TypeConverter.getImport(javaType);
                if (importPath != null) context.getImports().add(importPath);

                col.setComment(colDef.getComment() != null ? colDef.getComment().toString().replace("'", "") : "");
                cols.add(col);
            }
        }
        context.setColumns(cols);

        // 组装渲染数据
        Map<String, Object> data = new HashMap<>();
        data.put("projectName", project.getProjectName());
        data.put("packageName", project.getPackageName());
        data.put("tableName", context.getTableName());
        data.put("className", context.getClassName());
        data.put("primaryKeyType", context.getPrimaryKeyType());
        data.put("tableComment", context.getTableComment());
        data.put("columns", context.getColumns());
        data.put("imports", context.getImports());

        Map<String, String> resultArray = new HashMap<>();
        String className = context.getClassName();

        // 1. 业务六件套
        resultArray.put(className + ".java", render(data, "Entity.java.ftl"));
        resultArray.put(className + "Mapper.xml", render(data, "Mapper.xml.ftl"));
        resultArray.put(className + "Mapper.java", render(data, "Mapper.java.ftl"));
        resultArray.put("I" + className + "Service.java", render(data, "Service.java.ftl"));
        resultArray.put(className + "ServiceImpl.java", render(data, "ServiceImpl.java.ftl"));
        resultArray.put(className + "Controller.java", render(data, "Controller.java.ftl"));

        // 2. 环境三件套
        resultArray.put("pom.xml", render(data, "pom.xml.ftl"));
        resultArray.put("application.properties", render(data, "application.properties.ftl"));
        resultArray.put(className + "Application.java", render(data, "Application.java.ftl"));

        return resultArray;
    }

    /**
     * 落地到磁盘：根据 Maven 规范和用户指定的路径
     */
    public void writeToDisk(Map<String, String> codes, ProjectContext project) throws Exception {
        // 拼接最终的项目根目录
        String baseDir = Paths.get(project.getOutputDir(), project.getProjectName()).toString();
        String packagePath = project.getPackageName().replace(".", "/");

        for (Map.Entry<String, String> entry : codes.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();
            Path finalPath;

            if (fileName.equals("pom.xml")) {
                finalPath = Paths.get(baseDir, fileName);
            } else if (fileName.equals("application.properties")) {
                finalPath = Paths.get(baseDir, "src/main/resources", fileName);
            } else if (fileName.endsWith("Application.java")) {
                finalPath = Paths.get(baseDir, "src/main/java", packagePath, fileName);
            } else if (fileName.endsWith("Mapper.xml")) {
                finalPath = Paths.get(baseDir, "src/main/resources/mapper", fileName);
            } else {
                // 根据后缀自动分包
                String subPackage = determineSubPackage(fileName);
                finalPath = Paths.get(baseDir, "src/main/java", packagePath, subPackage, fileName);
            }

            Files.createDirectories(finalPath.getParent());
            Files.write(finalPath, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String determineSubPackage(String fileName) {
        if (fileName.endsWith("Controller.java")) return "controller";
        if (fileName.endsWith("ServiceImpl.java")) return "service/impl";
        if (fileName.endsWith("Service.java")) return "service";
        if (fileName.endsWith("Mapper.java")) return "mapper";
        return "entity";
    }

    private String render(Map<String, Object> data, String templateName) throws Exception {
        Template template = freemarkerConfig.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        return writer.toString();
    }
}