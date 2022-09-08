package com.ulalalab.snipe.common.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import java.util.Locale;

public class P6spyFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(elapsed, category, sql);
        //return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + P6Util.singleLine(prepared) + sql;
        return "" + sql;
    }

    private String formatSql(long elapsed, String category,String sql) {
        if (sql == null || sql.trim().equals("")) return sql;

        // Only format Statement, distinguish DDL And DML
        if (Category.STATEMENT.getName().equals(category)) {
            String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
            if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            sql = System.lineSeparator() + "Hibernate (elapsed: " + elapsed + "ms): " + sql + System.lineSeparator();
        }
        return sql;
    }
}