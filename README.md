# S2S - SQL to SpringBoot 代码生成工具

## 📖 项目介绍

S2S（SQL to SpringBoot）是一个智能代码生成工具，支持将多种数据库的建表 SQL 转换为 SpringBoot 项目代码。采用**策略模式 + 工厂模式**设计，支持动态选择数据库类型和技术栈组合。

### 核心特性

- ✅ **多数据库支持**：MySQL、PostgreSQL（可扩展 Oracle、SQL Server）
- ✅ **多技术栈支持**：MyBatis、MyBatis-Plus（可扩展 JPA）
- ✅ **策略模式**：根据数据库类型和技术栈动态选择解析器和生成器
- ✅ **工厂模式**：统一管理解析器和生成器策略
- ✅ **安全防护**：路径遍历防护、输入验证、文件名注入防护
- ✅ **模板化设计**：FreeMarker 模板，易于扩展和维护
- ✅ **自动验证注解**：根据数据库约束自动生成 Bean Validation 注解
- ✅ **Swagger/OpenAPI 3 支持**：自动生成 SpringDoc OpenAPI 3 配置和注解
- ✅ **RootModel 数据模型**：结构化的模板数据访问
- ✅ **Vue.js 前端界面**：现代化的 Web 界面，支持代码生成和预览
- ✅ **项目打包下载**：支持将生成的项目打包为 ZIP 文件下载

### 技术栈

**后端：**
- Spring Boot 3.1.5+
- FreeMarker（模板引擎）
- Druid（SQL 解析，支持多数据库方言）
- MyBatis / MyBatis-Plus（生成的 ORM 框架）
- Lombok（简化代码）
- Spring Validation（输入验证）
- SpringDoc OpenAPI 3（Swagger 文档）

**前端：**
- Vue 3 + TypeScript
- Element Plus（UI 组件库）
- Pinia（状态管理）
- Vue Router（路由）
- Axios（HTTP 客户端）
- Vite（构建工具）

---

## 🏗️ 项目架构

### 分层结构（策略模式 + 工厂模式）

```
S2S 项目
├── Controller 层 (CodeGenController)
│   └── 接收 HTTP 请求，参数验证，调用 Service
│
├── Service 层 (CodeGenService)
│   └── 使用工厂获取策略，组装解析器和生成器
│
├── Core 层 (核心业务逻辑)
│   ├── parser/              # SQL 解析策略
│   │   ├── SqlParser.java (策略接口)
│   │   ├── MySqlParserImpl.java (MySQL 实现)
│   │   ├── PostgreSqlParserImpl.java (PostgreSQL 实现)
│   │   └── SqlParserFactory.java (解析器工厂)
│   │
│   ├── generator/           # 代码生成策略
│   │   ├── CodeGenerator.java (策略接口)
│   │   ├── FreeMarkerGenerator.java (FreeMarker 实现)
│   │   ├── GeneratorFactory.java (生成器工厂)
│   │   ├── TemplateSelector.java (模板选择器)
│   │   └── RootModel.java (全局数据模型)
│   │
│   └── converter/          # 类型转换
│       └── TypeConverter.java
│
├── Model 层 (数据模型)
│   ├── dto/                # 请求对象
│   ├── metadata/           # 元数据模型
│   └── vo/                 # 返回对象
│
├── Common 层 (公共基础设施)
│   ├── exception/          # 异常处理
│   ├── result/             # 统一返回体
│   ├── utils/              # 工具类
│   └── validation/         # 输入验证
│
└── Templates 层 (FreeMarker 模板)
    ├── common/             # 通用模板
    ├── mybatis/            # MyBatis 模板
    ├── mybatis-plus/       # MyBatis-Plus 模板
    └── maven/              # Maven 构建文件
```

### 核心流程

```
HTTP 请求 (SQL + 数据库类型 + 技术栈配置)
    ↓
CodeGenController (参数验证)
    ↓
CodeGenService.generateCode()
    ↓
SqlParserFactory.getParser(dbType) → 选择解析器策略
    ↓
SqlParser.parse() → 解析 SQL 为 TableMetadata 列表
    ↓
GeneratorFactory.getGenerator(techStack) → 选择生成器策略
    ↓
CodeGenerator.generate() → FreeMarker 模板渲染
    ↓
TemplateSelector → 根据技术栈选择模板路径
    ↓
生成代码 Map<String, String>
    ↓
CodeGenService.writeToDisk() → 写入文件系统（带安全验证）
    ↓
返回生成的代码预览
```

