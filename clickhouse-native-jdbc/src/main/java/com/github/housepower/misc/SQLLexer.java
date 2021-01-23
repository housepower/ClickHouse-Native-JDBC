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

package com.github.housepower.misc;

import java.sql.SQLException;

public class SQLLexer {
    private int pos;
    private final String data;

    public SQLLexer(int pos, String data) {
        this.pos = pos;
        this.data = data;
    }

    public char character() {
        return eof() ? 0 : data.charAt(pos++);
    }

    // only support dec
    public int intLiteral() {
        skipAnyWhitespace();

        int start = pos;

        if (isCharacter('-') || isCharacter('+'))
            pos++;

        for (; pos < data.length(); pos++)
            if (!isNumericASCII(data.charAt(pos)))
                break;

        return Integer.parseInt(new StringView(data, start, pos).toString());
    }

    public Number numberLiteral() {
        skipAnyWhitespace();

        int start = pos;
        // @formatter:off
        boolean isHex       = false;
        boolean isBinary    = false;
        boolean isDouble    = false;
        boolean hasExponent = false;
        boolean hasSigned   = false;
        // @formatter:on

        if (isCharacter('-') || isCharacter('+')) {
            hasSigned = true;
            pos++;
        }

        if (pos + 2 < data.length()) {
            // @formatter:off
            if (data.charAt(pos) == '0' && (data.charAt(pos + 1) == 'x' || data.charAt(pos + 1) == 'X'
                                         || data.charAt(pos + 1) == 'b' || data.charAt(pos + 1) == 'B')) {
                isHex    = data.charAt(pos + 1) == 'x' || data.charAt(pos + 1) == 'X';
                isBinary = data.charAt(pos + 1) == 'b' || data.charAt(pos + 1) == 'B';
                pos += 2;
            }
            // @formatter:on
        }

        for (; pos < data.length(); pos++) {
            if (isHex ? !isHexDigit(data.charAt(pos)) : !isNumericASCII(data.charAt(pos))) {
                break;
            }
        }

        if (pos < data.length() && data.charAt(pos) == '.') {
            isDouble = true;
            for (pos++; pos < data.length(); pos++) {
                if (isHex ? !isHexDigit(data.charAt(pos)) : !isNumericASCII(data.charAt(pos)))
                    break;
            }
        }

        if (pos + 1 < data.length()
                // @formatter:off
                && (isHex ? (data.charAt(pos) == 'p' || data.charAt(pos) == 'P')
                          : (data.charAt(pos) == 'e' || data.charAt(pos) == 'E'))) {
                // @formatter:on
            hasExponent = true;
            pos++;

            if (pos + 1 < data.length() && (data.charAt(pos) == '-' || data.charAt(pos) == '+')) {
                pos++;
            }

            for (; pos < data.length(); pos++) {
                char ch = data.charAt(pos);
                if (!isNumericASCII(ch)) {
                    break;
                }
            }
        }

        if (isBinary) {
            String signed = hasSigned ? data.charAt(start) + "" : "";
            int begin = start + (hasSigned ? 3 : 2);
            return Long.parseLong(signed + new StringView(data, begin, pos).toString(), 2);
        } else if (isDouble || hasExponent) {
            return Double.valueOf(new StringView(data, start, pos).toString());
        } else if (isHex) {
            String signed = hasSigned ? data.charAt(start) + "" : "";
            int begin = start + (hasSigned ? 3 : 2);
            return Long.parseLong(signed + new StringView(data, begin, pos), 16);
        } else {
            return Long.parseLong(new StringView(data, start, pos).toString());
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
        return pos >= data.length();
    }

    public boolean isCharacter(char ch) {
        return !eof() && data.charAt(pos) == ch;
    }

    public StringView bareWord() throws SQLException {
        skipAnyWhitespace();
        // @formatter:off
        if (isCharacter('`')) {
            return stringLiteralWithQuoted('`');
        } else if (isCharacter('"')) {
            return stringLiteralWithQuoted('"');
        } else if (data.charAt(pos) == '_'
               || (data.charAt(pos) >= 'a' && data.charAt(pos) <= 'z')
               || (data.charAt(pos) >= 'A' && data.charAt(pos) <= 'Z')) {
            int start = pos;
            for (pos++; pos < data.length(); pos++) {
                if (!('_' == data.charAt(pos)
                  || (data.charAt(pos) >= 'a' && data.charAt(pos) <= 'z')
                  || (data.charAt(pos) >= 'A' && data.charAt(pos) <= 'Z')
                  || (data.charAt(pos) >= '0' && data.charAt(pos) <= '9'))) {
                    break;
                }
            }
            return new StringView(data, start, pos);
        }
        // @formatter:on
        throw new SQLException("Expect Bare Token.");
    }

    public boolean isWhitespace() {
        return data.charAt(pos++) == ' ';
    }

    private boolean isNumericASCII(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(char c) {
        return isNumericASCII(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private void skipAnyWhitespace() {
        for (; pos < data.length(); pos++) {
            // @formatter:off
            if (data.charAt(pos) != ' '
             && data.charAt(pos) != '\t'
             && data.charAt(pos) != '\n'
             && data.charAt(pos) != '\r'
             && data.charAt(pos) != '\f') {
                return;
            }
            // @formatter:on
        }
    }

    private StringView stringLiteralWithQuoted(char quoted) throws SQLException {
        int start = pos;
        Validate.isTrue(data.charAt(pos) == quoted);
        for (pos++; pos < data.length(); pos++) {
            if (data.charAt(pos) == '\\')
                pos++;
            else if (data.charAt(pos) == quoted)
                return new StringView(data, start + 1, pos++);
        }
        throw new SQLException("The String Literal is no Closed.");
    }
}
