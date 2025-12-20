# S2S Frontend 使用指南

## 🚀 快速开始

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

## 📁 项目结构

```
frontend/
├── src/
│   ├── api/                 # API 接口封装
│   │   ├── request.ts      # Axios 配置和拦截器
│   │   ├── types.ts        # TypeScript 类型定义
│   │   └── codegen.ts      # 代码生成相关接口
│   ├── views/              # 页面组件
│   │   ├── CodeGen.vue     # 代码生成主页面
│   │   └── CodePreview.vue # 代码预览页面
│   ├── stores/             # Pinia 状态管理
│   │   └── codegen.ts      # 代码生成状态
│   ├── router/             # 路由配置
│   │   └── index.ts
│   ├── App.vue             # 根组件
│   └── main.ts             # 入口文件
├── public/                 # 静态资源
├── index.html              # HTML 模板
├── package.json            # 项目配置
├── vite.config.ts         # Vite 配置
└── tsconfig.json          # TypeScript 配置
```

## 🎨 功能特性

### 1. 代码生成页面 (`CodeGen.vue`)

**主要功能：**
- ✅ SQL 输入（支持多表，用分号分隔）
- ✅ 项目配置（项目名、包名、输出目录）
- ✅ 数据库类型选择（MySQL、PostgreSQL）
- ✅ 技术栈配置（ORM 框架、构建工具、Java 版本）
- ✅ 功能选项（Lombok、Swagger、Validation）
- ✅ 实时代码预览
- ✅ 一键打包下载

**表单验证：**
- SQL 必填
- 项目名必填
- 包名必填且格式验证
- 输出目录必填

### 2. 代码预览

- 文件列表切换
- 代码内容展示
- 滚动查看长文件

### 3. 打包下载

- 自动打包为 ZIP
- 浏览器自动下载
- 支持中文文件名

## 🔧 API 集成

### 接口封装

所有 API 调用都在 `src/api/codegen.ts` 中：

```typescript
// 生成代码
generateCode(data: GenRequest): Promise<ApiResult<GeneratedCodeFiles>>

// 打包项目（POST）
packProject(data: PackRequest): Promise<Blob>

// 打包项目（GET）
packProjectGet(projectName: string, outputDir: string): Promise<Blob>
```

### 请求拦截器

- 自动添加 Content-Type
- 统一错误处理
- 显示错误消息

### 响应拦截器

- 统一处理响应格式
- 自动显示错误消息
- 处理文件下载（Blob）

## 📦 状态管理

使用 Pinia 管理代码生成状态：

```typescript
interface CodeGenState {
  generatedFiles: GeneratedCodeFiles  // 生成的代码文件
  projectInfo: ProjectInfo            // 项目信息
}
```

## 🎯 使用流程

1. **输入 SQL** - 在 SQL 输入框中输入 CREATE TABLE 语句
2. **配置项目** - 填写项目名、包名、输出目录
3. **选择数据库** - 选择 MySQL 或 PostgreSQL
4. **配置技术栈** - 选择 ORM 框架、Java 版本等（可选）
5. **生成代码** - 点击"生成代码"按钮
6. **预览代码** - 在下方预览生成的代码文件
7. **打包下载** - 点击"打包下载"按钮下载 ZIP 文件

## 🔌 后端连接

### 开发环境

默认代理配置（`vite.config.ts`）：
- 前端：http://localhost:3000
- 后端：http://localhost:8080
- 代理：`/api` → `http://localhost:8080/api`

### 生产环境

修改 `.env.production`：
```
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## 🎨 UI 组件

使用 Element Plus 组件库：
- `el-form` - 表单
- `el-input` - 输入框
- `el-select` - 下拉选择
- `el-radio-group` - 单选组
- `el-checkbox` - 复选框
- `el-button` - 按钮
- `el-card` - 卡片
- `el-tabs` - 标签页
- `el-message` - 消息提示
- `el-scrollbar` - 滚动条

## 📝 注意事项

1. **后端服务**：确保后端运行在 `http://localhost:8080`
2. **跨域问题**：开发环境已配置代理，生产环境需要配置 CORS
3. **文件下载**：打包下载功能需要浏览器支持 Blob API
4. **SQL 格式**：MySQL 使用反引号，PostgreSQL 使用双引号或不使用引号

## 🐛 常见问题

### 1. 无法连接后端

- 检查后端服务是否启动
- 检查 `vite.config.ts` 中的 proxy 配置
- 检查浏览器控制台错误信息

### 2. 下载文件失败

- 检查项目是否已生成
- 检查项目名和输出目录是否正确
- 查看浏览器控制台错误信息

### 3. 代码预览不显示

- 检查是否成功生成代码
- 检查响应数据格式
- 查看浏览器控制台错误信息

## 🚀 部署

### 开发环境

```bash
npm run dev
```

### 生产环境

```bash
npm run build
```

构建产物在 `dist/` 目录，可以部署到任何静态文件服务器。

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

