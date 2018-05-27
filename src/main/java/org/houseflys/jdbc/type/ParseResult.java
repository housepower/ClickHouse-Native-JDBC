package org.houseflys.jdbc.type;

public class ParseResult {
    private final int pos;
    private final String type;
    private final ColumnCreator creator;

    public ParseResult(int pos, String type, ColumnCreator creator) {
        this.pos = pos;
        this.type = type;
        this.creator = creator;
    }

    public int pos() {
        return pos;
    }

    public String type() {
        return type;
    }

    public ColumnCreator creator() {
        return creator;
    }
}
