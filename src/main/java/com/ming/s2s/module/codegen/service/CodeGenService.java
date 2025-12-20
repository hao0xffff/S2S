package com.ming.s2s.module.codegen.service;

import com.ming.s2s.common.utils.FileUtils;
import com.ming.s2s.common.validation.InputValidator;
import com.ming.s2s.common.validation.PathValidator;
import com.ming.s2s.core.generator.CodeGenerator;
import com.ming.s2s.core.generator.GeneratorFactory;
import com.ming.s2s.core.parser.SqlParser;
import com.ming.s2s.core.parser.SqlParserFactory;
import com.ming.s2s.model.metadata.ProjectMetadata;
import com.ming.s2s.model.metadata.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Code generation service
 * Uses Factory pattern to assemble Parser and Generator
 */
@Slf4j
@Service
public class CodeGenService {

    @Autowired
    private SqlParserFactory sqlParserFactory;

    @Autowired
    private GeneratorFactory generatorFactory;

    @Value("${s2s.output.base-dir:D:/S2S_Output}")
    private String allowedBaseDir;

    /**
     * Generate code from SQL
     *
     * @param sql SQL string
     * @param projectMetadata Project metadata (contains tech stack config with database type)
     * @return Map of filename -> code content
     */
    public Map<String, String> generateCode(String sql, ProjectMetadata projectMetadata) {
        // Get database type from tech stack config
        String dbType = projectMetadata.getTechStack().getDatabaseType();
        if (dbType == null || dbType.trim().isEmpty()) {
            dbType = "mysql"; // Default to MySQL
        }

        // 1. Get parser strategy for database type (Strategy pattern via Factory)
        SqlParser parser = sqlParserFactory.getParser(dbType);
        log.info("Using parser strategy for database type: {}", dbType);

        // 2. Parse SQL to metadata
        List<TableMetadata> tableMetadataList = parser.parse(sql, dbType);

        // Validate table count
        InputValidator.validateTableCount(tableMetadataList.size());

        // 3. Get generator strategy for technology stack (Strategy pattern via Factory)
        CodeGenerator generator = generatorFactory.getGenerator(projectMetadata.getTechStack());
        log.info("Using generator strategy for tech stack: {} + {}", 
            projectMetadata.getTechStack().getOrmFramework(),
            projectMetadata.getTechStack().getBuildTool());

        // 4. Generate code from metadata
        Map<String, String> codeFiles = generator.generate(projectMetadata, tableMetadataList);

        log.info("Generated {} files for project: {}", codeFiles.size(), projectMetadata.getProjectName());
        return codeFiles;
    }

    /**
     * Write generated code to disk
     *
     * @param codeFiles Map of filename -> code content
     * @param projectMetadata Project metadata
     */
    public void writeToDisk(Map<String, String> codeFiles, ProjectMetadata projectMetadata) {
        // Create path validator with allowed base directory
        PathValidator pathValidator = new PathValidator(allowedBaseDir);

        // Validate output directory is within allowed base
        String baseDir = Paths.get(projectMetadata.getOutputDir(), projectMetadata.getProjectName()).toString();
        
        try {
            // Use path validator to ensure security
            String packagePath = projectMetadata.getPackageName().replace(".", "/");
            for (Map.Entry<String, String> entry : codeFiles.entrySet()) {
                String relativePath = FileUtils.determineRelativePath(entry.getKey(), packagePath);
                pathValidator.validatePath(projectMetadata.getOutputDir(), 
                    projectMetadata.getProjectName() + "/" + relativePath);
            }

            // Write files with validation
            FileUtils.writeFiles(codeFiles, baseDir, projectMetadata.getPackageName(), pathValidator);
            log.info("Code files written to: {}", baseDir);
        } catch (Exception e) {
            log.error("Failed to write files to disk", e);
            throw new RuntimeException("Failed to write files to disk: " + e.getMessage(), e);
        }
    }

    /**
     * Pack generated project to ZIP file
     *
     * @param projectName Project name
     * @param outputDir Output directory
     * @return ZIP file as byte array
     */
    public byte[] packProjectToZip(String projectName, String outputDir) {
        // Create path validator with allowed base directory
        PathValidator pathValidator = new PathValidator(allowedBaseDir);
        
        // Build project directory path
        String projectDir = Paths.get(outputDir, projectName).toString();
        
        try {
            // Validate and pack
            return FileUtils.packProjectToZip(projectDir, projectName, pathValidator);
        } catch (Exception e) {
            log.error("Failed to pack project to ZIP: {}", projectDir, e);
            throw new RuntimeException("Failed to pack project to ZIP: " + e.getMessage(), e);
        }
    }
}

