package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.BlockSettings.Setting;

import java.io.IOException;
import java.sql.SQLException;

public class Block {
    private final int rows;
    private final Column[] columns;
    private final BlockSettings settings;


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
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        settings.writeTo(serializer);

        serializer.writeVarInt(columns.length);
        serializer.writeVarInt(rows);

        for (Column column : columns) {
            column.writeTo(serializer);
        }
    }

    public long rows() {
        return rows;
    }

    public long columns() {
        return columns.length;
    }

    public Column getByPosition(int column) {
        return columns[column];
    }

    public static Block readFrom(BinaryDeserializer deserializer) throws IOException, SQLException {
        BlockSettings info = BlockSettings.readFrom(deserializer);

        int columns = (int) deserializer.readVarInt();
        int rows = (int) deserializer.readVarInt();

        Column[] cols = new Column[columns];

        for (int i = 0; i < columns; i++) {
            cols[i] = ColumnFactory.createColumn(rows, deserializer);
        }

        return new Block(rows, cols, info);
    }
}
