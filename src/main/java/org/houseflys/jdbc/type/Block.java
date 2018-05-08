package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;

public class Block {
    private final int rows;
    private final Column[] columns;
    private final BlockInfo blockInfo;


    public Block() {
        this(0, new Column[0]);
    }

    public Block(int rows, Column[] columns) {
        this(rows, columns, new BlockInfo(BlockInfo.Field.values()));
    }

    public Block(int rows, Column[] columns, BlockInfo blockInfo) {
        this.rows = rows;
        this.columns = columns;
        this.blockInfo = blockInfo;
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        blockInfo.writeTo(serializer);

        serializer.writeVarInt(columns.length);
        serializer.writeVarInt(rows);

        for (Column column : columns) {
            column.writeTo(serializer);
        }
    }

    public static Block readFrom(BinaryDeserializer deserializer) throws IOException {
        BlockInfo info = BlockInfo.readFrom(deserializer);

        int columns = (int) deserializer.readVarInt();
        int rows = (int) deserializer.readVarInt();

        Column[] cols = new Column[columns];

        for (int i = 0; i < columns; i++) {
            cols[i] = ColumnFactory.createColumn(rows, deserializer);
        }

        return new Block(rows, cols, info);
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
}
