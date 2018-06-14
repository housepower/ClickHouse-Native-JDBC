package org.houseflys.jdbc.stream;

import org.houseflys.jdbc.data.Block;
import org.houseflys.jdbc.data.Column;
import org.houseflys.jdbc.misc.Validate;

import java.sql.SQLException;
import java.util.Arrays;

public class ValuesInputFormat implements InputFormat {
    private final QuotedLexer lexer;

    public ValuesInputFormat(int pos, String data) {
        this.lexer = new QuotedLexer(data, pos);
    }

    @Override
    public Block next(Block header, int maxRows) throws SQLException {
        Object[][] columnData = new Object[header.columns()][maxRows];

        for (int row = 0; row < maxRows; row++) {
            QuotedToken quotedToken = lexer.next();
            if (quotedToken.type() == QuotedTokenType.EndOfStream || quotedToken.type() == QuotedTokenType.Semicolon) {
                return newValuesBlock(header, row, columnData);
            }

            Validate.isTrue(quotedToken.type() == QuotedTokenType.OpeningRoundBracket, "Expected OpeningRoundBracket.");

            for (int i = 0; i < header.columns(); i++) {
                Column column = header.getByPosition(i);

                columnData[i][row] = column.type().deserializeTextQuoted(this.lexer);

                quotedToken = lexer.next();
                Validate.isTrue(quotedToken.type() == QuotedTokenType.ClosingRoundBracket
                    || quotedToken.type() == QuotedTokenType.Comma, "");
            }
        }
        return newValuesBlock(header, maxRows, columnData);
    }

    private Block newValuesBlock(Block header, int rows, Object[][] columnData) throws SQLException {
        Validate.isTrue(header.columns() == columnData.length, "");

        Column[] columns = new Column[columnData.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Column(header.getByPosition(i).name(),
                header.getByPosition(i).type(), Arrays.copyOf(columnData[i], rows));
        }
        return new Block(rows, columns);
    }
}
