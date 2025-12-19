# S2S (SQL to SpringBoot Engine) 🚀

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![SpringBoot Version](https://img.shields.io/badge/SpringBoot-3.x-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](LICENSE)

**S2S** 是一个强大的后端代码自动化生成引擎。它能够深度解析 MySQL 的 DDL（建表）脚本，瞬间为你构建出一个标准的、生产就绪的 SpringBoot 项目骨架。



---

## 🌟 核心特性

- **全家桶式生成**：不仅生成代码，还生成整个项目！包括 `Entity`, `Mapper`, `Service`, `Controller` 以及 `pom.xml`。
- **环境零配置**：自动产出 `application.properties`，预设好数据库连接与 MyBatis 配置，实现真正的“解压即运行”。
- **智能类型识别**：支持自动识别 SQL 主键类型（Long, String, Integer 等），动态匹配 Java 泛型。
- **极致稳定性**：采用 `.properties` 配置文件规避 YAML 缩进风险，确保生成项目 100% 启动成功。
- **符合 Maven 规范**：产出的目录结构严格遵循标准 Maven 骨架（src/main/java, src/main/resources）。

---

## 🛠️ 技术栈

- **核心框架**: Spring Boot 3.x
- **数据库交互**: MyBatis, MySQL Connector
- **解析引擎**: Druid SQL Parser (阿里巴巴开源解析库)
- **模板技术**: FreeMarker
- **辅助工具**: Lombok, Apache Commons

---

## 🚀 快速开始

### 1. 运行引擎
启动 `S2SApplication.java`，引擎默认监听 `8080` 端口。

### 2. 调用接口生成项目
使用 Postman 或 Curl 调用接口：

- **URL**: `http://localhost:8080/api/s2s/generate`
- **Method**: `POST`
- **Body (JSON)**:
```json
{
    "sql": "CREATE TABLE sys_user (id BIGINT PRIMARY KEY, username VARCHAR(50));",
    "projectName": "my-cool-app",
    "baseDir": "D:/S2S_Output"
}
