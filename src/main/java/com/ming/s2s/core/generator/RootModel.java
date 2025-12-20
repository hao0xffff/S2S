package com.ming.s2s.core.generator;

import com.ming.s2s.model.metadata.ColumnMetadata;
import com.ming.s2s.model.metadata.ProjectMetadata;
import com.ming.s2s.model.metadata.TableMetadata;
import com.ming.s2s.model.metadata.TechStackConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Root data model for FreeMarker templates
 * Provides a structured, hierarchical data model for template rendering
 * 
 * Structure:
 * {
 *   "project": { "packageName": "...", "projectName": "...", "javaVersion": "17" },
 *   "table": { "className": "User", "columns": [...] },
 *   "config": { "useLombok": true, "useValidation": true }
 * }
 */
@Data
public class RootModel {
    
    /**
     * Project-level information
     */
    @Data
    public static class ProjectInfo {
        private String packageName;
        private String projectName;
        private String className;  // Application class name (e.g., OrderCenterApplication)
        private String javaVersion;
        private String springBootVersion;
    }
    
    /**
     * Table-level information
     */
    @Data
    public static class TableInfo {
        private String tableName;
        private String className;
        private String tableComment;
        private String primaryKeyType;
        private String primaryKeyColumn;
        private String primaryKeyColumnName;
        private String primaryKeyPropertyName;
        private List<ColumnMetadata> columns;
        private Set<String> imports;
    }
    
    /**
     * Configuration information (from TechStackConfig)
     */
    @Data
    public static class ConfigInfo {
        private String ormFramework;
        private String buildTool;
        private String databaseType;
        private boolean useLombok;
        private boolean useSwagger;
        private boolean useValidation;
        private boolean usePagination;
        private boolean useRedis;
    }
    
    // Root level fields
    private ProjectInfo project;
    private TableInfo table;
    private ConfigInfo config;
    
    // Additional helper fields for backward compatibility
    private String packageName;
    private String projectName;
    private String className;
    private TechStackConfig techStack;
    
    /**
     * Build RootModel for project-level files (no table context)
     */
    public static RootModel forProject(ProjectMetadata projectMetadata) {
        RootModel root = new RootModel();
        TechStackConfig techStack = projectMetadata.getTechStack();
        
        // Build project info
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setPackageName(projectMetadata.getPackageName());
        projectInfo.setProjectName(projectMetadata.getProjectName());
        projectInfo.setJavaVersion(techStack.getJavaVersion());
        projectInfo.setSpringBootVersion(techStack.getSpringBootVersion());
        
        // Handle project name with hyphens (e.g., ming-mall-system -> MingMallSystem)
        String safeProjectName = projectMetadata.getProjectName().replace("-", "_");
        String appBaseName = com.ming.s2s.common.utils.NamingUtils.toClassName(safeProjectName);
        projectInfo.setClassName(appBaseName);
        
        root.project = projectInfo;
        
        // Build config info
        ConfigInfo configInfo = buildConfigInfo(techStack);
        root.config = configInfo;
        
        // Backward compatibility fields
        root.packageName = projectMetadata.getPackageName();
        root.projectName = projectMetadata.getProjectName();
        root.className = appBaseName;
        root.techStack = techStack;
        
        return root;
    }
    
    /**
     * Build RootModel for table-level files (with table context)
     */
    public static RootModel forTable(ProjectMetadata projectMetadata, TableMetadata table) {
        RootModel root = forProject(projectMetadata);
        TechStackConfig techStack = projectMetadata.getTechStack();
        
        // Build table info
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(table.getTableName());
        tableInfo.setClassName(table.getClassName());
        tableInfo.setTableComment(table.getTableComment());
        tableInfo.setPrimaryKeyType(table.getPrimaryKeyType());
        tableInfo.setPrimaryKeyColumn(table.getPrimaryKeyColumn());
        tableInfo.setColumns(table.getColumns());
        
        // Build imports set (add validation and swagger imports if needed)
        Set<String> imports = new HashSet<>(table.getImports());
        
        // Add Swagger imports if enabled
        if (techStack.isUseSwagger()) {
            imports.add("io.swagger.v3.oas.annotations.media.Schema");
        }
        
        // Add validation imports if enabled
        if (techStack.isUseValidation()) {
            // Check if any column needs validation annotations
            boolean needsValidation = table.getColumns().stream()
                .anyMatch(col -> !col.isPrimaryKey() && (!col.isNullable() || col.getMaxLength() != null));
            if (needsValidation) {
                imports.add("jakarta.validation.constraints.NotNull");
                imports.add("jakarta.validation.constraints.Size");
            }
        }
        
        tableInfo.setImports(imports);
        
        // Find primary key column for convenience
        table.getColumns().stream()
            .filter(col -> col.isPrimaryKey())
            .findFirst()
            .ifPresent(pkCol -> {
                tableInfo.setPrimaryKeyColumnName(pkCol.getColumnName());
                tableInfo.setPrimaryKeyPropertyName(pkCol.getPropertyName());
            });
        
        root.table = tableInfo;
        
        // Update backward compatibility fields for table context
        root.className = table.getClassName();
        
        return root;
    }
    
    /**
     * Build ConfigInfo from TechStackConfig
     */
    private static ConfigInfo buildConfigInfo(TechStackConfig techStack) {
        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setOrmFramework(techStack.getOrmFramework());
        configInfo.setBuildTool(techStack.getBuildTool());
        configInfo.setDatabaseType(techStack.getDatabaseType());
        configInfo.setUseLombok(techStack.isUseLombok());
        configInfo.setUseSwagger(techStack.isUseSwagger());
        configInfo.setUseValidation(techStack.isUseValidation());
        configInfo.setUsePagination(techStack.isUsePagination());
        configInfo.setUseRedis(techStack.isUseRedis());
        return configInfo;
    }
    
    /**
     * Convert RootModel to Map for FreeMarker
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        // Add structured data
        map.put("project", project);
        map.put("table", table);
        map.put("config", config);
        
        // Add backward compatibility fields (flat structure)
        map.put("packageName", packageName);
        map.put("projectName", projectName);
        map.put("className", className);
        map.put("techStack", techStack);
        
        // Add table-specific fields if table exists
        if (table != null) {
            map.put("tableName", table.getTableName());
            map.put("tableComment", table.getTableComment());
            map.put("primaryKeyType", table.getPrimaryKeyType());
            map.put("primaryKeyColumn", table.getPrimaryKeyColumn());
            map.put("primaryKeyColumnName", table.getPrimaryKeyColumnName());
            map.put("primaryKeyPropertyName", table.getPrimaryKeyPropertyName());
            map.put("columns", table.getColumns());
            map.put("imports", table.getImports());
            map.put("classNameUncap", com.ming.s2s.common.utils.NamingUtils.toPropertyName(table.getClassName()));
        }
        
        return map;
    }
}

