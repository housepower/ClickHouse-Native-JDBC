package org.houseflys.jdbc.statement;

import org.houseflys.jdbc.ClickHouseConnection;
import org.houseflys.jdbc.stream.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickHousePreparedInsertStatement extends AbstractPreparedStatement {

    private final int posOfData;
    private final String fullQuery;
    private final String insertQuery;
    private final List<Object[]> parameters;

    public ClickHousePreparedInsertStatement(int posOfData, String fullQuery, ClickHouseConnection conn) {
        super(conn, null, computeQuestionMarkSize(fullQuery, posOfData));
        this.posOfData = posOfData;
        this.fullQuery = fullQuery;
        this.parameters = new ArrayList<Object[]>();
        this.insertQuery = fullQuery.substring(0, posOfData);
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        parameters.add(super.parameters);
        return connection.sendInsertRequest(insertQuery,
            new ValuesWithParametersInputFormat(fullQuery, posOfData, parameters));
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        executeUpdate();
        return null;
    }

    @Override
    public void addBatch() throws SQLException {
        parameters.add(super.parameters);
        super.parameters = new Object[super.parameters.length];
    }

    @Override
    public void clearBatch() throws SQLException {
        parameters.clear();
        super.parameters = new Object[super.parameters.length];
    }

    @Override
    public int[] executeBatch() throws SQLException {
        Integer rows = connection.sendInsertRequest(
            insertQuery, new ValuesWithParametersInputFormat(fullQuery, posOfData, parameters));
        int[] result = new int[rows];
        Arrays.fill(result, -1);
        return result;
    }

    private static int computeQuestionMarkSize(String query, int start) {
        int size = 0;
        QuotedToken token;
        QuotedLexer lexer = new QuotedLexer(query, start);

        while ((token = lexer.next()).type() != QuotedTokenType.EndOfStream) {
            if (token.type() == QuotedTokenType.QuestionMark) {
                size++;
            }
        }
        return size;
    }
}
