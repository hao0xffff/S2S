package com.ming.s2s.model.context;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Data
public class TableContext {
    private String tableName;        // 原生表名
    private String className;        // 驼峰类名
    private String tableComment;     // 表注释

    // --- 补全这个字段 ---
    private String primaryKeyType = "Long"; // 默认主键类型为 Long

    private List<ColumnContext> columns = new ArrayList<>();
    private Set<String> imports = new HashSet<>();
}