package org.houseflys.jdbc.data.type;

import org.houseflys.jdbc.data.IDataType;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.stream.QuotedLexer;
import org.houseflys.jdbc.stream.QuotedToken;
import org.houseflys.jdbc.stream.QuotedTokenType;

import java.io.IOException;
import java.sql.SQLException;

public class DataTypeInt8 implements IDataType {

    private static final Byte DEFAULT_VALUE = 0;

    @Override
    public Object defaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public void serializeBinary(Object data, BinarySerializer serializer) throws SQLException, IOException {
        Validate.isTrue(data instanceof Byte,
            "Can't serializer " + data.getClass().getSimpleName() + " With ByteDataTypeSerializer.");
        serializer.writeByte((Byte) data);
    }

    @Override
    public Byte deserializeBinary(BinaryDeserializer deserializer) throws IOException {
        return deserializer.readByte();
    }

    @Override
    public void serializeBinaryBulk(Object[] data, BinarySerializer serializer) throws SQLException, IOException {
        for (Object datum : data) {
            serializeBinary(datum, serializer);
        }
    }

    @Override
    public Byte[] deserializeBinaryBulk(int rows, BinaryDeserializer deserializer) throws IOException {
        Byte[] data = new Byte[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readByte();
        }
        return data;
    }

    @Override
    public Object deserializeTextQuoted(QuotedLexer lexer) throws SQLException {
        QuotedToken token = lexer.next();
        Validate.isTrue(token.type() == QuotedTokenType.Number, "");
        return Byte.valueOf(token.data());
    }
}
