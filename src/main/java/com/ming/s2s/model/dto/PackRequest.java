package com.ming.s2s.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for packing project to ZIP
 */
@Data
public class PackRequest {
    
    @NotBlank(message = "项目名称不能为空")
    private String projectName;
    
    @NotBlank(message = "输出目录不能为空")
    private String outputDir;
}