### 设计模式

#### 1. 策略模式（Strategy Pattern）
- **SQL 解析策略**：`SqlParser` 接口，不同数据库有不同的实现
- **代码生成策略**：`CodeGenerator` 接口，不同技术栈有不同的实现
- **优势**：易于扩展新的数据库或技术栈，符合开闭原则

#### 2. 工厂模式（Factory Pattern）
- **SqlParserFactory**：根据数据库类型创建对应的解析器
- **GeneratorFactory**：根据技术栈配置创建对应的生成器
- **优势**：解耦客户端代码与具体实现，统一管理策略实例

#### 3. 模板方法模式（Template Method Pattern）
- `FreeMarkerGenerator` 定义了代码生成的固定流程
- 具体模板选择由 `TemplateSelector` 决定
- **优势**：代码生成流程统一，模板选择灵活

### 支持的技术栈组合

| 数据库 | ORM框架 | 构建工具 | 状态 |
|--------|---------|----------|------|
| MySQL | MyBatis | Maven | ✅ 完整支持 |
| MySQL | MyBatis-Plus | Maven | ✅ 完整支持 |
| PostgreSQL | MyBatis | Maven | ✅ 完整支持 |
| PostgreSQL | MyBatis-Plus | Maven | ✅ 完整支持 |
| MySQL | JPA | Maven | ⏳ 待实现 |
| * | * | Gradle | ⏳ 待实现 |

### 生成的文件结构

**MyBatis 项目**：
- `Entity.java` - 实体类（entity 包）
- `Mapper.java` - Mapper 接口（mapper 包）
- `Mapper.xml` - MyBatis XML（resources/mapper）
- `I{Entity}Service.java` - Service 接口（service 包）
- `{Entity}ServiceImpl.java` - Service 实现（service/impl 包）
- `{Entity}Controller.java` - Controller（controller 包）

**MyBatis-Plus 项目**：
- `Entity.java` - 实体类（带 MyBatis-Plus 注解）
- `Mapper.java` - Mapper 接口（继承 BaseMapper）
- `I{Entity}Service.java` - Service 接口
- `{Entity}ServiceImpl.java` - Service 实现（使用 BaseMapper）
- `{Entity}Controller.java` - Controller
- **不生成** `Mapper.xml`（BaseMapper 已提供方法）

**项目级文件**（所有技术栈共用）：
- `pom.xml` - Maven 构建文件（根据技术栈动态配置依赖）
- `application.properties` - 配置文件（根据数据库类型动态配置）
- `{ProjectName}Application.java` - 启动类（根据 ORM 框架选择模板）
- `Result.java` - 统一返回结果
- `ResultCode.java` - 返回码枚举
- `OpenApiConfig.java` - Swagger/OpenAPI 3 配置类（仅当 `useSwagger=true` 时生成）

---

## 🚀 API 接口文档

### 接口列表

#### 1. 生成代码接口

- **接口地址**: `POST http://localhost:8080/api/s2s/generate`
- **Content-Type**: `application/json`
- **请求方式**: POST
- **功能**: 根据 SQL 语句生成 SpringBoot 项目代码

#### 2. 打包项目接口

- **接口地址**: 
  - `POST http://localhost:8080/api/s2s/pack` (JSON 请求体)
  - `GET http://localhost:8080/api/s2s/pack?projectName=xxx&outputDir=xxx` (查询参数)
- **Content-Type**: `application/json` (POST)
- **请求方式**: POST 或 GET
- **响应**: ZIP 文件下载
- **功能**: 将已生成的项目打包为 ZIP 文件

### 前端界面

项目提供了基于 Vue 3 的现代化 Web 界面，支持：

- 📝 **代码生成表单**：可视化配置项目信息和技术栈
- 👀 **代码预览**：实时预览生成的代码文件
- 📦 **项目打包下载**：一键打包并下载生成的项目
- 🎨 **美观的 UI**：基于 Element Plus 的现代化界面

