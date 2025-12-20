package com.ming.s2s.core.parser;

import com.ming.s2s.model.metadata.TableMetadata;

import java.util.List;

/**
 * SQL parser strategy interface
 * Parse SQL statements into table metadata
 */
public interface SqlParser {

    /**
     * Check if this parser supports the given database type
     *
     * @param dbType Database type (mysql, postgresql, oracle, etc.)
     * @return true if supported, false otherwise
     */
    boolean supports(String dbType);

    /**
     * Parse SQL string into table metadata list
     *
     * @param sql SQL string (can contain multiple CREATE TABLE statements)
     * @param dbType Database type for context
     * @return List of table metadata
     */
    List<TableMetadata> parse(String sql, String dbType);
}
