/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc.settings;

import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.time.Duration;

public interface ISettingType {

    Object deserializeURL(String queryParameter);

    void serializeSetting(BinarySerializer serializer, Object value) throws IOException;

    ISettingType Int64 = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return Long.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt((Long) value);
        }
    };

    ISettingType Int32 = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return Integer.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt((Integer) value);
        }
    };

    ISettingType Float = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return java.lang.Float.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeUTF8StringBinary(java.lang.String.valueOf(value));
        }
    };

    ISettingType String = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return queryParameter;
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeUTF8StringBinary(java.lang.String.valueOf(value));
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
            return Duration.ofSeconds(Long.parseLong(queryParameter));
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt(((Duration) value).getSeconds());
        }
    };

    ISettingType Milliseconds = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return Duration.ofMillis(Long.parseLong(queryParameter));
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeVarInt(((Duration) value).toMillis());
        }
    };

    ISettingType Character = new ISettingType() {
        @Override
        public Object deserializeURL(String queryParameter) {
            return queryParameter.charAt(0);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Object value) throws IOException {
            serializer.writeUTF8StringBinary(java.lang.String.valueOf(value));
        }
    };
}
