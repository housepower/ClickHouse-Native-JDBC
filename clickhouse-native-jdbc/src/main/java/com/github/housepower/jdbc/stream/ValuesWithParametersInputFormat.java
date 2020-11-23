/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc.stream;

import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.SQLLexer;
import com.github.housepower.jdbc.misc.Validate;

import java.sql.SQLException;

public class ValuesWithParametersInputFormat implements InputFormat {

    private final SQLLexer lexer;

    public ValuesWithParametersInputFormat(String query, int pos) throws SQLException {
        this.lexer = new SQLLexer(pos, query);
    }

    @Override
    public void fillBlock(Block block) throws SQLException {
        int[] constIdx = new int[block.columns()];
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
                block.incIndex(column);
            }
        }
        Validate.isTrue(lexer.character() == ')');
    }
}
