package com.github.housepower.jdbc.stream;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.data.Column;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ValuesWithParametersInputFormat implements InputFormat {
    private final Iterator<Object[]> iterator;

    private int pPos;
    private SQLLexer lexer;
    private Object[] parameters;

    private final int pos;
    private final String query;

    public ValuesWithParametersInputFormat(String query, int pos, List<Object[]> parameters) throws SQLException {
        this.pos = pos;
        this.query = query;
        this.iterator = parameters.iterator();
        this.lexer = new SQLLexer(pos, query);
        this.parameters = iterator.hasNext() ? iterator.next() : new Object[0];
    }


    @Override
    public Block next(Block header, int maxRows) throws SQLException {
        Object[][] columnData = new Object[header.columns()][maxRows];

        for (int row = 0; row < maxRows; row++) {
            char nextChar = lexer.character();
            if (lexer.eof() || nextChar == ';') {
                if (!iterator.hasNext())
                    return newPreparedBlock(header, row, columnData);
                pPos=0;
                parameters = iterator.next();
                lexer = new SQLLexer(pos, query);
                nextChar = lexer.character();
            }

            Validate.isTrue(nextChar == '(');
            for (int column = 0; column < header.columns(); column++) {
                if (column > 0)
                    Validate.isTrue(lexer.character() == ',');

                columnData[column][row] = lexer.isCharacter('?') ? quotedParameter() :
                    header.getByPosition(column).type().deserializeTextQuoted(lexer);
            }
            Validate.isTrue(lexer.character() == ')');
        }
        return newPreparedBlock(header, maxRows, columnData);
    }

    private Object quotedParameter() throws SQLException {
        Validate.isTrue(lexer.character() == '?');
        return parameters[pPos++];
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
