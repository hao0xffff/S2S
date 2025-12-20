package com.ming.s2s.module.codegen.controller;

import com.ming.s2s.common.result.Result;
import com.ming.s2s.model.dto.GenRequest;
import com.ming.s2s.model.dto.PackRequest;
import com.ming.s2s.model.metadata.ProjectMetadata;
import com.ming.s2s.module.codegen.service.CodeGenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Code generation controller
 * Expose API for code generation
 */
@Slf4j
@RestController
@RequestMapping("/api/s2s")
public class CodeGenController {

    @Autowired
    private CodeGenService codeGenService;

    /**
     * Generate SpringBoot project from SQL
     *
     * @param request Generation request
     * @return Generated code preview
     */
    @PostMapping("/generate")
    public Result<Map<String, String>> generateProject(@Valid @RequestBody GenRequest request) {
        try {
            // Validate input
            request.validate();

            // 1. Build project metadata
            ProjectMetadata projectMetadata = new ProjectMetadata();
            projectMetadata.setProjectName(request.getProjectName());
            projectMetadata.setPackageName(request.getPackageName());
            projectMetadata.setOutputDir(request.getOutputDir());
            // Set tech stack config (use default if not provided)
            if (request.getTechStack() != null) {
                projectMetadata.setTechStack(request.getTechStack());
            }
            
            // Set database type in tech stack if provided in request
            if (request.getDbType() != null && !request.getDbType().trim().isEmpty()) {
                projectMetadata.getTechStack().setDatabaseType(request.getDbType());
            }

            // 2. Generate code (using factory pattern for parser and generator strategies)
            Map<String, String> codeFiles = codeGenService.generateCode(
                request.getSql(), 
                projectMetadata
            );

            // 3. Write to disk
            codeGenService.writeToDisk(codeFiles, projectMetadata);

            // 4. Return preview
            return Result.success(codeFiles);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input: {}", e.getMessage());
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            log.error("Code generation failed", e);
            // Don't expose internal error details in production
            return Result.failed("代码生成失败，请检查输入参数");
        }
    }

    /**
     * Pack generated project to ZIP file (POST with JSON body)
     *
     * @param request Pack request containing project name and output directory
     * @return ZIP file as ResponseEntity
     */
    @PostMapping("/pack")
    public ResponseEntity<byte[]> packProject(@Valid @RequestBody PackRequest request) {
        try {
            // Pack project to ZIP
            byte[] zipBytes = codeGenService.packProjectToZip(
                request.getProjectName(),
                request.getOutputDir()
            );

            // Set response headers for file download
            String fileName = request.getProjectName() + ".zip";
            
            // Encode filename for Content-Disposition header (RFC 5987 format for UTF-8)
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
            String utf8FileName = "filename*=UTF-8''" + encodedFileName;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Use both formats for better compatibility
            headers.set("Content-Disposition", 
                String.format("attachment; filename=\"%s\"; %s", fileName, utf8FileName));
            headers.setContentLength(zipBytes.length);
            // Prevent caching
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("Project packed to ZIP: {} ({} bytes)", fileName, zipBytes.length);
            return ResponseEntity.ok()
                .headers(headers)
                .body(zipBytes);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid pack request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to pack project", e);
            throw new RuntimeException("打包项目失败: " + e.getMessage(), e);
        }
    }

    /**
     * Pack generated project to ZIP file (GET with query parameters)
     * Alternative endpoint for easier testing
     *
     * @param projectName Project name
     * @param outputDir Output directory
     * @return ZIP file as ResponseEntity
     */
    @GetMapping("/pack")
    public ResponseEntity<byte[]> packProjectGet(
            @RequestParam String projectName,
            @RequestParam String outputDir) {
        try {
            // Pack project to ZIP
            byte[] zipBytes = codeGenService.packProjectToZip(projectName, outputDir);

            // Set response headers for file download
            String fileName = projectName + ".zip";
            
            // Encode filename for Content-Disposition header (RFC 5987 format for UTF-8)
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
            String utf8FileName = "filename*=UTF-8''" + encodedFileName;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Use both formats for better compatibility
            headers.set("Content-Disposition", 
                String.format("attachment; filename=\"%s\"; %s", fileName, utf8FileName));
            headers.setContentLength(zipBytes.length);
            // Prevent caching
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("Project packed to ZIP (GET): {} ({} bytes)", fileName, zipBytes.length);
            return ResponseEntity.ok()
                .headers(headers)
                .body(zipBytes);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid pack request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to pack project", e);
            throw new RuntimeException("打包项目失败: " + e.getMessage(), e);
        }
    }
}

