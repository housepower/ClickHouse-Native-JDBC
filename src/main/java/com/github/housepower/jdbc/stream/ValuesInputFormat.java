package com.github.housepower.jdbc.stream;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.data.Column;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;
import java.util.Arrays;

public class ValuesInputFormat implements InputFormat {
    private final SQLLexer lexer;

    public ValuesInputFormat(int pos, String data) throws SQLException {
        this.lexer = new SQLLexer(pos, data);
    }

    @Override
    public Block next(Block header, int maxRows) throws SQLException {
        Object[][] columnData = new Object[header.columns()][maxRows];

        for (int row = 0; row < maxRows; row++) {
            char nextChar = lexer.character();
            if (lexer.eof() || nextChar == ';')
                return newValuesBlock(header, row, columnData);

            if (row > 0 && nextChar == ',')
                nextChar = lexer.character();
            Validate.isTrue(nextChar == '(');
            for (int column = 0; column < header.columns(); column++) {
                if (column > 0)
                    Validate.isTrue(lexer.character() == ',');

                columnData[column][row] = header.getByPosition(column).type().deserializeTextQuoted(lexer);
            }
            Validate.isTrue(lexer.character() == ')');
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
