package com.github.housepower.jdbc.stream;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;

public class ValuesWithParametersInputFormat implements InputFormat {

    private SQLLexer lexer;

    public ValuesWithParametersInputFormat(String query, int pos)
        throws SQLException {
        this.lexer = new SQLLexer(pos, query);
    }


    @Override
    public void fillBlock(Block block) throws SQLException {
        int[] constIdx = new int[block.columns()];
        block.resetIndex();
        char nextChar = lexer.character();
        Validate.isTrue(nextChar == '(');
        for (int column = 0; column < block.columns(); column++) {
            if (column > 0) {
                Validate.isTrue(lexer.character() == ',');
            }

            if (!lexer.isCharacter('?')) {
                block.setConstObject(column, block.getByPosition(column).type()
                                                 .deserializeTextQuoted(lexer));
                constIdx[column] = 1;
            } else {
                lexer.character();
            }
        }

        for (int column = 0; column < block.columns(); column++) {
            if (constIdx[column] > 0) {
                block.incrIndex(column);
            }
        }
        Validate.isTrue(lexer.character() == ')');
    }
}
