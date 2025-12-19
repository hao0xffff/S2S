package com.ming.s2s.core.converter;

import java.util.HashMap;
import java.util.Map;

public class TypeConverter {
    private static final Map<String, String> TYPE_MAP = new HashMap<>();
    private static final Map<String, String> IMPORT_MAP = new HashMap<>();

    static {
        // 1. 数据库类型 -> Java 类型
        TYPE_MAP.put("varchar", "String");
        TYPE_MAP.put("char", "String");
        TYPE_MAP.put("text", "String");
        TYPE_MAP.put("int", "Integer");
        TYPE_MAP.put("bigint", "Long");
        TYPE_MAP.put("decimal", "BigDecimal");
        TYPE_MAP.put("datetime", "LocalDateTime");
        TYPE_MAP.put("date", "LocalDate");
        TYPE_MAP.put("timestamp", "LocalDateTime");

        // 2. Java 类型 -> 需要导入的完整路径 (String, Long 等 java.lang 包下的不需要写)
        IMPORT_MAP.put("BigDecimal", "java.math.BigDecimal");
        IMPORT_MAP.put("LocalDateTime", "java.time.LocalDateTime");
        IMPORT_MAP.put("LocalDate", "java.time.LocalDate");
    }

    public static String convert(String dbType) {
        return TYPE_MAP.getOrDefault(dbType.toLowerCase(), "Object");
    }

    // 新增：根据 Java 类型获取对应的导包路径
    public static String getImport(String javaType) {
        return IMPORT_MAP.get(javaType);
    }
}