**启动前端：**
```bash
cd frontend
npm install
npm run dev
```

访问地址：`http://localhost:5173`（Vite 默认端口）

---

## 📦 打包项目接口

### 接口说明

将已生成的项目打包为 ZIP 文件，方便下载和分发。

### 请求示例

#### 方式一：POST 请求（JSON 请求体）

**请求体 (Body → raw → JSON):**

```json
{
    "projectName": "demo-project",
    "outputDir": "D:/S2S_Output"
}
```

#### 方式二：GET 请求（查询参数，更方便测试）

**URL:**
```
GET http://localhost:8080/api/s2s/pack?projectName=demo-project&outputDir=D:/S2S_Output
```

**说明:**
- GET 方式不需要设置请求体，直接在浏览器地址栏输入即可测试
- 适合快速测试和浏览器直接访问
- 参数需要 URL 编码（中文或特殊字符）

### 响应

- **Content-Type**: `application/octet-stream`
- **Content-Disposition**: `attachment; filename="demo-project.zip"`
- **Body**: ZIP 文件字节流

### Postman 下载 ZIP 文件的方法

**重要**：Postman 默认会将二进制响应显示为乱码，需要特殊设置才能下载文件。

#### 方法一：使用 Postman 的 "Send and Download" 功能（推荐）

1. 在 Postman 中创建请求
2. 配置请求方法和 URL：`POST http://localhost:8080/api/s2s/pack`
3. 设置 Body（JSON 格式）
4. **关键步骤**：点击 **Send** 按钮旁边的 **▼** 下拉箭头
5. 选择 **"Send and Download"**
6. Postman 会自动下载 ZIP 文件

#### 方法二：在 Postman 中查看响应并保存

1. 发送请求后，在响应区域点击 **"Save Response"**
2. 选择 **"Save to a file"**
3. 选择保存位置，文件名会自动设置为 `demo-project.zip`

#### 方法三：使用浏览器测试

直接在浏览器中无法测试 POST 请求，但可以使用以下方式：

1. 使用浏览器插件（如 Postman 的浏览器扩展）
2. 或者使用 JavaScript 代码：

```javascript
fetch('http://localhost:8080/api/s2s/pack', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        projectName: "demo-project",
        outputDir: "D:/S2S_Output"
    })
})
.then(response => response.blob())
.then(blob => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'demo-project.zip';
    a.click();
});
```

### 使用场景

1. 生成项目后，直接打包下载
2. 批量打包多个项目
3. 项目备份和分发

### 注意事项

- 项目必须已经通过 `/api/s2s/generate` 接口生成
- `projectName` 和 `outputDir` 必须与生成时使用的参数一致
- ZIP 文件包含完整的项目结构，可以直接解压使用
- **Postman 显示乱码是正常的**，需要使用 "Send and Download" 功能下载文件

---

## 🚀 Postman 测试指南

### 快速开始

#### 1. 启动应用

确保 Spring Boot 应用已启动，默认端口为 `8080`。

#### 2. 在 Postman 中创建请求

1. 打开 Postman
2. 点击 **New** → **HTTP Request**
3. 设置请求方法为 **POST**
4. 输入 URL: `http://localhost:8080/api/s2s/generate`
5. 在 **Headers** 标签页添加：
   - Key: `Content-Type`
   - Value: `application/json`

### 测试用例

#### 用例 1: MySQL + MyBatis + Maven (默认配置)

**请求体 (Body → raw → JSON):**

```json
{
    "sql": "CREATE TABLE `user` (\n  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,\n  `username` VARCHAR(50) NOT NULL COMMENT '用户名',\n  `email` VARCHAR(100) COMMENT '邮箱',\n  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'\n) COMMENT='用户表';",
    "projectName": "demo-project",
    "packageName": "com.example.demo",
    "outputDir": "D:/S2S_Output",
    "dbType": "mysql"
}
```

**说明:**
- 使用默认的 MyBatis + Maven 配置
- 不需要提供 `techStack`，会使用默认值

---

#### 用例 2: PostgreSQL + MyBatis + Maven

**请求体:**

