# S2S v3.0 更新日志

## 🎉 版本概述

S2S v3.0 是一个重大版本更新，引入了 Swagger/OpenAPI 3 支持、Vue.js 前端界面、项目打包下载功能，以及多项重要的 bug 修复和优化。

---

## ✨ 新功能

### 1. Swagger/SpringDoc OpenAPI 3 支持

- **可插拔的 API 文档生成**
  - 通过 `techStack.useSwagger` 开关控制
  - 自动生成 `OpenApiConfig.java` 配置类
  - Entity 类自动添加 `@Schema` 注解
  - Controller 自动添加 `@Tag`、`@Operation`、`@Parameter` 注解

- **依赖管理**
  - `pom.xml.ftl` 中条件性添加 SpringDoc OpenAPI 3 依赖（版本 2.3.0）
  - 仅在启用 Swagger 时添加依赖

- **模板增强**
  - `Entity.java.ftl`（MyBatis 和 MyBatis-Plus）支持 Swagger 注解
  - `Controller.java.ftl` 支持完整的 Swagger 注解
  - 智能处理表注释为空的情况，使用类名作为默认值

- **API 文档访问**
  - 生成的 API 文档可通过 `http://localhost:8080/swagger-ui/index.html` 访问

### 2. Vue.js 前端界面

- **现代化 Web 界面**
  - 基于 Vue 3 + TypeScript + Element Plus
  - 完整的代码生成表单
  - 实时代码预览
  - 项目打包下载功能

- **技术栈**
  - Vue 3（Composition API）
  - TypeScript
  - Element Plus（UI 组件库）
  - Pinia（状态管理）
  - Vue Router（路由）
  - Axios（HTTP 客户端）
  - Vite（构建工具）

- **功能特性**
  - 可视化配置项目信息和技术栈
  - 支持 MySQL 和 PostgreSQL 模板快速填充
  - 支持 MyBatis 和 MyBatis-Plus 预设配置
  - 代码预览和下载
  - 项目打包下载

### 3. 项目打包下载功能

- **ZIP 文件打包**
  - 新增 `/api/s2s/pack` 接口（支持 POST 和 GET）
  - 将生成的项目打包为 ZIP 文件
  - 支持直接下载

- **安全验证**
  - 路径遍历防护
  - 文件大小限制
  - 文件名清理

---

## 🐛 Bug 修复

### 1. 类名引号问题修复

**问题**：生成的代码中类名和变量名带有引号（如 `"omsCart"` 而不是 `omsCart`）

**原因**：SQL 解析时，表名和列名的 `toString()` 方法可能返回带引号的字符串

**修复**：
- 在 `PostgreSqlParserImpl` 和 `MySqlParserImpl` 中增强表名和列名清理逻辑
- 移除所有类型的引号（反引号、双引号、单引号）
- 添加 `trim()` 去除空白字符
- 在设置 `className` 和 `propertyName` 前进行双重清理

**影响文件**：
- `src/main/java/com/ming/s2s/core/parser/PostgreSqlParserImpl.java`
- `src/main/java/com/ming/s2s/core/parser/MySqlParserImpl.java`

### 2. Swagger 注解空值问题修复

**问题**：当表注释为空时，Swagger 的 `@Tag` 注解的 `name` 和 `description` 为空

**原因**：FreeMarker 的 `!` 操作符只在变量为 `null` 时生效，空字符串不会触发默认值

**修复**：
- 使用 `<#assign displayName = (tableComment?has_content)?then(tableComment, className)>`
- `?has_content` 检查变量是否存在且非空
- 所有 Swagger 注解统一使用 `displayName` 变量

**影响文件**：
- `src/main/resources/templates/common/Controller.java.ftl`

### 3. Controller 模板路径问题修复

**问题**：启用 Swagger 时，`TemplateSelector.getControllerTemplate()` 返回不存在的 `swagger/Controller.java.ftl`

**修复**：
- 统一使用 `common/Controller.java.ftl` 模板
- 模板内部通过条件判断处理 Swagger 注解

**影响文件**：
- `src/main/java/com/ming/s2s/core/generator/TemplateSelector.java`

---

## 🔧 代码改进

### 1. 模板组织结构优化

- **新的模板目录结构**：
  ```
  templates/
  ├── common/          # 通用模板（支持条件性 Swagger）
  ├── mybatis/         # MyBatis 专用模板
  ├── mybatis-plus/    # MyBatis-Plus 专用模板
  ├── maven/           # Maven 构建文件
  └── jpa/             # JPA 模板（待实现）
  ```

### 2. RootModel 增强

- 动态添加 Swagger imports（`io.swagger.v3.oas.annotations.media.Schema`）
- 保持向后兼容性

