package org.houseflys.jdbc.data;

public class ParseResult {
    private final int pos;
    private final IDataType dataType;

    public ParseResult(int pos, IDataType dataType) {
        this.pos = pos;
        this.dataType = dataType;
    }

    public int pos() {
        return pos;
    }

    public IDataType dataType() {
        return dataType;
    }
}
