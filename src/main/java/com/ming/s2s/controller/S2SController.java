package com.ming.s2s.controller;

import com.ming.s2s.core.S2SEngine;
import com.ming.s2s.model.context.ProjectContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/s2s")
public class S2SController {

    @Autowired
    private S2SEngine s2SEngine;

    /**
     * 一键生成 SpringBoot 全家桶项目
     */
    @PostMapping("/generate")
    public Map<String, String> generateProject(@RequestBody GenRequest req) {
        try {
            // 1. 组装项目上下文配置
            ProjectContext project = new ProjectContext();
            project.setProjectName(req.getProjectName());
            project.setPackageName(req.getPackageName());
            project.setOutputDir(req.getOutputDir());

            // 2. 调用引擎生成代码内容（Map 中包含所有文件内容）
            Map<String, String> result = s2SEngine.generateAll(req.getSql(), project);

            // 3. 落地到本地磁盘
            s2SEngine.writeToDisk(result, project);

            // 4. 返回 JSON 结果供预览
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "生成失败: " + e.getMessage());
        }
    }

    /**
     * 接收参数的内部类
     */
    @Data
    public static class GenRequest {
        private String sql;         // 建表 SQL
        private String projectName; // 项目名 (如: order-center)
        private String packageName; // 基础包名 (如: com.ming.order)
        private String outputDir;   // 输出根目录 (如: D:/S2S_Output)
    }
}