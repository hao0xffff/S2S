package com.ming.s2s.model.vo;

import lombok.Data;

import java.util.Map;

/**
 * Code preview VO
 */
@Data
public class PreviewVO {
    private String projectName;
    private Map<String, String> files; // filename -> code content
}

