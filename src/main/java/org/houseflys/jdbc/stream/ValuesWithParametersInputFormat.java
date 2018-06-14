package org.houseflys.jdbc.stream;


import org.houseflys.jdbc.data.Block;
import org.houseflys.jdbc.data.Column;
import org.houseflys.jdbc.misc.Validate;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ValuesWithParametersInputFormat implements InputFormat {
    private final QuotedLexer lexer;
    private final Iterator<Object[]> iterator;

    private int pPos;
    private Object[] parameters;

    public ValuesWithParametersInputFormat(String query, int pos, List<Object[]> parameters) {
        this.iterator = parameters.iterator();
        this.lexer = new QuotedLexer(query, pos);
        this.parameters = iterator.hasNext() ? iterator.next() : new Object[0];
    }


    @Override
    public Block next(Block header, int maxRows) throws SQLException {
        Object[][] columnData = new Object[header.columns()][maxRows];

        for (int row = 0; row < maxRows; row++) {
            QuotedToken token = lexer.next();
            if (isEndToken(token)) {
                if (!iterator.hasNext())
                    return newPreparedBlock(header, row, columnData);

                pPos = 0;
                lexer.reset();
                parameters = iterator.next();
                token = lexer.next();
            }

            Validate.isTrue(token.type() == QuotedTokenType.OpeningRoundBracket, "Expected OpeningRoundBracket.");
            for (int i = 0; i < header.columns(); i++) {
                Column column = header.getByPosition(i);

                columnData[i][row] =
                    lexer.next().type() == QuotedTokenType.QuestionMark ? parameters[pPos++] : getQuotedObject(column);

                token = lexer.next();
                Validate.isTrue(
                    token.type() == QuotedTokenType.ClosingRoundBracket || token.type() == QuotedTokenType.Comma, "");
            }
        }
        return newPreparedBlock(header, maxRows, columnData);
    }

    private boolean isEndToken(QuotedToken token) {
        return token.type() == QuotedTokenType.EndOfStream || token.type() == QuotedTokenType.Semicolon;
    }

    private Object getQuotedObject(Column column) throws SQLException {
        lexer.prev();
        return column.type().deserializeTextQuoted(lexer);
    }

    private Block newPreparedBlock(Block header, int rows, Object[][] columnData) throws SQLException {
        Validate.isTrue(header.columns() == columnData.length, "");

        Column[] columns = new Column[columnData.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Column(header.getByPosition(i).name(),
                header.getByPosition(i).type(), Arrays.copyOf(columnData[i], rows));
        }
        return new Block(rows, columns);
    }
}