```json
{
    "sql": "CREATE TABLE users (\n  id SERIAL PRIMARY KEY,\n  username VARCHAR(50) NOT NULL,\n  email VARCHAR(100),\n  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n);",
    "projectName": "postgres-demo",
    "packageName": "com.example.postgres",
    "outputDir": "D:/S2S_Output",
    "dbType": "postgresql",
    "techStack": {
        "ormFramework": "mybatis",
        "buildTool": "maven"
    }
}
```

**说明:**
- 使用 PostgreSQL 数据库
- PostgreSQL 使用 `SERIAL` 类型作为自增主键
- 使用双引号或不用引号，不使用反引号

---

#### 用例 3: MySQL + MyBatis-Plus + Maven

**请求体:**

```json
{
    "sql": "CREATE TABLE `product` (\n  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,\n  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',\n  `price` DECIMAL(10,2) COMMENT '价格',\n  `stock` INT DEFAULT 0 COMMENT '库存',\n  `status` TINYINT DEFAULT 1 COMMENT '状态：1-上架，0-下架'\n) COMMENT='产品表';",
    "projectName": "mybatis-plus-demo",
    "packageName": "com.example.product",
    "outputDir": "D:/S2S_Output",
    "dbType": "mysql",
    "techStack": {
        "ormFramework": "mybatis-plus",
        "buildTool": "maven",
        "useLombok": true,
        "useSwagger": false,
        "useValidation": true,
        "javaVersion": "17",
        "springBootVersion": "3.1.5"
    }
}
```

**说明:**
- 使用 MyBatis-Plus ORM 框架
- 生成的代码会使用 `BaseMapper` 和 MyBatis-Plus 注解

---

#### 用例 4: 完整配置示例（包含 Swagger）

**请求体:**

```json
{
    "sql": "CREATE TABLE `order` (\n  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,\n  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',\n  `user_id` BIGINT NOT NULL COMMENT '用户ID',\n  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',\n  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态',\n  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'\n) COMMENT='订单表';",
    "projectName": "order-service",
    "packageName": "com.ming.order",
    "outputDir": "D:/S2S_Output",
    "dbType": "mysql",
    "techStack": {
        "ormFramework": "mybatis",
        "buildTool": "maven",
        "useLombok": true,
        "useSwagger": true,
        "useValidation": true,
        "javaVersion": "17",
        "springBootVersion": "3.1.5"
    }
}
```

**说明:**
- `useSwagger: true` 会生成：
  - `OpenApiConfig.java` 配置类
  - Entity 类上的 `@Schema` 注解
  - Controller 类和方法上的 Swagger 注解（`@Tag`, `@Operation`, `@Parameter`）
  - `pom.xml` 中的 SpringDoc OpenAPI 3 依赖
- 生成的 API 文档访问地址：`http://localhost:8080/swagger-ui/index.html`

---

### 响应示例

#### 成功响应

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "pom.xml": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...",
        "application.properties": "spring.datasource.url=...",
        "OrderApplication.java": "package com.ming.order;...",
        "Order.java": "package com.ming.order.entity;...",
        "OrderMapper.java": "package com.ming.order.mapper;...",
        "OrderMapper.xml": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...",
        "IOrderService.java": "package com.ming.order.service;...",
        "OrderServiceImpl.java": "package com.ming.order.service.impl;...",
        "OrderController.java": "package com.ming.order.controller;...",
        "Result.java": "package com.ming.order.common.api;...",
        "ResultCode.java": "package com.ming.order.common.api;..."
    }
}
```

#### 错误响应

```json
{
    "code": 500,
    "message": "SQL cannot be empty",
    "data": null
}
```

---

### 请求参数说明

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| sql | String | ✅ | CREATE TABLE SQL 语句 | `CREATE TABLE user (...)` |
| projectName | String | ✅ | 项目名称（支持连字符） | `order-service` |
| packageName | String | ✅ | 包名 | `com.example.order` |
| outputDir | String | ✅ | 输出目录（绝对路径） | `D:/S2S_Output` |
| dbType | String | ❌ | 数据库类型，默认 `mysql` | `mysql`, `postgresql` |
| techStack | Object | ❌ | 技术栈配置 | 见下方说明 |

#### techStack 对象参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| ormFramework | String | `mybatis` | ORM框架：`mybatis`, `mybatis-plus`, `jpa` |
| buildTool | String | `maven` | 构建工具：`maven`, `gradle` |
| useLombok | Boolean | `true` | 是否使用 Lombok |
| useSwagger | Boolean | `false` | 是否使用 Swagger/SpringDoc OpenAPI 3 |
| useValidation | Boolean | `true` | 是否使用 Bean Validation |
| javaVersion | String | `17` | Java版本：`8`, `11`, `17`, `21` |
| springBootVersion | String | `3.1.5` | Spring Boot 版本 |

---

### 常见错误及解决方案

#### 错误 1: 400 Bad Request - SQL cannot be blank

**原因**: SQL 字段为空或格式错误

**解决**: 检查 SQL 字段是否填写，SQL 语法是否正确

---

#### 错误 2: 500 - No SQL parser found for database type

**原因**: 不支持的数据库类型

**解决**: 使用 `mysql` 或 `postgresql`（或 `postgres`）

---

#### 错误 3: 500 - Failed to parse SQL statements

**原因**: SQL 语法错误或不符合数据库规范

**解决**: 
- MySQL: 使用反引号 `` ` `` 包裹标识符
- PostgreSQL: 使用双引号 `"` 或不使用引号，不要使用反引号

