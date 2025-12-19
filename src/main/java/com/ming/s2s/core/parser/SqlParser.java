package com.ming.s2s.core.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SqlParser {

    /**
     * 将 SQL 字符串解析为多个 MySqlCreateTableStatement 对象
     * 适配 Plan B：支持多表同时解析
     */
    public List<MySqlCreateTableStatement> parse(String sql) {
        // 1. 使用 Druid 解析所有 SQL 语句
        // 注意：这里建议直接用 DbType.mysql (Druid 新版推荐) 或 JdbcConstants.MYSQL
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, DbType.mysql);

        if (statements == null || statements.isEmpty()) {
            throw new RuntimeException("未能解析到有效的 SQL 语句，请检查 SQL 格式是否正确");
        }

        List<MySqlCreateTableStatement> tableStatements = new ArrayList<>();

        // 2. 遍历所有解析出来的语句，过滤出建表语句
        for (SQLStatement statement : statements) {
            if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                tableStatements.add(createTableStatement);
            }
            // 如果是 INSERT 或 ALTER 语句，这里会自动跳过，保证引擎不会崩溃
        }

        if (tableStatements.isEmpty()) {
            throw new RuntimeException("输入的 SQL 中未包含任何 CREATE TABLE 语句");
        }

        return tableStatements;
    }
}