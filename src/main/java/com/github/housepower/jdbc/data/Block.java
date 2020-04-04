package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.data.BlockSettings.Setting;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Block {

    private final Column[] columns;
    private final BlockSettings settings;
    private final Map<String, Integer> nameWithPosition;

    private Object[]  objects;
    private int[] columnIndexAdds;
    private int rows;

    public Block() {
        this(0, new Column[0]);
    }

    public Block(int rows, Column[] columns) {
        this(rows, columns, new BlockSettings(Setting.values()));
    }

    public Block(int rows, Column[] columns, BlockSettings settings) {
        this.rows = rows;
        this.columns = columns;
        this.settings = settings;

        this.objects = new Object[columns.length];
        this.columnIndexAdds = new int[columns.length];
        nameWithPosition = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            nameWithPosition.put(columns[i].name(), i + 1);
            columnIndexAdds[i] = i;
        }
    }

    public void appendRow() throws SQLException {
    	int i=0;
        try {
            for (i = 0; i < columns.length; i++) {
                columns[i].write(objects[i]);
            }
            rows ++;
        } catch (IOException e) {
            throw new SQLException("Exception processing value "+objects[i]+" for column : "+columns[i].name(),e);
        } catch (ClassCastException e) {
            throw new SQLException("Exception processing value "+objects[i]+" for column : "+columns[i].name(),e);
        }
    }

    public void setObject(int i, Object object) throws SQLException {
        objects[columnIndexAdds[i]] = object;
    }

    public void setConstObject(int i, Object object) throws SQLException {
        objects[i] = object;
    }

    public void incrIndex(int i) {
        for (int j = i; j < columnIndexAdds.length; j++) {
            columnIndexAdds[j] += 1;
        }
    }

    public void writeTo(BinarySerializer serializer) throws IOException, SQLException {
        settings.writeTo(serializer);

        serializer.writeVarInt(columns.length);
        serializer.writeVarInt(rows);

        for (Column column : columns) {
            column.serializeBinaryBulk(serializer);
        }
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns.length;
    }

    public Column getByPosition(int column) throws SQLException {
        Validate.isTrue(column < columns.length,
                        "Position " + column
                        + " is out of bound in Block.getByPosition, max position = " + (
                            columns.length - 1));
        return columns[column];
    }

    public int getPositionByName(String name) throws SQLException {
        Validate.isTrue(nameWithPosition.containsKey(name),"Column '" + name + "' does not exist");
        return nameWithPosition.get(name);
    }
    
    public Object getObject(int index) throws SQLException {
    	Validate.isTrue(index < columns.length,
                "Position " + index 
                + " is out of bound in Block.getByPosition, max position = " + (
                    columns.length - 1));
    	return objects[index];
    }

    public static Block readFrom(BinaryDeserializer deserializer,
                                 PhysicalInfo.ServerInfo serverInfo)
        throws IOException, SQLException {
        BlockSettings info = BlockSettings.readFrom(deserializer);

        int columns = (int) deserializer.readVarInt();
        int rows = (int) deserializer.readVarInt();

        Column[] cols = new Column[columns];

        for (int i = 0; i < columns; i++) {
            String name = deserializer.readStringBinary();
            String type = deserializer.readStringBinary();

            IDataType dataType = DataTypeFactory.get(type, serverInfo);
            Object[] arr = dataType.deserializeBinaryBulk(rows, deserializer);
            cols[i] = new Column(name, dataType, arr);
        }

        return new Block(rows, cols, info);
    }

    public void initWriteBuffer() {
        for (Column column : columns) {
            column.initWriteBuffer();
        }
    }
}
