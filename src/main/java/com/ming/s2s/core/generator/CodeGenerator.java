package com.ming.s2s.core.generator;

import com.ming.s2s.model.metadata.ProjectMetadata;
import com.ming.s2s.model.metadata.TableMetadata;

import java.util.Map;

/**
 * Code generator strategy interface
 * Generate code strings from metadata
 */
public interface CodeGenerator {

    /**
     * Check if this generator supports the given technology stack
     *
     * @param ormFramework ORM framework (mybatis, jpa, mybatis-plus, etc.)
     * @param buildTool Build tool (maven, gradle)
     * @return true if supported, false otherwise
     */
    boolean supports(String ormFramework, String buildTool);

    /**
     * Generate all code files
     *
     * @param projectMetadata Project metadata
     * @param tableMetadataList List of table metadata
     * @return Map of filename -> code content
     */
    Map<String, String> generate(ProjectMetadata projectMetadata, java.util.List<TableMetadata> tableMetadataList);
}

