package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.buffer.ByteArrayWriter;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.ClickHouseDefines;

import java.io.IOException;

/**
 *
 */
public class ColumnWriterBuffer {

    public BinarySerializer offset;
    public BinarySerializer column;

    private ByteArrayWriter offsetWriter;
    private ByteArrayWriter columnWriter;


    public ColumnWriterBuffer() {
        this.offsetWriter = new ByteArrayWriter(ClickHouseDefines.COLUMN_BUFFER);
        this.columnWriter = new ByteArrayWriter(ClickHouseDefines.COLUMN_BUFFER);

        this.offset = new BinarySerializer(offsetWriter, false);
        this.column = new BinarySerializer(columnWriter, false);
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        offsetWriter.getBuffer().flip();
        columnWriter.getBuffer().flip();

        while (offsetWriter.getBuffer().hasRemaining()) {
            serializer.writeByte(offsetWriter.getBuffer().get());
        }

        while (columnWriter.getBuffer().hasRemaining()) {
            serializer.writeByte(columnWriter.getBuffer().get());
        }
    }
}