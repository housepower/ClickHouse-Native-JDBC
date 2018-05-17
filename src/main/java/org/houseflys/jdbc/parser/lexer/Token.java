package org.houseflys.jdbc.parser.lexer;

public final class Token {
    private final int end;
    private final int start;
    private final char[] data;
    private final TokenType type;

    public Token(TokenType type, int start, int end, char[] data) {
        this.end = end;
        this.type = type;
        this.data = data;
        this.start = start;
    }

    public int end() {
        return end;
    }

    public int start() {
        return start;
    }

    public char[] data() {
        return data;
    }

    public TokenType type() {
        return type;
    }
}
