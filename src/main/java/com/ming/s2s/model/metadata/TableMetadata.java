package com.ming.s2s.model.metadata;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Table metadata - unified model for all database types
 */
@Data
public class TableMetadata {
    private String dbType;           // Database type (mysql, postgresql, oracle, etc.)
    private String tableName;        // Raw table name
    private String className;         // Camel case class name
    private String tableComment;      // Table comment
    private String primaryKeyType = "Long"; // Primary key type, default Long
    private String primaryKeyColumn; // Primary key column name

    private List<ColumnMetadata> columns = new ArrayList<>();
    private Set<String> imports = new HashSet<>();
}

