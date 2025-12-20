# S2S Frontend - Vue 3 前端项目

## 项目简介

S2S 代码生成工具的前端界面，基于 Vue 3 + TypeScript + Element Plus 构建。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript
- **Element Plus** - Vue 3 组件库
- **Vite** - 下一代前端构建工具
- **Pinia** - Vue 状态管理
- **Vue Router** - 官方路由管理器
- **Axios** - HTTP 客户端

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API 接口封装
│   │   ├── request.ts   # Axios 配置
│   │   ├── types.ts     # TypeScript 类型定义
│   │   └── codegen.ts   # 代码生成相关接口
│   ├── views/           # 页面组件
│   │   ├── CodeGen.vue  # 代码生成页面
│   │   └── CodePreview.vue # 代码预览页面
│   ├── stores/          # Pinia 状态管理
│   │   └── codegen.ts   # 代码生成状态
│   ├── router/          # 路由配置
│   │   └── index.ts
│   ├── App.vue          # 根组件
│   └── main.ts          # 入口文件
├── index.html
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## 快速开始

### 安装依赖

```bash
cd frontend
npm install
```

### 开发运行

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

## 功能特性

### 1. 代码生成页面

- SQL 输入（支持多表）
- 项目配置（项目名、包名、输出目录）
- 数据库类型选择（MySQL、PostgreSQL）
- 技术栈配置（ORM 框架、构建工具、Java 版本等）
- 功能选项（Lombok、Swagger、Validation）

### 2. 代码预览

- 实时预览生成的代码
- 文件列表切换
- 代码高亮显示

### 3. 打包下载

- 一键打包项目为 ZIP
- 自动下载到本地

## API 接口

### 生成代码

```typescript
POST /api/s2s/generate
Body: {
  sql: string
  projectName: string
  packageName: string
  outputDir: string
  dbType?: string
  techStack?: TechStackConfig
}
```

### 打包项目

```typescript
POST /api/s2s/pack
Body: {
  projectName: string
  outputDir: string
}
Response: Blob (ZIP file)
```

## 环境配置

### 开发环境

在 `.env.development` 中配置：

```
VITE_API_BASE_URL=/api
```

### 生产环境

在 `.env.production` 中配置：

```
VITE_API_BASE_URL=/api
```

## 注意事项

1. 确保后端服务运行在 `http://localhost:8080`
2. 如果后端地址不同，修改 `vite.config.ts` 中的 proxy 配置
3. 打包下载功能需要浏览器支持 Blob 下载

## 浏览器支持

- Chrome (推荐)
- Firefox
- Edge
- Safari

