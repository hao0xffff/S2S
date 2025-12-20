package com.ming.s2s.common.validation;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Path validator to prevent path traversal attacks
 */
public class PathValidator {

    private final Path allowedBaseDir;

    public PathValidator(String allowedBaseDir) {
        this.allowedBaseDir = Paths.get(allowedBaseDir).normalize().toAbsolutePath();
    }

    /**
     * Validate and normalize path to prevent path traversal
     *
     * @param baseDir Base directory
     * @param relativePath Relative path to validate
     * @return Normalized absolute path
     * @throws SecurityException if path traversal detected
     */
    public Path validatePath(String baseDir, String relativePath) {
        try {
            Path base = Paths.get(baseDir).normalize().toAbsolutePath();
            
            // Ensure base directory is within allowed directory
            if (!base.startsWith(allowedBaseDir)) {
                throw new SecurityException("Base directory is outside allowed path: " + baseDir);
            }

            Path fullPath = base.resolve(relativePath).normalize().toAbsolutePath();

            // Ensure resolved path is still within allowed directory
            if (!fullPath.startsWith(allowedBaseDir)) {
                throw new SecurityException("Path traversal detected: " + relativePath);
            }

            return fullPath;
        } catch (Exception e) {
            if (e instanceof SecurityException) {
                throw e;
            }
            throw new SecurityException("Invalid path: " + relativePath, e);
        }
    }

    /**
     * Sanitize filename to prevent injection
     *
     * @param fileName Original filename
     * @return Sanitized filename
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        // Remove path separators and special characters
        String sanitized = fileName
            .replaceAll("[\\\\/:*?\"<>|]", "_")  // Windows illegal chars
            .replaceAll("\\.\\.", "_")  // Path traversal
            .trim();

        // Check for Windows reserved names
        String upperName = sanitized.toUpperCase();
        String[] reservedNames = {"CON", "PRN", "AUX", "NUL", 
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        
        for (String reserved : reservedNames) {
            if (upperName.equals(reserved) || upperName.startsWith(reserved + ".")) {
                sanitized = "_" + sanitized;
                break;
            }
        }

        // Limit filename length (Windows max is 255)
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }

        return sanitized;
    }
}

