# RootModel 实现总结

## ✅ 已完成的工作

### 1. 创建 RootModel 类

**位置**：`src/main/java/com/ming/s2s/core/generator/RootModel.java`

**核心特性**：
- ✅ 三层数据结构：`project`、`table`、`config`
- ✅ 静态工厂方法：`forProject()` 和 `forTable()`
- ✅ 向后兼容：`toMap()` 同时提供扁平结构和结构化数据
- ✅ 自动导入管理：智能添加验证注解导入

### 2. 更新 FreeMarkerGenerator

**改动**：
- ✅ `buildProjectData()` 使用 `RootModel.forProject()`
- ✅ `buildTableData()` 使用 `RootModel.forTable()`
- ✅ 简化了代码，移除了重复的数据构建逻辑

### 3. 模板兼容性

**验证结果**：
- ✅ 现有模板无需修改即可工作（向后兼容）
- ✅ 新模板可以使用结构化访问（`${project.packageName}`）
- ✅ 旧模板可以使用扁平访问（`${packageName}`）

## 📊 数据结构对比

### 旧方式（扁平结构）

```java
Map<String, Object> data = new HashMap<>();
data.put("packageName", projectMetadata.getPackageName());
data.put("projectName", projectMetadata.getProjectName());
data.put("techStack", projectMetadata.getTechStack());
data.put("tableName", table.getTableName());
data.put("className", table.getClassName());
// ... 更多字段
```

### 新方式（RootModel）

```java
RootModel rootModel = RootModel.forTable(projectMetadata, table);
Map<String, Object> data = rootModel.toMap();
// 自动包含：
// - 结构化数据：project, table, config
// - 扁平数据：packageName, projectName, className, techStack, ...
```

## 🎯 使用示例

### 项目级模板（pom.xml）

```freemarker
<#-- 使用结构化访问 -->
<groupId>${project.packageName}</groupId>
<artifactId>${project.projectName}</artifactId>
<java.version>${project.javaVersion}</java.version>

<#-- 或使用扁平访问（向后兼容） -->
<groupId>${packageName}</groupId>
<artifactId>${projectName}</artifactId>
<java.version>${techStack.javaVersion}</java.version>
```

### 表级模板（Entity.java）

```freemarker
<#-- 使用结构化访问 -->
package ${project.packageName}.entity;

<#if config.useLombok>
import lombok.Data;
</#if>

<#list table.imports as importPath>
import ${importPath};
</#list>

public class ${table.className} {
    <#list table.columns as col>
        <#if !col.nullable && config.useValidation>
        @NotNull
        </#if>
        private ${col.javaType} ${col.propertyName};
    </#list>
}

<#-- 或使用扁平访问（向后兼容） -->
package ${packageName}.entity;

<#if techStack.useLombok>
import lombok.Data;
</#if>

<#list imports as importPath>
import ${importPath};
</#list>

public class ${className} {
    <#list columns as col>
        <#if !col.nullable && techStack.useValidation>
        @NotNull
        </#if>
        private ${col.javaType} ${col.propertyName};
    </#list>
}
```

## 🔍 关键实现细节

### 1. 自动导入管理

```java
// 在 RootModel.forTable() 中
if (techStack.isUseValidation()) {
    boolean needsValidation = table.getColumns().stream()
        .anyMatch(col -> !col.isPrimaryKey() && (!col.isNullable() || col.getMaxLength() != null));
    if (needsValidation) {
        imports.add("jakarta.validation.constraints.NotNull");
        imports.add("jakarta.validation.constraints.Size");
    }
}
```

### 2. 向后兼容字段映射

```java
// 在 RootModel.toMap() 中
map.put("packageName", packageName);           // 扁平访问
map.put("project", project);                   // 结构化访问
// 两者都可用
```

### 3. 主键列信息提取

```java
// 自动提取主键列信息
table.getColumns().stream()
    .filter(col -> col.isPrimaryKey())
    .findFirst()
    .ifPresent(pkCol -> {
        tableInfo.setPrimaryKeyColumnName(pkCol.getColumnName());
        tableInfo.setPrimaryKeyPropertyName(pkCol.getPropertyName());
    });
```

## 📋 字段对照表

| 结构化访问 | 扁平访问 | 适用场景 |
|-----------|---------|---------|
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

## 🚀 优势

1. **结构化访问** - 数据层次清晰，易于理解
2. **向后兼容** - 现有模板无需修改
3. **类型安全** - 通过内部类提供类型提示
4. **自动管理** - 智能添加验证注解导入
5. **代码简化** - 减少了重复的数据构建逻辑

## ✅ 总结

RootModel 成功实现了：
- ✅ 统一的数据模型结构
- ✅ 向后兼容的字段映射
- ✅ 自动导入管理
- ✅ 简化的代码生成逻辑

**模板开发体验提升了一个档次！** 🎉

