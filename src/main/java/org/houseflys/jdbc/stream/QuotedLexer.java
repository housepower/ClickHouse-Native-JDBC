package org.houseflys.jdbc.stream;

import java.util.ArrayList;
import java.util.List;

public class QuotedLexer {

    private int pos;
    private int tPos;
    private final String in;
    private final List<QuotedToken> tokens;

    public QuotedLexer(String in) {
        this.in = in;
        this.tokens = new ArrayList<QuotedToken>();
    }

    public QuotedLexer(String in, int pos) {
        this.in = in;
        this.pos = pos;
        this.tokens = new ArrayList<QuotedToken>();
    }

    public QuotedToken next() {
        for (int i = tPos; ; i++) {
            if (tokens.size() == i) {
                tokens.add(nextImpl());
            }
            QuotedToken token = tokens.get(i);
            if (token.type() != QuotedTokenType.Whitespace) {
                tPos = ++i;
                return token;
            }
        }
    }

    public QuotedToken prev() {
        return tokens.get(--tPos);
    }

    public void reset() {
        tPos = 0;
    }

    private QuotedToken nextImpl() {
        if (pos >= in.length()) {
            return new QuotedToken(QuotedTokenType.EndOfStream);
        }

        char ch = in.charAt(pos++);

        switch (ch) {
            case '\'':
                return newQuotedString(pos - 1, '\'', QuotedTokenType.StringLiteral);
            case '"':
                return newQuotedString(pos - 1, '"', QuotedTokenType.BareWord);
            case '`':
                return newQuotedString(pos - 1, '`', QuotedTokenType.BareWord);
            case '(':
                return new QuotedToken(QuotedTokenType.OpeningRoundBracket);
            case ')':
                return new QuotedToken(QuotedTokenType.ClosingRoundBracket);
            case '[':
                return new QuotedToken(QuotedTokenType.OpeningSquareBracket);
            case ']':
                return new QuotedToken(QuotedTokenType.ClosingSquareBracket);
            case ',':
                return new QuotedToken(QuotedTokenType.Comma);
            case ';':
                return new QuotedToken(QuotedTokenType.Semicolon);
            case '.':
                return new QuotedToken(QuotedTokenType.Dot);
            case '*':
                return new QuotedToken(QuotedTokenType.Asterisk);
            case '/':
                return new QuotedToken(QuotedTokenType.Slash);
            case '%':
                return new QuotedToken(QuotedTokenType.Percent);
            case '!':
                return new QuotedToken(QuotedTokenType.Not);
            case '<':
                return new QuotedToken(QuotedTokenType.Less);
            case '>':
                return new QuotedToken(QuotedTokenType.Greater);
            case ':':
                return new QuotedToken(QuotedTokenType.Colon);
            case '|':
                return new QuotedToken(QuotedTokenType.Concatenation);
            case '?':
                return new QuotedToken(QuotedTokenType.QuestionMark);
            case '=':
                return new QuotedToken(QuotedTokenType.Equals);
            default:
                if (isWhitespace(ch)) {
                    return new QuotedToken(QuotedTokenType.Whitespace);
                } else if (isBareWord(ch)) {
                    int start = pos - 1;
                    for (; pos < in.length(); pos++) {
                        if (!isWordCharASCII(in.charAt(pos))) {
                            break;
                        }
                    }
                    return new QuotedToken(QuotedTokenType.BareWord, in, start, pos);
                } else if (isNumericASCII(ch)) {
                    return newNumericToken(pos - 1);
                } else if (ch == '-' || ch == '+') {
                    if (isNumericASCII(in.charAt(pos))) {
                        return newNumericToken(pos - 1);
                    }
                    return new QuotedToken(ch == '+' ? QuotedTokenType.Plus : QuotedTokenType.Minus);
                }
        }
        return new QuotedToken(QuotedTokenType.Error);
    }

    private QuotedToken newQuotedString(int start, char quoted, QuotedTokenType type) {
        pos = start;
        int end = in.length();

        for (pos++; pos < end; pos++) {
            if (in.charAt(pos) == '\\') {
                pos++;
            } else if (in.charAt(pos) == quoted) {
                pos++;
                return new QuotedToken(type, in, start + 1, pos - 1);
            }
        }
        return new QuotedToken(QuotedTokenType.Error);
    }

    private QuotedToken newNumericToken(int start) {
        boolean hex = false;

        pos = start;
        int end = in.length();


        if (in.charAt(pos) == '-' || in.charAt(pos) == '+') {
            pos++;
        }

        if (pos + 2 < end) {
            char[] twoChars = new char[] {in.charAt(pos), in.charAt(pos + 1)};

            if (twoChars[0] == '0' && (twoChars[1] == 'x' || twoChars[1] == 'X' || twoChars[1] == 'b'
                || twoChars[1] == 'B')) {
                pos += 2;

                if (twoChars[1] == 'x' || twoChars[1] == 'X')
                    hex = true;
            }
        }

        for (; pos < end; pos++) {
            char ch = in.charAt(pos);
            if (hex ? !isHexDigit(ch) : !isNumericASCII(ch))
                break;
        }

        if (pos < end && in.charAt(pos) == '.') {
            for (pos++; pos < end; pos++) {
                char ch = in.charAt(pos);
                if (hex ? !isHexDigit(ch) : !isNumericASCII(ch))
                    break;
            }
        }

        if (pos + 1 < end && (hex ?
            (in.charAt(pos) == 'p' || in.charAt(pos) == 'P') :
            (in.charAt(pos) == 'E' || in.charAt(pos) == 'e'))) {

            pos++;

            if (pos + 1 < end && (in.charAt(pos) == '-' || in.charAt(pos) == '+')) {
                pos++;
            }

            for (; pos < end; pos++) {
                char ch = in.charAt(pos);
                if (!isNumericASCII(ch)) {
                    break;
                }
            }

        }

        return new QuotedToken(QuotedTokenType.Number, in, start, pos);
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

    public int pos() {
        return pos;
    }
}
