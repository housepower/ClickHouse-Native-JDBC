package org.houseflys.jdbc.data;

public class Column {

    private final String name;
    private final String type;
    private final Object[] rowsData;

    public Column(String name, String type, Object[] rowsData) {
        this.name = name;
        this.type = type;
        this.rowsData = rowsData;
    }

    public String name() {
        return this.name;
    }

    public String type() {
        return this.type;
    }

    public Object[] data() {
        return rowsData;
    }

    public Object data(int rows) {
        return rowsData[rows];
    }

    //    public abstract int typeWithSQL() throws SQLException;

    //    public abstract void writeImpl(BinarySerializer serializer) throws IOException;

    //    public void writeTo(BinarySerializer serializer) throws IOException {
    //        serializer.writeStringBinary(name);
    //        serializer.writeStringBinary(type);
    //
    //        writeImpl(serializer);
    //    }
}
