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

import com.github.housepower.misc.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

public class ByteBufSink implements ISink, ByteBufHelper {

    private final ByteBuf buf;

    public ByteBufSink() {
        this.buf = NettyUtil.alloc().buffer();
    }

    public ByteBuf retain() {
        return buf.retain();
    }

    @Override
    public void writeZero(int len) {
        buf.writeZero(len);
    }

    @Override
    public void writeBoolean(boolean b) {
        buf.writeBoolean(b);
    }

    @Override
    public void writeByte(byte byt) {
        buf.writeByte(byt);
    }

    @Override
    public void writeShortLE(short s) {
        buf.writeShortLE(s);
    }

    @Override
    public void writeIntLE(int i) {
        buf.writeIntLE(i);
    }

    @Override
    public void writeLongLE(long l) {
        buf.writeLongLE(l);
    }

    @Override
    public void writeVarInt(long v) {
        writeVarInt(buf, v);
    }

    @Override
    public void writeFloatLE(float f) {
        buf.writeFloatLE(f);
    }

    @Override
    public void writeDoubleLE(double d) {
        buf.writeDoubleLE(d);
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        buf.writeBytes(bytes);
        ReferenceCountUtil.safeRelease(bytes);
    }

    @Override
    public void writeCharSequence(CharSequence seq, Charset charset) {
        buf.writeCharSequence(seq, charset);
    }

    @Override
    public void writeBinary(ByteBuf bytes) {
        writeBinary(buf, bytes);
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
    }

    @Override
    public void close() {
        ReferenceCountUtil.safeRelease(buf);
    }
}
