# S2S Frontend 快速启动指南

## 📦 安装和运行

### 1. 进入前端目录

```bash
cd frontend
```

### 2. 安装依赖

```bash
npm install
```

### 3. 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:3000

## 🎯 使用步骤

### 步骤 1: 输入 SQL

在 SQL 输入框中输入 CREATE TABLE 语句，例如：

```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) COMMENT '邮箱',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='用户表';
```

### 步骤 2: 配置项目信息

- **项目名称**：例如 `demo-project`
- **包名**：例如 `com.example.demo`
- **输出目录**：例如 `D:/S2S_Output`

### 步骤 3: 选择数据库类型

- MySQL（默认）
- PostgreSQL

### 步骤 4: 配置技术栈（可选）

- **ORM 框架**：MyBatis 或 MyBatis-Plus
- **构建工具**：Maven
- **Java 版本**：8、11、17、21
- **功能选项**：Lombok、Swagger、Validation

### 步骤 5: 生成代码

点击"生成代码"按钮，等待生成完成。

### 步骤 6: 预览代码

在下方预览区域查看生成的代码文件，可以切换不同文件查看。

### 步骤 7: 打包下载

点击"打包下载"按钮，自动下载 ZIP 文件。

## 🔧 配置说明

### 后端地址配置

如果后端不在 `http://localhost:8080`，修改 `vite.config.ts`：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url:8080',
      changeOrigin: true
    }
  }
}
```

### 环境变量

创建 `.env.development` 或 `.env.production`：

```
VITE_API_BASE_URL=/api
```

## 📱 界面预览

### 主页面功能

1. **SQL 输入区** - 大文本框，支持多行 SQL
2. **项目配置区** - 项目名、包名、输出目录
3. **数据库选择** - 单选按钮组
4. **技术栈配置** - 折叠面板，可选配置
5. **操作按钮** - 生成、下载、重置
6. **代码预览区** - 标签页切换查看不同文件

## 🎨 界面特色

- **渐变背景** - 紫色渐变，现代化设计
- **卡片布局** - 清晰的信息分组
- **响应式设计** - 适配不同屏幕尺寸
- **实时反馈** - 加载状态、成功/错误提示
- **代码高亮** - 代码预览区域使用等宽字体

## ⚠️ 注意事项

1. **后端服务**：确保后端 Spring Boot 应用已启动
2. **端口冲突**：如果 3000 端口被占用，Vite 会自动使用其他端口
3. **浏览器兼容**：推荐使用 Chrome、Firefox 或 Edge
4. **文件下载**：打包下载需要浏览器支持 Blob API

## 🐛 故障排除

### 问题 1: 无法连接后端

**解决：**
- 检查后端是否运行在 8080 端口
- 检查浏览器控制台是否有 CORS 错误
- 检查 `vite.config.ts` 中的 proxy 配置

### 问题 2: 下载文件失败

**解决：**
- 确保已成功生成代码
- 检查项目名和输出目录是否正确
- 查看浏览器控制台错误信息

### 问题 3: 代码预览空白

**解决：**
- 检查是否成功生成代码（查看响应数据）
- 检查浏览器控制台是否有 JavaScript 错误
- 尝试刷新页面

## 📚 更多信息

查看 `README.md` 和 `FRONTEND_GUIDE.md` 获取更多详细信息。

