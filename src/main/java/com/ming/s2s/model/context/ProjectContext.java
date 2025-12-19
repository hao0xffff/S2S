package com.ming.s2s.model.context;

import lombok.Data;

@Data
public class ProjectContext {
    private String projectName;   // 项目名，如：order-service
    private String packageName;   // 包名，如：com.ming.test
    private String outputDir;     // 输出根目录，如：D:/S2S_Output
}