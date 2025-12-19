package com.ming.s2s.model.context;

import lombok.Data;

@Data
public class ColumnContext {
    private String columnName;   // 数据库原始列名 (如: user_id)
    private String propertyName; // Java 驼峰属性名 (如: userId)
    private String javaType;     // Java 类型 (如: Long)
    private String comment;      // 注释
}