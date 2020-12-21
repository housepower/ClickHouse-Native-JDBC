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

package com.github.housepower.jdbc.serde;

import java.io.IOException;
import java.time.Duration;

public interface SettingSerde<T> {

    T deserializeURL(String queryParameter);

    void serializeSetting(BinarySerializer serializer, T value) throws IOException;

    SettingSerde<Long> Int64 = new SettingSerde<Long>() {
        @Override
        public Long deserializeURL(String queryParameter) {
            return Long.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Long value) throws IOException {
            serializer.writeVarInt(value);
        }
    };

    SettingSerde<Integer> Int32 = new SettingSerde<Integer>() {
        @Override
        public Integer deserializeURL(String queryParameter) {
            return Integer.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Integer value) throws IOException {
            serializer.writeVarInt(value);
        }
    };

    SettingSerde<Float> Float32 = new SettingSerde<Float>() {
        @Override
        public Float deserializeURL(String queryParameter) {
            return Float.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Float value) throws IOException {
            serializer.writeUTF8StringBinary(String.valueOf(value));
        }
    };

    SettingSerde<String> UTF8 = new SettingSerde<String>() {
        @Override
        public String deserializeURL(String queryParameter) {
            return queryParameter;
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, String value) throws IOException {
            serializer.writeUTF8StringBinary(String.valueOf(value));
        }
    };

    SettingSerde<Boolean> Bool = new SettingSerde<Boolean>() {
        @Override
        public Boolean deserializeURL(String queryParameter) {
            return Boolean.valueOf(queryParameter);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Boolean value) throws IOException {
            serializer.writeVarInt(Boolean.TRUE.equals(value) ? 1 : 0);
        }
    };

    SettingSerde<Duration> Seconds = new SettingSerde<Duration>() {
        @Override
        public Duration deserializeURL(String queryParameter) {
            return Duration.ofSeconds(Long.parseLong(queryParameter));
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Duration value) throws IOException {
            serializer.writeVarInt(value.getSeconds());
        }
    };

    SettingSerde<Duration> Milliseconds = new SettingSerde<Duration>() {
        @Override
        public Duration deserializeURL(String queryParameter) {
            return Duration.ofMillis(Long.parseLong(queryParameter));
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Duration value) throws IOException {
            serializer.writeVarInt(value.toMillis());
        }
    };

    SettingSerde<Character> Char = new SettingSerde<Character>() {
        @Override
        public Character deserializeURL(String queryParameter) {
            return queryParameter.charAt(0);
        }

        @Override
        public void serializeSetting(BinarySerializer serializer, Character value) throws IOException {
            serializer.writeUTF8StringBinary(String.valueOf(value));
        }
    };
}
