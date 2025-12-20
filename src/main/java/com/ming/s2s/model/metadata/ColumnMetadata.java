package com.ming.s2s.model.metadata;

import lombok.Data;

/**
 * Column metadata - unified model for all database types
 * Enhanced with validation-related fields for code generation
 */
@Data
public class ColumnMetadata {
    private String dbType;        // Database type (mysql, postgresql, oracle, etc.)
    private String columnName;    // Database raw column name (e.g., user_id)
    private String propertyName;  // Java camel case property name (e.g., userId)
    private String rawDbType;     // Raw database type with length (e.g., varchar(255), int(11))
    private String javaType;      // Java type (e.g., Long)
    private String length;        // Column length/precision (e.g., "255", "10,2")
    private Integer maxLength;    // Maximum length for validation (e.g., 255 for varchar(255))
    private boolean nullable = true; // Is nullable
    private boolean primaryKey = false; // Is primary key
    private String defaultValue;  // Default value
    private String comment;        // Comment
    
    /**
     * Check if this column needs validation annotations
     * @return true if column is not nullable or has max length
     */
    public boolean needsValidation() {
        return !nullable || maxLength != null;
    }
}

