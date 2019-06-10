package com.github.housepower.jdbc.misc;

import java.sql.SQLException;

public class SQLLexer {
    private int pos;
    private final char[] data;

    public SQLLexer(int pos, String data) throws SQLException {
        this.pos = pos;
        this.data = StringViewCoding.getValues(data);
    }

    public char character() {
        return eof() ? 0 : data[pos++];
    }

    public Number numberLiteral() {
        skipAnyWhitespace();

        int start = pos;
        boolean isHex = false;
        boolean isDouble = false;

        if (isCharacter('-') || isCharacter('+'))
            pos++;

        if (pos + 2 < data.length) {
            if (data[pos] == '0' && (Character.toLowerCase(data[pos + 1]) == 'x'
                || Character.toLowerCase(data[pos + 1]) == 'b')) {
                pos += 2;
                isHex = Character.toLowerCase(data[pos + 1]) == 'x';
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

        if (pos + 1 < data.length && (isHex ? (data[pos] == 'p' || data[pos] == 'P') :
            (data[pos] == 'E' || data[pos] == 'e'))) {

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
        return isDouble ? Double.valueOf(String.valueOf(new StringView(start, pos, data))) :
            Long.valueOf(String.valueOf(new StringView(start, pos, data)));
    }

    public StringView stringLiteral() throws SQLException {
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
        } else if ('_' == data[pos] || (data[pos] >= 'a' && data[pos] <= 'z') || (data[pos] >= 'A'
            && data[pos] <= 'Z')) {
            int start = pos;
            for (pos++; pos < data.length; pos++) {
                if (!('_' == data[pos] || (data[pos] >= 'a' && data[pos] <= 'z') || (data[pos] >= 'A'
                    && data[pos] <= 'Z') || (data[pos] >= '0' && data[pos] <= '9')))
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
        for (; pos < data.length; pos++) {
            if (!(data[pos] == ' ' || data[pos] == '\t' || data[pos] == '\n' || data[pos] == '\r'
                || data[pos] == '\f')) {
                return;
            }
        }
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
