package org.houseflys.jdbc.stream;

public class QuotedToken {
    private final String data;
    private final QuotedTokenType type;

    QuotedToken(QuotedTokenType type) {
        this.type = type;
        this.data = new String();
    }

    QuotedToken(QuotedTokenType type, String data) {
        this.type = type;
        this.data = data;
    }

    public String data() {
        return data;
    }

    public QuotedTokenType type() {
        return type;
    }
}
