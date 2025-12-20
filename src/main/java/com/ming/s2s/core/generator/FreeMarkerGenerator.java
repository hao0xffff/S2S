package com.ming.s2s.core.generator;

import com.ming.s2s.model.metadata.ProjectMetadata;
import com.ming.s2s.model.metadata.TableMetadata;
import com.ming.s2s.model.metadata.TechStackConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FreeMarker-based code generator strategy implementation
 * Default generator for MyBatis + Maven
 */
@Component
public class FreeMarkerGenerator implements CodeGenerator {

    @Autowired
    private Configuration freemarkerConfig;

    @Override
    public boolean supports(String ormFramework, String buildTool) {
        // Supports MyBatis, MyBatis-Plus with Maven
        return ("mybatis".equalsIgnoreCase(ormFramework) || "mybatis-plus".equalsIgnoreCase(ormFramework))
                && "maven".equalsIgnoreCase(buildTool);
    }

    @Override
    public Map<String, String> generate(ProjectMetadata projectMetadata, List<TableMetadata> tableMetadataList) {
        Map<String, String> result = new HashMap<>();

        // Generate project-level files
        generateProjectFiles(projectMetadata, result);

        // Generate table-level files for each table
        for (TableMetadata table : tableMetadataList) {
            generateTableFiles(projectMetadata, table, result);
        }

        return result;
    }

    private void generateProjectFiles(ProjectMetadata projectMetadata, Map<String, String> result) {
        Map<String, Object> data = buildProjectData(projectMetadata);
        TechStackConfig techStack = projectMetadata.getTechStack();

        try {
            // Generate build file based on build tool
            String buildTemplate = TemplateSelector.getBuildTemplate(techStack);
            String buildFileName = buildTemplate.contains("gradle") ? "build.gradle" : "pom.xml";
            result.put(buildFileName, render(data, buildTemplate));

            // Generate common configuration files
            result.put("application.properties", render(data, TemplateSelector.getApplicationPropertiesTemplate()));
            result.put("Result.java", render(data, TemplateSelector.getResultTemplate()));
            result.put("ResultCode.java", render(data, TemplateSelector.getResultCodeTemplate()));

            // Generate Application class (ORM-specific)
            String appBaseName = (String) data.get("className");
            String appFileName = appBaseName + "Application.java";
            String appTemplate = TemplateSelector.getApplicationTemplate(techStack);
            result.put(appFileName, render(data, appTemplate));
            
            // Generate OpenAPI Config (only when Swagger is enabled)
            if (techStack.isUseSwagger()) {
                String openApiConfigTemplate = TemplateSelector.getOpenApiConfigTemplate();
                result.put("OpenApiConfig.java", render(data, openApiConfigTemplate));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate project files", e);
        }
    }

    private Map<String, Object> buildProjectData(ProjectMetadata projectMetadata) {
        // Use RootModel for structured data
        RootModel rootModel = RootModel.forProject(projectMetadata);
        return rootModel.toMap();
    }

    private void generateTableFiles(ProjectMetadata projectMetadata, TableMetadata table, Map<String, String> result) {
        Map<String, Object> data = buildTableData(projectMetadata, table);
        String className = table.getClassName();
        TechStackConfig techStack = projectMetadata.getTechStack();

        // Sanitize class name to prevent filename injection
        String safeClassName = com.ming.s2s.common.validation.PathValidator.sanitizeFileName(className);

        try {
            // Generate Entity
            String entityTemplate = TemplateSelector.getEntityTemplate(techStack);
            result.put(safeClassName + ".java", render(data, entityTemplate));

            // Generate Mapper/Repository (only for MyBatis/JPA)
            String mapperTemplate = TemplateSelector.getMapperTemplate(techStack);
            if (mapperTemplate != null) {
                String mapperFileName = "jpa".equals(techStack.getOrmFramework()) 
                    ? safeClassName + "Repository.java" 
                    : safeClassName + "Mapper.java";
                result.put(mapperFileName, render(data, mapperTemplate));
            }

            // Generate Mapper XML (only for MyBatis)
            String mapperXmlTemplate = TemplateSelector.getMapperXmlTemplate(techStack);
            if (mapperXmlTemplate != null) {
                result.put(safeClassName + "Mapper.xml", render(data, mapperXmlTemplate));
            }

            // Generate Service
            String serviceTemplate = TemplateSelector.getServiceTemplate(techStack);
            result.put("I" + safeClassName + "Service.java", render(data, serviceTemplate));

            // Generate ServiceImpl
            String serviceImplTemplate = TemplateSelector.getServiceImplTemplate(techStack);
            result.put(safeClassName + "ServiceImpl.java", render(data, serviceImplTemplate));

            // Generate Controller
            String controllerTemplate = TemplateSelector.getControllerTemplate(techStack);
            result.put(safeClassName + "Controller.java", render(data, controllerTemplate));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate table files for " + className, e);
        }
    }

    private Map<String, Object> buildTableData(ProjectMetadata projectMetadata, TableMetadata table) {
        // Use RootModel for structured data
        RootModel rootModel = RootModel.forTable(projectMetadata, table);
        return rootModel.toMap();
    }

    private String render(Map<String, Object> data, String templateName) throws Exception {
        Template template = freemarkerConfig.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        return writer.toString();
    }
}

