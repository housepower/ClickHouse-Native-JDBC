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
import okio.Buffer;
import okio.ByteString;

import java.nio.charset.Charset;

public class BufferSink implements ISink, OkioHelper {

    private final Buffer buf;

    public BufferSink() {
        this.buf = new Buffer();
    }

    public Buffer internal() {
        return this.buf;
    }

    @Override
    public void writeZero(int len) {
        OkioUtil.writeZero(buf, len);
    }

    @Override
    public void writeBoolean(boolean b) {
        buf.writeByte(b ? 1 : 0);
    }

    @Override
    public void writeByte(byte byt) {
        buf.writeByte(byt);
    }

    @Override
    public void writeShortLE(short s) {
        buf.writeShortLe(s);
    }

    @Override
    public void writeIntLE(int i) {
        buf.writeIntLe(i);
    }

    @Override
    public void writeLongLE(long l) {
        buf.writeLongLe(l);
    }

    @Override
    public void writeVarInt(long v) {
        writeVarInt(buf, v);
    }

    @Override
    public void writeFloatLE(float f) {
        buf.writeIntLe(Float.floatToRawIntBits(f));
    }

    @Override
    public void writeDoubleLE(double d) {
        buf.writeLongLe(Double.doubleToRawLongBits(d));
    }

    @Override
    public void writeBytes(byte[] bytes) {
        buf.write(bytes);
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        buf.write(bytes.array());
        ReferenceCountUtil.safeRelease(bytes);
    }

    @Override
    public void writeCharSequence(CharSequence seq, Charset charset) {
        buf.write(ByteString.encodeString(seq.toString(), charset));
    }

    @Override
    public void writeBinary(byte[] bytes) {
        writeBinary(buf, bytes);
    }

    @Override
    public void writeBinary(ByteBuf bytes) {
        writeBinary(buf, bytes.array());
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
        buf.flush();
    }

    @Override
    public void close() {
        buf.close();
    }
}
