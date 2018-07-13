package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.connect.PhysicalInfo;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serializer.BinaryDeserializer;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.data.BlockSettings.Setting;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Block {
    private final int rows;
    private final Column[] columns;
    private final BlockSettings settings;
    private final Map<String, Integer> nameWithPosition;


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

        nameWithPosition = new HashMap<String, Integer>();
        for (int i = 0; i < columns.length; i++) {
            nameWithPosition.put(columns[i].name(), i);
        }
    }

    public void writeTo(BinarySerializer serializer) throws IOException, SQLException {
        settings.writeTo(serializer);

        serializer.writeVarInt(columns.length);
        serializer.writeVarInt(rows);

        for (Column column : columns) {
            serializer.writeStringBinary(column.name());
            serializer.writeStringBinary(column.type().name());
            column.type().serializeBinaryBulk(column.data(), serializer);
        }
    }

    public long rows() {
        return rows;
    }

    public int columns() {
        return columns.length;
    }

    public Column getByPosition(int column) throws SQLException {
        Validate.isTrue(column < columns.length,
            "Position " + column + " is out of bound in Block.getByPosition, max position = " + (columns.length - 1));
        return columns[column];
    }

    public int getPositionByName(String name) throws SQLException {
        Validate.isTrue(nameWithPosition.containsKey(name));
        return nameWithPosition.get(name);
    }

    public static Block readFrom(BinaryDeserializer deserializer, PhysicalInfo.ServerInfo serverInfo)
        throws IOException, SQLException {
        BlockSettings info = BlockSettings.readFrom(deserializer);

        int columns = (int) deserializer.readVarInt();
        int rows = (int) deserializer.readVarInt();

        Column[] cols = new Column[columns];

        for (int i = 0; i < columns; i++) {
            String name = deserializer.readStringBinary();
            String type = deserializer.readStringBinary();

            IDataType dataType = DataTypeFactory.get(type, serverInfo);
            cols[i] = new Column(name, dataType, dataType.deserializeBinaryBulk(rows, deserializer));
        }

        return new Block(rows, cols, info);
    }
}
