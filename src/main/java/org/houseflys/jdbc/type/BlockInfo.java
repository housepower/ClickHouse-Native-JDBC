package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;

public class BlockInfo {
    private final Field[] fields;

    public BlockInfo(Field[] fields) {
        this.fields = fields;
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        for (Field field : fields) {
            serializer.writeVarInt(field.num);

            if (Boolean.class.isAssignableFrom(field.clazz)) {
                serializer.writeBoolean((Boolean) field.defaultValue);
            } else if (Integer.class.isAssignableFrom(field.clazz)) {
                serializer.writeInt((Integer) field.defaultValue);
            }
        }
        serializer.writeVarInt(0);
    }

    public static BlockInfo readFrom(BinaryDeserializer deserializer) throws IOException {
        return new BlockInfo(readFieldsFrom(1, deserializer));
    }

    private static Field[] readFieldsFrom(int currentSize, BinaryDeserializer deserializer) throws IOException {
        long num = deserializer.readVarInt();

        for (Field field : Field.values()) {
            if (field.num == num) {
                if (Boolean.class.isAssignableFrom(field.clazz)) {
                    Field receiveField = new Field(field.num, deserializer.readBoolean());
                    Field[] fields = readFieldsFrom(currentSize + 1, deserializer);
                    fields[currentSize - 1] = receiveField;
                    return fields;
                } else if (Integer.class.isAssignableFrom(field.clazz)) {
                    Field receiveField = new Field(field.num, deserializer.readInt());
                    Field[] fields = readFieldsFrom(currentSize + 1, deserializer);
                    fields[currentSize - 1] = receiveField;
                    return fields;
                }
            }
        }
        return new Field[currentSize - 1];
    }

    public static class Field {
        public static final Field BUCKET_NUM = new Field(2, -1);
        public static final Field IS_OVERFLOWS = new Field(1, false);

        private final int num;
        private final Class clazz;
        private final Object defaultValue;

        public Field(int num, Object defaultValue) {
            this.num = num;
            this.defaultValue = defaultValue;
            this.clazz = defaultValue.getClass();
        }

        public static Field[] values() {
            return new Field[] {IS_OVERFLOWS, BUCKET_NUM};
        }
    }
}
