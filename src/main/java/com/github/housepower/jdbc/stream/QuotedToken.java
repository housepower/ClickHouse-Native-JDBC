package com.github.housepower.jdbc.stream;

public class QuotedToken {
    private final int end;
    private final int start;
    private final String query;
    private final QuotedTokenType type;

    private String data;

    QuotedToken(QuotedTokenType type) {
        this(type, "", 0, 0);
    }

    QuotedToken(QuotedTokenType type, String query, int start, int end) {
        this.end = end;
        this.type = type;
        this.query = query;
        this.start = start;
    }

    public String data() {
        return data == null ? (data = query.substring(start, end)) : data;
    }

    public QuotedTokenType type() {
        return type;
    }
}
