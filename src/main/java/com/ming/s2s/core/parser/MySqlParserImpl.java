package com.ming.s2s.core.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.ming.s2s.common.exception.BusinessException;
import com.ming.s2s.common.utils.NamingUtils;
import com.ming.s2s.core.converter.TypeConverter;
import com.ming.s2s.model.metadata.ColumnMetadata;
import com.ming.s2s.model.metadata.TableMetadata;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MySQL SQL parser strategy implementation
 */
@Component
public class MySqlParserImpl implements SqlParser {

    private static final String SUPPORTED_DB_TYPE = "mysql";

    @Override
    public boolean supports(String dbType) {
        return SUPPORTED_DB_TYPE.equalsIgnoreCase(dbType);
    }

    @Override
    public List<TableMetadata> parse(String sql, String dbType) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new BusinessException("SQL cannot be empty");
        }

        // Parse SQL statements using Druid
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, DbType.mysql);

        if (statements == null || statements.isEmpty()) {
            throw new BusinessException("Failed to parse SQL statements, please check SQL format");
        }

        List<TableMetadata> tableMetadataList = new ArrayList<>();

        // Extract CREATE TABLE statements
        for (SQLStatement statement : statements) {
            if (statement instanceof MySqlCreateTableStatement createTableStmt) {
                TableMetadata tableMetadata = parseTable(createTableStmt);
                tableMetadataList.add(tableMetadata);
            }
        }

        if (tableMetadataList.isEmpty()) {
            throw new BusinessException("No CREATE TABLE statements found in SQL");
        }

        return tableMetadataList;
    }

    private TableMetadata parseTable(MySqlCreateTableStatement stmt) {
        TableMetadata table = new TableMetadata();
        
        // Parse table name
        // Remove all quotes (backticks, double quotes, single quotes) and trim whitespace
        String rawTableName = stmt.getTableName().toString()
            .replace("`", "")
            .replace("\"", "")
            .replace("'", "")
            .trim();
        table.setTableName(rawTableName);
        
        // Ensure className is clean (no quotes)
        String className = NamingUtils.toClassName(rawTableName);
        // Additional safety: remove any remaining quotes
        className = className.replace("\"", "").replace("'", "").replace("`", "").trim();
        table.setClassName(className);
        table.setTableComment(
            stmt.getComment() != null ? stmt.getComment().toString().replace("'", "") : ""
        );
        table.setPrimaryKeyType("Long");

        // Parse columns and identify primary keys
        List<ColumnMetadata> columns = new ArrayList<>();
        Set<String> imports = new HashSet<>();

        for (SQLTableElement element : stmt.getTableElementList()) {
            if (element instanceof SQLColumnDefinition colDef) {
                ColumnMetadata column = parseColumn(colDef);
                
                // Check if this column is a primary key
                // Primary key can be defined in column definition (e.g., id INT PRIMARY KEY)
                String colDefStr = colDef.toString().toUpperCase();
                boolean isPrimaryKey = colDefStr.contains("PRIMARY KEY");
                column.setPrimaryKey(isPrimaryKey);
                
                if (isPrimaryKey) {
                    table.setPrimaryKeyType(column.getJavaType());
                    table.setPrimaryKeyColumn(column.getColumnName());
                }
                
                columns.add(column);

                // Collect imports
                String importPath = TypeConverter.getImport(column.getJavaType());
                if (importPath != null) {
                    imports.add(importPath);
                }
            }
        }

        table.setColumns(columns);
        table.setImports(imports);
        return table;
    }

    private ColumnMetadata parseColumn(SQLColumnDefinition colDef) {
        ColumnMetadata column = new ColumnMetadata();
        
        // Remove all quotes and trim whitespace
        String rawColName = colDef.getName().toString()
            .replace("`", "")
            .replace("\"", "")
            .replace("'", "")
            .trim();
        column.setColumnName(rawColName);
        
        // Ensure propertyName is clean
        String propertyName = NamingUtils.toPropertyName(rawColName);
        propertyName = propertyName.replace("\"", "").replace("'", "").replace("`", "").trim();
        column.setPropertyName(propertyName);

        // Parse data type
        String dbTypeName = colDef.getDataType().getName();
        column.setRawDbType(extractRawDbType(colDef));
        
        // Extract length/precision
        String lengthStr = extractLength(colDef);
        column.setLength(lengthStr);
        column.setMaxLength(parseMaxLength(lengthStr));

        // Convert database type to Java type
        String javaType = TypeConverter.convert(dbTypeName);
        column.setJavaType(javaType);

        // Parse nullable (default is true, unless NOT NULL is specified)
        boolean nullable = !colDef.containsNotNullConstaint();
        column.setNullable(nullable);

        // Parse default value
        if (colDef.getDefaultExpr() != null) {
            String defaultValue = colDef.getDefaultExpr().toString()
                .replace("'", "")
                .replace("\"", "");
            column.setDefaultValue(defaultValue);
        }

        // Parse comment
        column.setComment(
            colDef.getComment() != null ? colDef.getComment().toString().replace("'", "") : ""
        );

        return column;
    }

    /**
     * Extract raw database type with length (e.g., varchar(255), int(11))
     */
    private String extractRawDbType(SQLColumnDefinition colDef) {
        StringBuilder sb = new StringBuilder();
        sb.append(colDef.getDataType().getName());
        
        // Extract length/precision
        if (colDef.getDataType().getArguments() != null && !colDef.getDataType().getArguments().isEmpty()) {
            sb.append("(");
            for (int i = 0; i < colDef.getDataType().getArguments().size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(colDef.getDataType().getArguments().get(i).toString());
            }
            sb.append(")");
        }
        
        return sb.toString();
    }

    /**
     * Extract length string (e.g., "255", "10,2")
     */
    private String extractLength(SQLColumnDefinition colDef) {
        if (colDef.getDataType().getArguments() == null || colDef.getDataType().getArguments().isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colDef.getDataType().getArguments().size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(colDef.getDataType().getArguments().get(i).toString());
        }
        return sb.toString();
    }

    /**
     * Parse max length from length string (e.g., "255" -> 255, "10,2" -> null)
     */
    private Integer parseMaxLength(String lengthStr) {
        if (lengthStr == null || lengthStr.trim().isEmpty()) {
            return null;
        }
        
        // Only parse if it's a single number (not precision like "10,2")
        if (!lengthStr.contains(",")) {
            try {
                return Integer.parseInt(lengthStr.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
}

