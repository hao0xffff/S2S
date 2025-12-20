package com.ming.s2s.model.metadata;

import lombok.Data;

/**
 * Technology stack configuration
 */
@Data
public class TechStackConfig {
    
    /**
     * ORM framework: mybatis, jpa, mybatis-plus
     */
    private String ormFramework = "mybatis";
    
    /**
     * Build tool: maven, gradle
     */
    private String buildTool = "maven";
    
    /**
     * Use Lombok
     */
    private boolean useLombok = true;
    
    /**
     * Use Swagger/OpenAPI
     */
    private boolean useSwagger = false;
    
    /**
     * Java version: 8, 11, 17, 21
     */
    private String javaVersion = "17";
    
    /**
     * Spring Boot version: 2.7.x, 3.1.x, 3.2.x
     */
    private String springBootVersion = "3.1.5";
    
    /**
     * Database type: mysql, postgresql, oracle
     */
    private String databaseType = "mysql";
    
    /**
     * Use pagination plugin (PageHelper for MyBatis)
     */
    private boolean usePagination = false;
    
    /**
     * Use Redis
     */
    private boolean useRedis = false;
    
    /**
     * Use validation (Bean Validation)
     */
    private boolean useValidation = true;
    
    /**
     * Validate tech stack configuration
     */
    public void validate() {
        if (!ormFramework.matches("mybatis|jpa|mybatis-plus")) {
            throw new IllegalArgumentException("Invalid ORM framework: " + ormFramework);
        }
        if (!buildTool.matches("maven|gradle")) {
            throw new IllegalArgumentException("Invalid build tool: " + buildTool);
        }
        if (!javaVersion.matches("8|11|17|21")) {
            throw new IllegalArgumentException("Invalid Java version: " + javaVersion);
        }
    }
}

