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

package com.github.housepower.jdbc.misc;

import java.sql.SQLException;

public class SQLLexer {
    private int pos;
    private final char[] data;

    public SQLLexer(int pos, String data) {
        this.pos = pos;
        this.data = data.toCharArray();
    }

    public char character() {
        return eof() ? 0 : data[pos++];
    }

    // only support dec
    public int intLiteral() {
        skipAnyWhitespace();

        int start = pos;

        if (isCharacter('-') || isCharacter('+'))
            pos++;

        for (; pos < data.length; pos++)
            if (!isNumericASCII(data[pos]))
                break;

        return Integer.parseInt(new StringView(start, pos, data).toString());
    }

    public Number numberLiteral() {
        skipAnyWhitespace();

        int start = pos;
        boolean isHex = false;
        boolean isBinary = false;
        boolean isDouble = false;
        boolean hasExponent = false;
        boolean hasSigned = false;

        if (isCharacter('-') || isCharacter('+')) {
            hasSigned = true;
            pos++;
        }

        if (pos + 2 < data.length) {
            if (data[pos] == '0'
                    && (data[pos + 1] == 'x' || data[pos + 1] == 'X' || data[pos + 1] == 'b' || data[pos + 1] == 'B')) {
                isHex = data[pos + 1] == 'x' || data[pos + 1] == 'X';
                isBinary = data[pos + 1] == 'b' || data[pos + 1] == 'B';
                pos += 2;
            }
        }

        for (; pos < data.length; pos++) {
            if (isHex ? !isHexDigit(data[pos]) : !isNumericASCII(data[pos])) {
                break;
            }
        }

        if (pos < data.length && data[pos] == '.') {
            isDouble = true;
            for (pos++; pos < data.length; pos++) {
                if (isHex ? !isHexDigit(data[pos]) : !isNumericASCII(data[pos]))
                    break;
            }
        }

        if (pos + 1 < data.length
                && (isHex ? (data[pos] == 'p' || data[pos] == 'P') : (data[pos] == 'e' || data[pos] == 'E'))) {
            hasExponent = true;
            pos++;

            if (pos + 1 < data.length && (data[pos] == '-' || data[pos] == '+')) {
                pos++;
            }

            for (; pos < data.length; pos++) {
                char ch = data[pos];
                if (!isNumericASCII(ch)) {
                    break;
                }
            }
        }

        if (isBinary) {
            String signed = hasSigned ? data[start] + "" : "";
            int begin = start + (hasSigned ? 3 : 2);
            return Long.parseLong(signed + new String(data, begin, pos - begin), 2);
        } else if (isDouble || hasExponent) {
            return Double.valueOf(new String(data, start, pos - start));
        } else if (isHex) {
            String signed = hasSigned ? data[start] + "" : "";
            int begin = start + (hasSigned ? 3 : 2);
            return Long.parseLong(signed + new String(data, begin, pos - begin), 16);
        } else {
            return Long.parseLong(new String(data, start, pos - start));
        }
    }

    public String stringLiteral() throws SQLException {
        return stringView().toString();
    }

    public StringView stringView() throws SQLException {
        skipAnyWhitespace();
        Validate.isTrue(isCharacter('\''));
        return stringLiteralWithQuoted('\'');
    }

    public boolean eof() {
        skipAnyWhitespace();
        return pos >= data.length;
    }

    public boolean isCharacter(char ch) {
        return !eof() && data[pos] == ch;
    }

    public StringView bareWord() throws SQLException {
        skipAnyWhitespace();
        if (isCharacter('`')) {
            return stringLiteralWithQuoted('`');
        } else if (isCharacter('"')) {
            return stringLiteralWithQuoted('"');
        } else if ('_' == data[pos] || (data[pos] >= 'a' && data[pos] <= 'z')
                || (data[pos] >= 'A' && data[pos] <= 'Z')) {
            int start = pos;
            for (pos++; pos < data.length; pos++) {
                if (!('_' == data[pos] || (data[pos] >= 'a' && data[pos] <= 'z')
                        || (data[pos] >= 'A' && data[pos] <= 'Z') || (data[pos] >= '0' && data[pos] <= '9')))
                    break;
            }
            return new StringView(start, pos, data);
        }
        throw new SQLException("Expect Bare Token.");
    }

    public boolean isWhitespace() {
        return data[pos++] == ' ';
    }

    private boolean isNumericASCII(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(char c) {
        return isNumericASCII(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private void skipAnyWhitespace() {
        for (; pos < data.length; pos++)
            if (data[pos] != ' ' && data[pos] != '\t' && data[pos] != '\n' && data[pos] != '\r' && data[pos] != '\f')
                return;
    }

    private StringView stringLiteralWithQuoted(char quoted) throws SQLException {
        int start = pos;
        Validate.isTrue(data[pos] == quoted);
        for (pos++; pos < data.length; pos++) {
            if (data[pos] == '\\')
                pos++;
            else if (data[pos] == quoted)
                return new StringView(start + 1, pos++, data);
        }
        throw new SQLException("The String Literal is no Closed.");
    }
}
