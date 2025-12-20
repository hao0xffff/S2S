package com.ming.s2s.model.metadata;

import lombok.Data;

/**
 * Project metadata
 */
@Data
public class ProjectMetadata {
    private String projectName;   // Project name, e.g., order-service
    private String packageName;   // Package name, e.g., com.ming.test
    private String outputDir;     // Output root directory, e.g., D:/S2S_Output
    private TechStackConfig techStack; // Technology stack configuration
    
    public ProjectMetadata() {
        // Default tech stack: MyBatis + Maven
        this.techStack = new TechStackConfig();
    }
}