### 3. FileUtils 增强

- 添加 `packProjectToZip()` 方法
- 支持 `OpenApiConfig.java` 正确放置到 `config` 包

### 4. 错误处理优化

- 增强 `GlobalExceptionHandler`，处理 `HttpMessageNotReadableException`
- 提供更友好的错误消息

---

## 📝 文档更新

### 1. README.md 全面更新

- 添加 Swagger/OpenAPI 3 支持说明
- 添加 Vue.js 前端界面说明
- 添加项目打包下载功能说明
- 更新技术栈表格
- 更新模板文件结构说明
- 添加最新更新章节

### 2. 新增文档

- `ROOT_MODEL_GUIDE.md` - RootModel 使用指南
- `ROOT_MODEL_IMPLEMENTATION.md` - RootModel 实现说明
- `frontend/README.md` - 前端项目说明
- `frontend/FRONTEND_GUIDE.md` - 前端使用指南
- `frontend/QUICK_START.md` - 前端快速开始

---

## 📦 新增文件

### 后端文件

- `src/main/resources/templates/common/OpenApiConfig.java.ftl` - Swagger 配置类模板
- `src/main/java/com/ming/s2s/model/dto/PackRequest.java` - 打包请求 DTO

### 前端文件

- `frontend/` - 完整的 Vue.js 前端项目
  - `package.json` - 项目配置
  - `vite.config.ts` - Vite 配置
  - `tsconfig.json` - TypeScript 配置
  - `src/main.ts` - 应用入口
  - `src/App.vue` - 根组件
  - `src/router/index.ts` - 路由配置
  - `src/api/` - API 封装
  - `src/views/` - 页面组件
  - `src/stores/` - 状态管理

---

## 🔄 修改的文件

### 核心代码

- `src/main/java/com/ming/s2s/core/parser/PostgreSqlParserImpl.java` - 增强表名/列名清理
- `src/main/java/com/ming/s2s/core/parser/MySqlParserImpl.java` - 增强表名/列名清理
- `src/main/java/com/ming/s2s/core/generator/FreeMarkerGenerator.java` - 添加 OpenApiConfig 生成
- `src/main/java/com/ming/s2s/core/generator/RootModel.java` - 添加 Swagger imports
- `src/main/java/com/ming/s2s/core/generator/TemplateSelector.java` - 修复 Controller 模板路径
- `src/main/java/com/ming/s2s/common/utils/FileUtils.java` - 添加打包功能和 OpenApiConfig 路径处理
- `src/main/java/com/ming/s2s/module/codegen/controller/CodeGenController.java` - 添加打包接口
- `src/main/java/com/ming/s2s/common/exception/GlobalExceptionHandler.java` - 增强错误处理

### 模板文件

- `src/main/resources/templates/maven/pom.xml.ftl` - 更新 Swagger 版本到 2.3.0
- `src/main/resources/templates/common/Controller.java.ftl` - 添加 Swagger 注解支持，修复空值问题
- `src/main/resources/templates/mybatis/Entity.java.ftl` - 添加 Swagger 注解支持
- `src/main/resources/templates/mybatis-plus/Entity.java.ftl` - 添加 Swagger 注解支持

---

## 🎯 技术亮点

1. **可插拔架构**：Swagger 功能完全可插拔，不影响现有功能
2. **智能默认值**：表注释为空时自动使用类名
3. **防御性编程**：多重清理确保类名和属性名正确
4. **现代化前端**：Vue 3 + TypeScript 提供优秀的开发体验
5. **完整文档**：详细的 README 和更新日志

---

## 🚀 升级指南

### 从 v2.x 升级到 v3.0

1. **Swagger 功能**（可选）：
   - 在请求中添加 `"useSwagger": true` 即可启用
   - 生成的代码会自动包含 Swagger 配置和注解

2. **前端界面**（可选）：
   - 进入 `frontend` 目录
   - 运行 `npm install` 安装依赖
   - 运行 `npm run dev` 启动开发服务器

3. **API 变更**：
   - 新增 `/api/s2s/pack` 接口用于项目打包
   - 其他接口保持不变，向后兼容

---

## 📊 统计数据

- **新增文件**：50+ 个文件（包括前端项目）
- **修改文件**：15+ 个文件
- **新增功能**：3 个主要功能
- **Bug 修复**：3 个重要问题
- **代码行数**：前端约 2000+ 行，后端约 500+ 行新增/修改

---

## 🙏 致谢

感谢所有为 S2S v3.0 做出贡献的开发者和用户！

---

**版本号**：v3.0  
**发布日期**：2025-12-20  
**主要维护者**：MING

