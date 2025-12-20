# RootModel 全局数据模型使用指南

## 🎯 设计目标

创建统一的根数据模型（RootModel），提供结构化的、层次化的数据访问方式，让模板更清晰、更易维护。

## 📊 数据结构

RootModel 提供三层数据结构：

```json
{
  "project": {
    "packageName": "com.example",
    "projectName": "order-service",
    "className": "OrderServiceApplication",
    "javaVersion": "17",
    "springBootVersion": "3.1.5"
  },
  "table": {
    "tableName": "user",
    "className": "User",
    "tableComment": "用户表",
    "primaryKeyType": "Long",
    "primaryKeyColumn": "id",
    "primaryKeyColumnName": "id",
    "primaryKeyPropertyName": "id",
    "columns": [...],
    "imports": [...]
  },
  "config": {
    "ormFramework": "mybatis",
    "buildTool": "maven",
    "databaseType": "mysql",
    "useLombok": true,
    "useSwagger": false,
    "useValidation": true,
    "usePagination": false,
    "useRedis": false
  }
}
```

## 🔄 向后兼容

为了保持向后兼容，RootModel 同时提供**扁平结构**的字段：

```freemarker
${packageName}           # 等同于 ${project.packageName}
${projectName}           # 等同于 ${project.projectName}
${className}             # 等同于 ${table.className} 或 ${project.className}
${techStack}             # 完整的 TechStackConfig 对象
${tableName}             # 等同于 ${table.tableName}
${columns}               # 等同于 ${table.columns}
```

## 📝 模板使用示例

### 方式一：使用结构化访问（推荐）

```freemarker
package ${project.packageName}.entity;

<#if config.useLombok>
import lombok.Data;
</#if>

<#list table.imports as importPath>
import ${importPath};
</#list>

/**
 * ${table.tableComment}
 */
<#if config.useLombok>
@Data
</#if>
public class ${table.className} {
<#list table.columns as col>
    /**
     * ${col.comment}
     */
    <#if !col.nullable && config.useValidation>
    @NotNull(message = "${col.comment!col.propertyName}不能为空")
    </#if>
    <#if col.maxLength?? && col.javaType == "String" && config.useValidation>
    @Size(max = ${col.maxLength}, message = "${col.comment!col.propertyName}长度不能超过${col.maxLength}个字符")
    </#if>
    private ${col.javaType} ${col.propertyName};

</#list>
}
```

### 方式二：使用扁平结构（向后兼容）

```freemarker
package ${packageName}.entity;

<#if techStack.useLombok>
import lombok.Data;
</#if>

<#list imports as importPath>
import ${importPath};
</#list>

/**
 * ${tableComment}
 */
<#if techStack.useLombok>
@Data
</#if>
public class ${className} {
<#list columns as col>
    private ${col.javaType} ${col.propertyName};
</#list>
}
```

## 🎨 优势对比

### 使用 RootModel 结构化访问

**优点**：
- ✅ 数据层次清晰，易于理解
- ✅ 避免命名冲突（如 `className` 在 project 和 table 中都有）
- ✅ 配置访问更直观（`config.useLombok` vs `techStack.useLombok`）
- ✅ 更好的 IDE 提示支持

**示例**：
```freemarker
<#if config.useLombok>
@Data
</#if>

<#if config.useValidation && !col.nullable>
@NotNull
</#if>
```

### 使用扁平结构（向后兼容）

**优点**：
- ✅ 现有模板无需修改即可工作
- ✅ 更简洁的访问方式
- ✅ 符合传统模板习惯

**示例**：
```freemarker
<#if techStack.useLombok>
@Data
</#if>

<#if techStack.useValidation && !col.nullable>
@NotNull
</#if>
```

## 🔧 实现细节

### RootModel 构建

#### 项目级文件（无表上下文）
```java
RootModel rootModel = RootModel.forProject(projectMetadata);
// 只包含 project 和 config，table 为 null
```

#### 表级文件（有表上下文）
```java
RootModel rootModel = RootModel.forTable(projectMetadata, table);
// 包含 project、table 和 config
```

### 自动导入管理

RootModel 会自动检测是否需要验证注解，并在需要时添加到 `table.imports` 中：

```java
if (techStack.isUseValidation()) {
    boolean needsValidation = table.getColumns().stream()
        .anyMatch(col -> !col.isPrimaryKey() && (!col.isNullable() || col.getMaxLength() != null));
    if (needsValidation) {
        imports.add("jakarta.validation.constraints.NotNull");
        imports.add("jakarta.validation.constraints.Size");
    }
}
```

## 📋 字段对照表

| 结构化访问 | 扁平访问 | 说明 |
|-----------|---------|------|
| `${project.packageName}` | `${packageName}` | 包名 |
| `${project.projectName}` | `${projectName}` | 项目名 |
| `${project.className}` | `${className}` | 应用类名（项目级） |
| `${table.className}` | `${className}` | 实体类名（表级） |
| `${table.tableName}` | `${tableName}` | 表名 |
| `${table.columns}` | `${columns}` | 列列表 |
| `${table.imports}` | `${imports}` | 导入列表 |
| `${config.useLombok}` | `${techStack.useLombok}` | 是否使用 Lombok |
| `${config.useValidation}` | `${techStack.useValidation}` | 是否使用验证 |
| `${config.ormFramework}` | `${techStack.ormFramework}` | ORM 框架 |

## 🚀 迁移建议

### 新模板
推荐使用结构化访问方式，更清晰、更易维护。

### 现有模板
可以继续使用扁平结构，无需修改。如果需要，可以逐步迁移到结构化访问。

## ✅ 总结

RootModel 提供了：
1. ✅ **结构化数据访问** - 层次清晰，易于理解
2. ✅ **向后兼容** - 现有模板无需修改
3. ✅ **自动导入管理** - 智能添加验证注解导入
4. ✅ **类型安全** - 通过内部类提供类型提示

**模板开发体验提升了一个档次！** 🎉