---

#### 错误 4: 500 - Invalid ORM framework

**原因**: `techStack.ormFramework` 值不正确

**解决**: 使用 `mybatis`, `mybatis-plus`, 或 `jpa`

---

#### 错误 5: Connection refused

**原因**: 应用未启动或端口不正确

**解决**: 
1. 确认 Spring Boot 应用已启动
2. 检查应用端口（默认 8080）
3. 检查防火墙设置

---

### Postman 设置步骤详解

#### 步骤 1: 创建新请求

1. 打开 Postman
2. 点击左侧 **+** 号或 **New** 按钮
3. 选择 **HTTP Request**

#### 步骤 2: 配置请求

1. **方法**: 选择 `POST`
2. **URL**: 输入 `http://localhost:8080/api/s2s/generate`
3. **Headers** 标签:
   - 点击 **Add Header**
   - Key: `Content-Type`
   - Value: `application/json`

#### 步骤 3: 设置请求体

1. 点击 **Body** 标签
2. 选择 **raw**
3. 右侧下拉框选择 **JSON**
4. 粘贴上面的 JSON 请求体

#### 步骤 4: 发送请求

1. 点击 **Send** 按钮
2. 查看响应结果

---

### 快速测试命令 (cURL)

如果不想使用 Postman，也可以使用 cURL：

#### 生成代码

```bash
curl -X POST http://localhost:8080/api/s2s/generate \
  -H "Content-Type: application/json" \
  -d '{
    "sql": "CREATE TABLE `user` (`id` BIGINT PRIMARY KEY AUTO_INCREMENT, `name` VARCHAR(50))",
    "projectName": "test",
    "packageName": "com.test",
    "outputDir": "D:/S2S_Output",
    "dbType": "mysql"
}'
```

#### 打包项目（POST）

```bash
curl -X POST http://localhost:8080/api/s2s/pack \
  -H "Content-Type: application/json" \
  -d '{
    "projectName": "test",
    "outputDir": "D:/S2S_Output"
}' \
  --output test.zip
```

#### 打包项目（GET，更简单）

```bash
curl "http://localhost:8080/api/s2s/pack?projectName=test&outputDir=D:/S2S_Output" \
  --output test.zip
```

**或者直接在浏览器中访问：**
```
http://localhost:8080/api/s2s/pack?projectName=test&outputDir=D:/S2S_Output
```

---

## 🔒 安全特性

1. **路径遍历防护**：`PathValidator` 限制输出目录在允许的基目录内
2. **输入验证**：`InputValidator` 验证 SQL、表数量、文件大小等
3. **文件名注入防护**：`PathValidator.sanitizeFileName()` 清理文件名
4. **参数验证**：使用 Spring Validation 验证请求参数

---

## 📝 注意事项

