package com.ming.s2s.model.dto;

import com.ming.s2s.common.validation.InputValidator;
import com.ming.s2s.model.metadata.TechStackConfig;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Code generation request DTO
 */
@Data
public class GenRequest {
    
    @NotBlank(message = "SQL cannot be blank")
    private String sql;         // CREATE TABLE SQL
    
    @NotBlank(message = "Project name cannot be blank")
    private String projectName; // Project name (e.g., order-center)
    
    @NotBlank(message = "Package name cannot be blank")
    private String packageName; // Base package name (e.g., com.ming.order)
    
    @NotBlank(message = "Output directory cannot be blank")
    private String outputDir;   // Output root directory (e.g., D:/S2S_Output)
    
    private String dbType = "mysql"; // Database type (mysql, postgresql, oracle, etc.), default mysql
    
    private TechStackConfig techStack; // Technology stack configuration (optional, defaults to MyBatis+Maven)

    /**
     * Validate all fields
     */
    public void validate() {
        InputValidator.validateSql(this.sql);
        InputValidator.validateProjectName(this.projectName);
        InputValidator.validatePackageName(this.packageName);
        InputValidator.validateOutputDir(this.outputDir);
        
        // Validate tech stack if provided
        if (techStack != null) {
            techStack.validate();
        }
    }
}

