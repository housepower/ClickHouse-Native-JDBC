package com.github.housepower.jdbc.data;

public class Column {

    private final String name;
    private final IDataType type;
    private final Object[] rowsData;

    public Column(String name, IDataType type, Object[] rowsData) {
        this.name = name;
        this.type = type;
        this.rowsData = rowsData;
    }

    public String name() {
        return this.name;
    }

    public IDataType type() {
        return this.type;
    }

    public Object[] data() {
        return rowsData;
    }

    public Object data(int rows) {
        return rowsData[rows];
    }


}
