package com.github.housepower.jdbc.statement;


import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.stream.QuotedLexer;
import com.github.housepower.jdbc.stream.QuotedToken;
import com.github.housepower.jdbc.stream.QuotedTokenType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

public class ClickHousePreparedQueryStatement extends AbstractPreparedStatement {

    public ClickHousePreparedQueryStatement(ClickHouseConnection conn, String query) {
        this(conn, splitQueryByQuestionMark(query));
    }

    private ClickHousePreparedQueryStatement(ClickHouseConnection conn, String[] parts) {
        super(conn, parts, parts.length - 1);
    }

    @Override
    public boolean execute() throws SQLException {
        return execute(assembleQueryPartsAndParameters());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return executeUpdate(assembleQueryPartsAndParameters());
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("");
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return executeQuery(assembleQueryPartsAndParameters());
    }

    private static String[] splitQueryByQuestionMark(String query) {
        int lastPos = 0;
        QuotedLexer lexer = new QuotedLexer(query);
        List<String> queryParts = new ArrayList<String>();
        while (true) {
            QuotedToken token = lexer.next();

            if (token.type() == QuotedTokenType.QuestionMark) {
                queryParts.add(query.substring(lastPos, (lastPos = lexer.pos()) - 1));
            } else if (token.type().ordinal() >= QuotedTokenType.EndOfStream.ordinal()) {
                queryParts.add(query.substring(lastPos, query.length()));
                return queryParts.toArray(new String[queryParts.size()]);
            }
        }
    }
}
