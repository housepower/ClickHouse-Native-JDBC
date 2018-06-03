package org.houseflys.jdbc.settings;

import org.houseflys.jdbc.serializer.BinarySerializer;

import java.io.IOException;

public interface ISettingType {

    Object deserializeURL(String queryParameter);

    void serializeSetting(BinarySerializer serializer, Object value) throws IOException;

    ISettingType UInt64 = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return Long.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt((Long) value);
        }
    };

    ISettingType Float = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return java.lang.Float.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeStringBinary(java.lang.String.valueOf(value));
        }
    };

    ISettingType String = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return queryParameter;
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeStringBinary(java.lang.String.valueOf(value));
        }
    };

    ISettingType Boolean = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return java.lang.Boolean.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt(java.lang.Boolean.TRUE.equals(value) ? 1 : 0);
        }
    };

    ISettingType Seconds = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return Long.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt((Long) value);
        }
    };

    ISettingType Character = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return queryParameter.charAt(0);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeStringBinary(java.lang.String.valueOf(value));
        }
    };

    ISettingType Milliseconds = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return Long.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt((Long) value);
        }
    };
}