1. **主键识别**：通过检查列定义中的 `PRIMARY KEY` 关键字和 `SERIAL` 类型识别主键
2. **MyBatis-Plus**：不生成 Mapper.xml，使用 BaseMapper 提供的方法
3. **模板路径**：模板按技术栈分类，`TemplateSelector` 负责选择正确的模板
4. **扩展性**：添加新数据库或技术栈只需实现对应接口并注册到工厂
5. **输出目录**：确保 `outputDir` 指定的目录存在且有写权限
6. **SQL 格式**：SQL 必须是有效的 CREATE TABLE 语句
7. **项目名**：`projectName` 可以包含中划线（如 `user-service`），会自动转换为类名（如 `UserService`）
8. **Swagger 支持**：
   - 启用 Swagger 后会自动生成 `OpenApiConfig.java` 配置类
   - Entity 类会添加 `@Schema` 注解
   - Controller 会添加 `@Tag`、`@Operation`、`@Parameter` 注解
   - 如果表没有注释，会使用类名作为 Swagger 文档的显示名称
9. **类名处理**：系统会自动清理表名和列名中的引号（反引号、双引号、单引号），确保生成的类名和属性名正确

---

## 🎯 项目结构

### 完整代码结构

```
com.ming.s2s
├── common/                    # 公共基础设施
│   ├── exception/            # 全局异常处理
│   ├── result/               # 统一返回体
│   ├── utils/                # 工具类
│   └── validation/           # 输入验证
│
├── config/                   # SpringBoot 配置
│   └── FreeMarkerConfig.java
│
├── core/                     # 核心业务逻辑
│   ├── converter/           # 类型转换
│   ├── generator/           # 代码生成策略
│   └── parser/              # SQL 解析策略
│
├── model/                    # 数据模型
│   ├── dto/                 # 请求对象
│   ├── metadata/            # 元数据模型
│   └── vo/                  # 返回对象
│
├── module/                   # 业务模块
│   └── codegen/             # 代码生成业务
│
└── S2SApplication.java       # 启动类
```

### 模板文件结构

```
src/main/resources/templates/
├── common/                    # 通用模板（所有技术栈共用）
│   ├── Application.java.ftl
│   ├── application.properties.ftl
│   ├── Controller.java.ftl   # 支持条件性 Swagger 注解
│   ├── OpenApiConfig.java.ftl # Swagger 配置类（条件生成）
│   ├── Service.java.ftl
│   ├── Result.java.ftl
│   └── ResultCode.java.ftl
│
├── mybatis/                   # MyBatis 专用模板
│   ├── Application.java.ftl
│   ├── Entity.java.ftl        # 支持 Swagger @Schema 注解
│   ├── Mapper.java.ftl
│   ├── Mapper.xml.ftl
│   └── ServiceImpl.java.ftl
│
├── mybatis-plus/              # MyBatis-Plus 专用模板
│   ├── Application.java.ftl
│   ├── Entity.java.ftl        # 支持 Swagger @Schema 注解
│   ├── Mapper.java.ftl
│   └── ServiceImpl.java.ftl
│
├── maven/                    # Maven 构建文件
│   └── pom.xml.ftl            # 条件性添加 Swagger 依赖
│
└── jpa/                      # JPA 模板（待实现）
    └── ...
```

---

## 📚 更多信息

- 查看生成的代码文件在 `outputDir` 指定的目录
- 生成的代码可以直接导入 IDE 使用
- 支持多表 SQL（用分号分隔多个 CREATE TABLE 语句）
- 生成的 Entity 会根据数据库约束自动生成验证注解（如 `@NotNull`, `@Size`）
- 启用 Swagger 后，生成的 API 文档可通过 `http://localhost:8080/swagger-ui/index.html` 访问
- 前端界面提供了完整的代码生成和预览功能，支持实时查看生成的代码

## 🆕 最新更新

### v3.0 新特性

- ✅ **Swagger/SpringDoc OpenAPI 3 支持**：可插拔的 API 文档生成
- ✅ **Vue.js 前端界面**：现代化的 Web 界面
- ✅ **项目打包下载**：支持 ZIP 文件打包和下载
- ✅ **类名引号清理**：自动清理表名和列名中的引号，确保生成的代码正确
- ✅ **Swagger 注解优化**：智能处理表注释为空的情况，使用类名作为默认值

---

## 📄 License

本项目采用 MIT 许可证。

