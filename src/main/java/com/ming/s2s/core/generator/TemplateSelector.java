package com.ming.s2s.core.generator;

import com.ming.s2s.model.metadata.TechStackConfig;

/**
 * Template selector based on technology stack
 * Supports backward compatibility with existing templates
 */
public class TemplateSelector {

    /**
     * Get build file template name based on build tool
     */
    public static String getBuildTemplate(TechStackConfig config) {
        if ("gradle".equals(config.getBuildTool())) {
            return "gradle/build.gradle.ftl";
        }
        // Default: Maven pom.xml
        return "maven/pom.xml.ftl";
    }
    
    /**
     * Get Application template name based on ORM framework
     */
    public static String getApplicationTemplate(TechStackConfig config) {
        String orm = config.getOrmFramework();
        if ("mybatis-plus".equals(orm)) {
            return "mybatis-plus/Application.java.ftl";
        } else if ("mybatis".equals(orm)) {
            return "mybatis/Application.java.ftl";
        } else if ("jpa".equals(orm)) {
            return "jpa/Application.java.ftl";
        }
        // Default: common Application (no ORM-specific annotations)
        return "common/Application.java.ftl";
    }

    /**
     * Get Entity template name based on ORM framework
     */
    public static String getEntityTemplate(TechStackConfig config) {
        String orm = config.getOrmFramework();
        if ("jpa".equals(orm)) {
            return "jpa/Entity.java.ftl";
        } else if ("mybatis-plus".equals(orm)) {
            return "mybatis-plus/Entity.java.ftl";
        }
        // Default: MyBatis Entity
        return "mybatis/Entity.java.ftl";
    }

    /**
     * Get Mapper template name based on ORM framework
     */
    public static String getMapperTemplate(TechStackConfig config) {
        String orm = config.getOrmFramework();
        if ("jpa".equals(orm)) {
            return "jpa/Repository.java.ftl";
        } else if ("mybatis-plus".equals(orm)) {
            return "mybatis-plus/Mapper.java.ftl";
        }
        // Default: MyBatis Mapper
        return "mybatis/Mapper.java.ftl";
    }

    /**
     * Get Mapper XML template name (only for MyBatis, not MyBatis-Plus)
     * MyBatis-Plus uses BaseMapper which provides CRUD methods without XML
     */
    public static String getMapperXmlTemplate(TechStackConfig config) {
        String orm = config.getOrmFramework();
        if ("jpa".equals(orm)) {
            return null; // JPA doesn't need XML mapper
        } else if ("mybatis-plus".equals(orm)) {
            return null; // MyBatis-Plus uses BaseMapper, no XML needed for basic CRUD
        } else if ("mybatis".equals(orm)) {
            return "mybatis/Mapper.xml.ftl"; // Only MyBatis needs XML mapper
        }
        return null;
    }

    /**
     * Get Service template name (common for all ORM frameworks)
     */
    public static String getServiceTemplate(TechStackConfig config) {
        return "common/Service.java.ftl";
    }

    /**
     * Get ServiceImpl template name
     */
    public static String getServiceImplTemplate(TechStackConfig config) {
        String orm = config.getOrmFramework();
        if ("jpa".equals(orm)) {
            return "jpa/ServiceImpl.java.ftl";
        } else if ("mybatis-plus".equals(orm)) {
            return "mybatis-plus/ServiceImpl.java.ftl";
        }
        // Default: MyBatis ServiceImpl
        return "mybatis/ServiceImpl.java.ftl";
    }

    /**
     * Get Controller template name
     * Note: The common Controller template supports both Swagger and non-Swagger modes
     * based on techStack.useSwagger flag
     */
    public static String getControllerTemplate(TechStackConfig config) {
        // Common Controller template handles both Swagger and non-Swagger cases
        // via conditional FreeMarker directives (#if techStack.useSwagger)
        return "common/Controller.java.ftl";
    }
    
    /**
     * Get application.properties template (common for all)
     */
    public static String getApplicationPropertiesTemplate() {
        return "common/application.properties.ftl";
    }
    
    /**
     * Get Result template (common for all)
     */
    public static String getResultTemplate() {
        return "common/Result.java.ftl";
    }
    
    /**
     * Get ResultCode template (common for all)
     */
    public static String getResultCodeTemplate() {
        return "common/ResultCode.java.ftl";
    }
    
    /**
     * Get OpenAPI Config template (only when Swagger is enabled)
     */
    public static String getOpenApiConfigTemplate() {
        return "common/OpenApiConfig.java.ftl";
    }
}

