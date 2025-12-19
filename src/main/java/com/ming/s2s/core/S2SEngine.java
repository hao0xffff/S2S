package com.ming.s2s.core;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.ming.s2s.common.utils.NamingUtils;
import com.ming.s2s.core.converter.TypeConverter;
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
    private Configuration freemarkerConfig;

    /**
     * Plan B 核心逻辑：多表解析与代码生成
     */
    public Map<String, String> generateAll(String sql, ProjectContext project) throws Exception {
        // 1. 使用 Druid 解析多条 SQL 语句
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        Map<String, String> resultArray = new HashMap<>();

        // 2. 准备项目级全局数据 (独立于任何表)
        Map<String, Object> projectData = new HashMap<>();
        projectData.put("packageName", project.getPackageName());
        projectData.put("projectName", project.getProjectName());

        // 【核心修改点】：处理 projectName 中的中划线，防止类名非法（如 Ming-mall 爆红）
        // 将 ming-mall-system 处理成 MingMallSystem
        String safeProjectName = project.getProjectName().replace("-", "_");
        String appBaseName = NamingUtils.toClassName(safeProjectName);
        projectData.put("className", appBaseName);

        // 3. 生成项目环境文件 (只生成一次，使用全局数据)
        resultArray.put("pom.xml", render(projectData, "pom.xml.ftl"));
        resultArray.put("application.properties", render(projectData, "application.properties.ftl"));
        resultArray.put("Result.java", render(projectData, "Result.java.ftl"));
        resultArray.put("ResultCode.java", render(projectData, "ResultCode.java.ftl"));

        // 重点：启动类文件名使用安全类名，确保文件名与 class 定义一致
        String appFileName = appBaseName + "Application.java";
        resultArray.put(appFileName, render(projectData, "Application.java.ftl"));

        // 4. 循环处理每一张表生成业务代码
        for (SQLStatement stmt : statementList) {
            if (stmt instanceof MySqlCreateTableStatement createTableStmt) {
                // 解析单表上下文
                Map<String, Object> tableData = prepareTableData(createTableStmt, project);
                String className = (String) tableData.get("className");

                // 渲染单表业务六件套
                resultArray.put(className + ".java", render(tableData, "Entity.java.ftl"));
                resultArray.put(className + "Mapper.xml", render(tableData, "Mapper.xml.ftl"));
                resultArray.put(className + "Mapper.java", render(tableData, "Mapper.java.ftl"));
                resultArray.put("I" + className + "Service.java", render(tableData, "Service.java.ftl"));
                resultArray.put(className + "ServiceImpl.java", render(tableData, "ServiceImpl.java.ftl"));
                resultArray.put(className + "Controller.java", render(tableData, "Controller.java.ftl"));
            }
        }
        return resultArray;
    }

    /**
     * 单表解析逻辑封装
     */
    private Map<String, Object> prepareTableData(MySqlCreateTableStatement stmt, ProjectContext project) {
        TableContext context = new TableContext();
        String rawTableName = stmt.getTableName().toString().replace("`", "");
        context.setTableName(rawTableName);
        context.setClassName(NamingUtils.toClassName(rawTableName));
        context.setTableComment(stmt.getComment() != null ? stmt.getComment().toString().replace("'", "") : "");
        context.setPrimaryKeyType("Long");

        List<ColumnContext> cols = new ArrayList<>();
        Set<String> imports = new HashSet<>();

        for (SQLTableElement element : stmt.getTableElementList()) {
            if (element instanceof SQLColumnDefinition colDef) {
                ColumnContext col = new ColumnContext();
                String rawColName = colDef.getName().toString().replace("`", "");
                col.setColumnName(rawColName);
                col.setPropertyName(NamingUtils.toPropertyName(rawColName));

                String javaType = TypeConverter.convert(colDef.getDataType().getName());
                col.setJavaType(javaType);

                if (colDef.toString().toUpperCase().contains("PRIMARY KEY")) {
                    context.setPrimaryKeyType(javaType);
                }

                String importPath = TypeConverter.getImport(javaType);
                if (importPath != null) imports.add(importPath);

                col.setComment(colDef.getComment() != null ? colDef.getComment().toString().replace("'", "") : "");
                cols.add(col);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("packageName", project.getPackageName());
        data.put("projectName", project.getProjectName());
        data.put("tableName", context.getTableName());
        data.put("className", context.getClassName());
        data.put("tableComment", context.getTableComment());
        data.put("primaryKeyType", context.getPrimaryKeyType());
        data.put("columns", cols);
        data.put("imports", imports);
        data.put("classNameUncap", NamingUtils.toPropertyName(context.getClassName()));
        return data;
    }

    /**
     * 落地磁盘逻辑
     */
    public void writeToDisk(Map<String, String> codes, ProjectContext project) throws Exception {
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
            } else if (fileName.endsWith("Mapper.xml")) {
                finalPath = Paths.get(baseDir, "src/main/resources/mapper", fileName);
            } else {
                String subPackage = determineSubPackage(fileName);
                if (subPackage.isEmpty()) {
                    finalPath = Paths.get(baseDir, "src/main/java", packagePath, fileName);
                } else {
                    finalPath = Paths.get(baseDir, "src/main/java", packagePath, subPackage, fileName);
                }
            }

            Files.createDirectories(finalPath.getParent());
            Files.write(finalPath, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String determineSubPackage(String fileName) {
        if (fileName.equals("Result.java") || fileName.equals("ResultCode.java")) return "common/api";
        if (fileName.endsWith("Controller.java")) return "controller";
        if (fileName.endsWith("ServiceImpl.java")) return "service/impl";
        if (fileName.endsWith("Service.java")) return "service";
        if (fileName.endsWith("Mapper.java")) return "mapper";
        // 修正判定：只要文件名以 Application.java 结尾且没被上述规则截获，就放根包
        if (fileName.endsWith("Application.java")) return "";

        return "entity";
    }

    private String render(Map<String, Object> data, String templateName) throws Exception {
        Template template = freemarkerConfig.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        return writer.toString();
    }
}