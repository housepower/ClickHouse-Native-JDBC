package com.github.housepower.jdbc.stream;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;

public class ValuesInputFormat implements InputFormat {

    private final SQLLexer lexer;

    public ValuesInputFormat(int pos, String data) throws SQLException {
        this.lexer = new SQLLexer(pos, data);
    }


    @Override
    public void fillBlock(Block block) throws SQLException {
        int[] constIdx = new int[block.columns()];
        for (; ; ) {
            char nextChar = lexer.character();
            if (lexer.eof() || nextChar == ';') {
                break;
            }

            if (nextChar == ',') {
                nextChar = lexer.character();
            }
            Validate.isTrue(nextChar == '(');
            for (int column = 0; column < block.columns(); column++) {
                if (column > 0) {
                    Validate.isTrue(lexer.character() == ',');
                }
                constIdx[column] = 1;
                block.setConstObject(column, block.getByPosition(column).type()
                                                 .deserializeTextQuoted(lexer));
            }
            Validate.isTrue(lexer.character() == ')');
            block.appendRow();
        }

        for (int column = 0; column < block.columns(); column++) {
            if (constIdx[column] > 0) {
                block.incrIndex(column);
            }
        }
    }
}
