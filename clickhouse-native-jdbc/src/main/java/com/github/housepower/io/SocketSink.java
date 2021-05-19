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

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.Charset;

public class SocketSink implements ISink, OkioHelper {

    private final BufferedSink buf;

    public SocketSink(Socket socket) throws IOException {
        this.buf = Okio.buffer(Okio.sink(socket));
    }

    @Override
    public void writeZero(int len) {
        try {
            for (int i = 0; i < len; i++) buf.writeByte(0);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeBoolean(boolean b) {
        try {
            buf.writeByte(b ? 1 : 0);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeByte(byte byt) {
        try {
            buf.writeByte(byt);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeShortLE(short s) {
        try {
            buf.writeShortLe(s);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeIntLE(int i) {
        try {
            buf.writeIntLe(i);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeLongLE(long l) {
        try {
            buf.writeLongLe(l);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeVarInt(long v) {
        writeVarInt(buf, v);
    }

    @Override
    public void writeFloatLE(float f) {
        try {
            buf.writeIntLe(Float.floatToRawIntBits(f));
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeDoubleLE(double d) {
        try {
            buf.writeLongLe(Double.doubleToRawLongBits(d));
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeBytes(byte[] bytes) {
        try {
            buf.write(bytes);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        try {
            buf.write(bytes.array().clone());
            ReferenceCountUtil.safeRelease(bytes);
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeCharSequence(CharSequence seq, Charset charset) {
        try {
            buf.write(ByteString.encodeString(seq.toString(), charset));
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void writeBinary(byte[] bytes) {
        writeBinary(buf, bytes);
    }

    @Override
    public void writeBinary(ByteBuf bytes) {
        writeBinary(buf, bytes.array().clone());
        ReferenceCountUtil.safeRelease(bytes);
    }

    @Override
    public void writeCharSequenceBinary(CharSequence seq, Charset charset) {
        writeCharSequenceBinary(buf, seq, charset);
    }

    @Override
    public void writeUTF8Binary(CharSequence utf8) {
        writeUTF8Binary(buf, utf8);
    }

    @Override
    public void flush(boolean force) {
        try {
            buf.flush();
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }

    @Override
    public void close() {
        try {
            buf.close();
        } catch (IOException rethrow) {
            throw new UncheckedIOException(rethrow);
        }
    }
}
