package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.buffer.ByteArrayWriter;
import com.github.housepower.jdbc.serializer.BinarySerializer;
import com.github.housepower.jdbc.settings.ClickHouseDefines;

import java.io.IOException;

/**
 *
 */
public class ColumnWriterBuffer {

    public BinarySerializer column;
    private ByteArrayWriter columnWriter;


    public ColumnWriterBuffer() {
        this.columnWriter = new ByteArrayWriter(ClickHouseDefines.COLUMN_BUFFER);
        this.column = new BinarySerializer(columnWriter, false);
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        columnWriter.getBuffer().flip();

        while (columnWriter.getBuffer().hasRemaining()) {
            serializer.writeByte(columnWriter.getBuffer().get());
        }
    }
}