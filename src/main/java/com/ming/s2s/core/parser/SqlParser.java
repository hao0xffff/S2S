package com.ming.s2s.core.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.springframework.stereotype.Component;
import java.util.List;

@Component // 必须加这个注解，Spring 才能找到它
public class SqlParser {

    /**
     * 将 SQL 字符串解析为 Druid 的表定义对象
     */
    public MySqlCreateTableStatement parse(String sql) {
        // 使用 Druid 工具类解析
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        if (statements.isEmpty()) {
            throw new RuntimeException("未能解析到有效的 SQL 语句");
        }

        SQLStatement statement = statements.get(0);
        if (!(statement instanceof MySqlCreateTableStatement)) {
            throw new RuntimeException("只支持 CREATE TABLE 语句");
        }

        return (MySqlCreateTableStatement) statement;
    }
}