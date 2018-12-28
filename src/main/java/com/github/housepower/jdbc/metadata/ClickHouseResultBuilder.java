package com.github.housepower.jdbc.metadata;

import com.github.housepower.jdbc.ClickHouseResultSet;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.data.Column;
import com.github.housepower.jdbc.data.IDataType;
import com.github.housepower.jdbc.data.type.*;
import com.github.housepower.jdbc.misc.CheckedIterator;
import com.github.housepower.jdbc.protocol.DataResponse;
import com.github.housepower.jdbc.statement.ClickHouseStatement;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ClickHouseResultBuilder {

    public static Map<String, IDataType> creators = new ConcurrentHashMap<>();

    static {

        creators.put("UUID", new DataTypeUUID());
        creators.put("String", new DataTypeString());
        creators.put("Float32", new DataTypeFloat32());
        creators.put("Float64", new DataTypeFloat64());

        creators.put("Int8", new DataTypeInt8("Int8"));
        creators.put("Int16", new DataTypeInt16("Int16"));
        creators.put("Int32", new DataTypeInt32("Int32"));
        creators.put("Int64", new DataTypeInt64("Int64"));
        creators.put("UInt8", new DataTypeInt8("UInt8"));
        creators.put("UInt16", new DataTypeInt16("UInt16"));
        creators.put("UInt32", new DataTypeInt32("UInt32"));
        creators.put("UInt64", new DataTypeInt64("UInt64"));
    }

    private final int columnsNum;
    private List<String> names;
    private List<IDataType> types;
    private List<List<?>> rows = new ArrayList<List<?>>();
    private Statement statement;

    public static ClickHouseResultBuilder builder(int columnsNum) {
        return new ClickHouseResultBuilder(columnsNum);
    }

    private ClickHouseResultBuilder(int columnsNum) {
        this.columnsNum = columnsNum;
    }

    public ClickHouseResultBuilder names(String... names) {
        return names(Arrays.asList(names));
    }

    public ClickHouseResultBuilder types(String... types) {
        return types(Arrays.asList(types));
    }

    public ClickHouseResultBuilder addRow(Object... row) {
        return addRow(Arrays.asList(row));
    }


    public ClickHouseResultBuilder names(List<String> names) {
        if (names.size() != columnsNum) {
            throw new IllegalArgumentException("size mismatch, req: " + columnsNum + " got: " + names.size());
        }
        this.names = names;
        return this;
    }

    public ClickHouseResultBuilder types(List<String> types) {
        if (types.size() != columnsNum) {
            throw new IllegalArgumentException("size mismatch, req: " + columnsNum + " got: " + types.size());
        }
        this.types = toIDataTypes(types);
        return this;
    }

    private List<IDataType> toIDataTypes(List<String> types) {
        List<IDataType> ret = new ArrayList<>();
        for (String type : types) {
            ret.add(creators.get(type));
        }
        return ret;
    }

    public ClickHouseResultBuilder addRow(List<?> row) {
        if (row.size() != columnsNum) {
            throw new IllegalArgumentException("size mismatch, req: " + columnsNum + " got: " + row.size());
        }
        rows.add(row);
        return this;
    }

    /**
     * @return 拿当前数据构建一个数据集.
     */
    public ClickHouseResultSet build() {
        if (names == null) {
            throw new IllegalStateException("names == null");
        }
        if (types == null) {
            throw new IllegalStateException("types == null");
        }
        Block header = new Block(rows.size(), toColumns(names, types, rows));
        ClickHouseResultSet crs = new ClickHouseResultSet(header,
                new CheckedIterator<DataResponse, SQLException>() {
                    @Override
                    public boolean hasNext() throws SQLException {
                        return false;
                    }

                    @Override
                    public DataResponse next() throws SQLException {
                        return null;
                    }
                }, (ClickHouseStatement) this.statement);

        return crs;
    }

    private Column[] toColumns(List<String> names, List<IDataType> types, List<List<?>> rows) {
        Column[] ret = new Column[names.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Column(names.get(i), types.get(i), pickRows(rows, i));
        }
        return ret;
    }

    private Object[] pickRows(List<List<?>> rows, int i) {
        Object[] ret = new Object[rows.size()];
        int j = 0;
        for (List<?> row : rows) {
            ret[j] = row.get(i);
            j++;
        }
        return ret;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }
}