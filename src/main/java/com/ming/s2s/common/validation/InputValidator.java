package com.ming.s2s.common.validation;

import java.util.regex.Pattern;

/**
 * Input validator for code generation requests
 */
public class InputValidator {

    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$");
    private static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9._-]*[a-z0-9]$|^[a-z0-9]$");
    private static final int MAX_SQL_LENGTH = 10 * 1024 * 1024; // 10MB
    private static final int MAX_TABLES = 100;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB per file

    /**
     * Validate package name
     */
    public static void validatePackageName(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be null or empty");
        }
        if (!PACKAGE_NAME_PATTERN.matcher(packageName.toLowerCase()).matches()) {
            throw new IllegalArgumentException("Invalid package name format: " + packageName);
        }
        if (packageName.length() > 100) {
            throw new IllegalArgumentException("Package name too long (max 100 characters)");
        }
    }

    /**
     * Validate project name
     */
    public static void validateProjectName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        if (!PROJECT_NAME_PATTERN.matcher(projectName.toLowerCase()).matches()) {
            throw new IllegalArgumentException("Invalid project name format: " + projectName);
        }
        if (projectName.length() > 50) {
            throw new IllegalArgumentException("Project name too long (max 50 characters)");
        }
    }

    /**
     * Validate SQL content
     */
    public static void validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be null or empty");
        }
        if (sql.length() > MAX_SQL_LENGTH) {
            throw new IllegalArgumentException("SQL too large (max " + MAX_SQL_LENGTH / 1024 / 1024 + "MB)");
        }
    }

    /**
     * Validate output directory
     */
    public static void validateOutputDir(String outputDir) {
        if (outputDir == null || outputDir.trim().isEmpty()) {
            throw new IllegalArgumentException("Output directory cannot be null or empty");
        }
        // Check for path traversal attempts
        if (outputDir.contains("..") || outputDir.contains("~")) {
            throw new IllegalArgumentException("Invalid output directory: path traversal detected");
        }
    }

    /**
     * Validate table count
     */
    public static void validateTableCount(int count) {
        if (count > MAX_TABLES) {
            throw new IllegalArgumentException("Too many tables (max " + MAX_TABLES + ")");
        }
    }

    /**
     * Validate file size
     */
    public static void validateFileSize(int size) {
        if (size > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large (max " + MAX_FILE_SIZE / 1024 / 1024 + "MB)");
        }
    }
}

