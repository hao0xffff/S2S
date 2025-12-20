package com.ming.s2s.core.parser;

import com.ming.s2s.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for creating SQL parser strategies based on database type
 */
@Slf4j
@Component
public class SqlParserFactory {

    private final List<SqlParser> parsers;

    @Autowired
    public SqlParserFactory(List<SqlParser> parsers) {
        this.parsers = parsers;
        log.info("Initialized SqlParserFactory with {} parser(s)", parsers.size());
    }

    /**
     * Get parser strategy for the given database type
     *
     * @param dbType Database type (mysql, postgresql, oracle, etc.)
     * @return SqlParser strategy implementation
     * @throws BusinessException if no parser found for the database type
     */
    public SqlParser getParser(String dbType) {
        if (dbType == null || dbType.trim().isEmpty()) {
            dbType = "mysql"; // Default to MySQL
        }

        String normalizedDbType = dbType.toLowerCase().trim();

        return parsers.stream()
            .filter(parser -> parser.supports(normalizedDbType))
            .findFirst()
            .orElseThrow(() -> new BusinessException(
                String.format("No SQL parser found for database type: %s. Supported types: mysql, postgresql", normalizedDbType)
            ));
    }

    /**
     * Get all supported database types
     *
     * @return List of supported database types
     */
    public List<String> getSupportedDbTypes() {
        return parsers.stream()
            .map(parser -> {
                // Try common database types to find what this parser supports
                String[] commonTypes = {"mysql", "postgresql", "oracle", "sqlserver"};
                for (String type : commonTypes) {
                    if (parser.supports(type)) {
                        return type;
                    }
                }
                return "unknown";
            })
            .filter(type -> !"unknown".equals(type))
            .distinct()
            .toList();
    }
}
