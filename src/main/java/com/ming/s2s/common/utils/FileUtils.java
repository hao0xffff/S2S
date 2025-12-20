package com.ming.s2s.common.utils;

import com.ming.s2s.common.validation.InputValidator;
import com.ming.s2s.common.validation.PathValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File utility class
 */
public class FileUtils {

    /**
     * Write code files to disk
     */
    public static void writeFiles(Map<String, String> codes, String baseDir, String packageName, PathValidator pathValidator) throws IOException {
        // Validate file count
        if (codes.size() > 1000) {
            throw new IllegalArgumentException("Too many files to generate (max 1000)");
        }

        String packagePath = packageName.replace(".", "/");

        for (Map.Entry<String, String> entry : codes.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            // Sanitize filename
            String sanitizedFileName = PathValidator.sanitizeFileName(fileName);

            // Determine relative path based on file type (creates proper directory structure)
            String relativePath = determineRelativePath(sanitizedFileName, packagePath);

            // Validate file size
            InputValidator.validateFileSize(content.getBytes(StandardCharsets.UTF_8).length);

            // Validate and get safe path (use relative path to create proper folder structure)
            Path finalPath = pathValidator.validatePath(baseDir, relativePath);

            // Create parent directories if they don't exist
            Files.createDirectories(finalPath.getParent());
            
            // Write file content
            Files.write(finalPath, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Determine relative path for a file based on its name
     */
    public static String determineRelativePath(String fileName, String packagePath) {
        if (fileName.equals("pom.xml")) {
            return fileName;
        } else if (fileName.equals("application.properties")) {
            return "src/main/resources/" + fileName;
        } else if (fileName.endsWith("Mapper.xml")) {
            return "src/main/resources/mapper/" + fileName;
        } else {
            String subPackage = determineSubPackage(fileName);
            if (subPackage.isEmpty()) {
                return "src/main/java/" + packagePath + "/" + fileName;
            } else {
                return "src/main/java/" + packagePath + "/" + subPackage + "/" + fileName;
            }
        }
    }

    private static String determineSubPackage(String fileName) {
        // Check in order of specificity (most specific first)
        if (fileName.equals("Result.java") || fileName.equals("ResultCode.java")) {
            return "common/api";
        }
        if (fileName.equals("OpenApiConfig.java")) {
            return "config";  // Swagger/OpenAPI configuration
        }
        if (fileName.endsWith("ServiceImpl.java")) {
            return "service/impl";
        }
        if (fileName.endsWith("Repository.java")) {
            return "repository";  // For JPA
        }
        if (fileName.endsWith("Mapper.java")) {
            return "mapper";
        }
        if (fileName.endsWith("Controller.java")) {
            return "controller";
        }
        if (fileName.endsWith("Service.java")) {
            return "service";
        }
        if (fileName.endsWith("Application.java")) {
            return "";  // Root package
        }
        // Default: entity classes
        return "entity";
    }

    /**
     * Pack project directory to ZIP file
     *
     * @param projectDir Project directory path
     * @param projectName Project name (used as ZIP file name prefix)
     * @param pathValidator Path validator for security
     * @return ZIP file as byte array
     * @throws IOException if file operations fail
     */
    public static byte[] packProjectToZip(String projectDir, String projectName, PathValidator pathValidator) throws IOException {
        Path projectPath = Paths.get(projectDir);
        
        // Validate project directory exists
        if (!Files.exists(projectPath) || !Files.isDirectory(projectPath)) {
            throw new IllegalArgumentException("Project directory does not exist: " + projectDir);
        }

        // Validate path is within allowed base directory
        pathValidator.validatePath(projectDir, "");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
            // Walk through all files in the project directory
            try (var paths = Files.walk(projectPath)) {
                paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            // Get relative path from project root
                            Path relativePath = projectPath.relativize(file);
                            String entryName = relativePath.toString().replace("\\", "/");
                            
                            // Create ZIP entry
                            ZipEntry zipEntry = new ZipEntry(entryName);
                            zos.putNextEntry(zipEntry);
                            
                            // Write file content to ZIP
                            Files.copy(file, zos);
                            
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to add file to ZIP: " + file, e);
                        }
                    });
            }
        }
        
        return baos.toByteArray();
    }
}

