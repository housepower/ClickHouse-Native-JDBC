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

package com.github.housepower.io;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Utf8;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class OkioUtil {

    public static void writeZero(BufferedSink buf, int len) {
        try {
            buf.write(new byte[len]);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static void writeBoolean(BufferedSink buf, boolean b) {
        try {
            buf.writeByte(b ? 1 : 0);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static long readVarInt(BufferedSource buf) {
        try {
            long number = 0;
            for (int i = 0; i < 9; i++) {
                int byt = buf.readByte();
                number |= (long) (byt & 0x7F) << (7 * i);
                if ((byt & 0x80) == 0) break;
            }
            return number;
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static void writeVarInt(BufferedSink buf, long value) {
        try {
            for (int i = 0; i < 9; i++) {
                byte byt = (byte) (value & 0x7F);
                if (value > 0x7F) byt |= 0x80;
                value >>= 7;
                buf.writeByte(byt);
                if (value == 0) return;
            }
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static String readUTF8Binary(BufferedSource buf) {
        long len = readVarInt(buf);
        try {
            return buf.readUtf8(len);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static void writeUTF8Binary(BufferedSink buf, CharSequence seq) {
        writeVarInt(buf, Utf8.size(seq.toString()));
        try {
            buf.write(ByteString.encodeString(seq.toString(), StandardCharsets.UTF_8));
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static void writeCharSequenceBinary(BufferedSink buf, CharSequence seq, Charset charset) {
        writeBinary(buf, ByteString.encodeString(seq.toString(), charset));
    }

    public static void writeBinary(BufferedSink buf, byte[] data) {
        writeVarInt(buf, data.length);
        try {
            buf.write(data);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    public static void writeBinary(BufferedSink buf, ByteString data) {
        writeVarInt(buf, data.size());
        try {
            buf.write(data);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }
}
