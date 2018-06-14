package com.github.housepower.jdbc.buffer;

import java.io.IOException;

public interface BuffedWriter {

    void writeBinary(byte byt) throws IOException;

    void writeBinary(byte[] bytes) throws IOException;

    void writeBinary(byte[] bytes, int offset, int length) throws IOException;

    void flushToTarget(boolean force) throws IOException;
}
