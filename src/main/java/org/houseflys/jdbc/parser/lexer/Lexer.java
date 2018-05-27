package org.houseflys.jdbc.parser.lexer;

public class Lexer {

    private int end;
    private int cursor;

    private final char[] data;

    public Lexer(char[] data) {
        this.data = data;
        this.end = data.length;
    }

    public Token next() {
        if (cursor >= end) {
            return new Token(TokenType.EndOfStream, end, end, data);
        }

        int tokenBegin = cursor;

        switch (data[cursor]) {
            case '(':
                return new Token(TokenType.OpeningRoundBracket, tokenBegin, ++cursor, data);
            case ')':
                return new Token(TokenType.ClosingRoundBracket, tokenBegin, ++cursor, data);
            case '[':
                return new Token(TokenType.OpeningSquareBracket, tokenBegin, ++cursor, data);
            case ']':
                return new Token(TokenType.ClosingSquareBracket, tokenBegin, ++cursor, data);
            case ',':
                return new Token(TokenType.Comma, tokenBegin, ++cursor, data);
            case ';':
                return new Token(TokenType.Semicolon, tokenBegin, ++cursor, data);
            case '.':
                return new Token(TokenType.Dot, tokenBegin, ++cursor, data);
            case '+':
                return new Token(TokenType.Plus, tokenBegin, ++cursor, data);
            case '-':
                return newMinusOrArrowToken(tokenBegin);
            case '*':
                return new Token(TokenType.Asterisk, tokenBegin, ++cursor, data);
            case '/':
                return newSlashOrCommentToken(tokenBegin);
            case '%':
                return new Token(TokenType.Percent, tokenBegin, ++cursor, data);
            case '=':
                return newEqualsToken(tokenBegin);
            case '!':
                return newNotEqualsToken(tokenBegin);
            case '<':
                break;
            case '>':
                break;
            case '?':
                return new Token(TokenType.QuestionMark, tokenBegin, ++cursor, data);
            case ':':
                return new Token(TokenType.Colon, tokenBegin, ++cursor, data);
            case '|':
                return newConcatenationToken(tokenBegin);
            case '"':
                return newQuotedString('"', TokenType.QuotedIdentifier, tokenBegin);
            case '`':
                return newQuotedString('`', TokenType.QuotedIdentifier, tokenBegin);
            case '\'':
                return newQuotedString('\'', TokenType.StringLiteral, tokenBegin);
            default:
                if (isWhitespace(data[cursor])) {
                    return new Token(TokenType.Whitespace, tokenBegin, ++cursor, data);
                } else if (isBareWord(data[cursor])) {
                    for (; cursor < end; cursor++) {
                        if (!isWordCharASCII(data[cursor])) {
                            break;
                        }
                    }
                    return new Token(TokenType.BareWord, tokenBegin, cursor, data);
                } else if (isNumericASCII(data[cursor])) {
                    return newNumericToken(tokenBegin);
                }
        }
        return new Token(TokenType.Error, -1, -1, null);
    }

    private Token newEqualsToken(int tokenBegin) {
        cursor++;
        if (cursor < end && data[cursor] == '=') {
            cursor++;
        }
        return new Token(TokenType.Equals, tokenBegin, cursor, data);
    }

    private Token newNumericToken(int tokenBegin) {
        boolean hex = false;
        if (cursor + 2 < end && data[cursor] == '0' && (data[cursor + 1] == 'x' || data[cursor + 1] == 'X')
            || data[cursor + 1] == 'b' || data[cursor + 1] == 'B') {

            cursor += 2;
            if (data[cursor + 1] == 'x' || data[cursor + 1] == 'X') {
                hex = true;
            }
        }

        for (; cursor < end; cursor++) {
            if (hex ? !isHexDigit(data[cursor]) : !isNumericASCII(data[cursor])) {
                break;
            }
        }

        if (cursor < end && data[cursor] == '.') {
            for (cursor++; cursor < end; cursor++) {
                if (hex ? !isHexDigit(data[cursor]) : !isNumericASCII(data[cursor])) {
                    break;
                }
            }
        }

        if (cursor + 1 < end && (hex ? (data[cursor] == 'p' || data[cursor] == 'P')
            : (data[cursor] == 'e' || data[cursor] == 'E'))) {

            cursor++;

            if (cursor + 1 < end && (data[cursor] == '-' || data[cursor] == '+')) {
                cursor++;
            }

            for (; cursor < end; cursor++) {
                if (!isNumericASCII(data[cursor])) {
                    break;
                }
            }
        }

        return new Token(TokenType.Number, tokenBegin, cursor, data);
    }

    private Token newMinusOrArrowToken(int start) {
        cursor++;
        if (cursor < end && data[cursor] == '>') {
            return new Token(TokenType.Arrow, start, (cursor += 2), data);
        } else if (cursor < end && data[cursor] == '-') {
            for (cursor++; cursor < end; cursor++) {
                if (data[cursor] == '\n') {
                    break;
                }
            }
            return new Token(TokenType.Comment, start, ++cursor, data);
        }
        return new Token(TokenType.Minus, start, ++cursor, data);
    }

    private Token newNotEqualsToken(int tokenBegin) {
        cursor++;
        if (cursor < end && data[cursor] == '=') {
            return new Token(TokenType.NotEquals, tokenBegin, ++cursor, data);
        }
        return new Token(TokenType.ErrorSingleExclamationMark, tokenBegin, ++cursor, data);
    }

    private Token newConcatenationToken(int tokenBegin) {
        cursor++;
        if (cursor < end && data[cursor] == '|') {
            return new Token(TokenType.Concatenation, tokenBegin, ++cursor, data);
        }
        return new Token(TokenType.ErrorSinglePipeMark, tokenBegin, cursor, data);
    }

    private Token newSlashOrCommentToken(int tokenBegin) {
        cursor++;
        if (cursor < end && (data[cursor] == '/' || data[cursor] == '*')) {
            if (data[cursor] == '/') {
                for (cursor++; cursor < end; cursor++) {
                    if (data[cursor] == '\n') {
                        break;
                    }
                }
                return new Token(TokenType.Comment, tokenBegin, ++cursor, data);
            } else if (data[cursor] == '*') {
                for (cursor++; cursor + 1 < end; cursor++) {
                    if (data[cursor] == '*' && data[cursor + 1] == '/') {
                        return new Token(TokenType.Comment, tokenBegin, (cursor += 2), data);
                    }
                }
            }
            return new Token(TokenType.ErrorMultilineCommentIsNotClosed, tokenBegin, cursor, data);
        }
        return new Token(TokenType.Slash, tokenBegin, cursor, data);
    }

    private Token newQuotedString(char quote, TokenType type, int tokenBegin) {
        for (cursor++; cursor < end; cursor++) {
            if (data[cursor] == '\\') {
                cursor++;
            } else if (data[cursor] == quote) {
                return new Token(type, tokenBegin, ++cursor, data);
            }
        }
        return new Token(TokenType.Error, tokenBegin, cursor, data);
    }

    private boolean isNumericASCII(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isWordCharASCII(char c) {
        return isBareWord(c) || isNumericASCII(c);
    }

    private boolean isBareWord(char c) {
        return '_' == c || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isHexDigit(char c) {
        return isNumericASCII(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
    }
}
