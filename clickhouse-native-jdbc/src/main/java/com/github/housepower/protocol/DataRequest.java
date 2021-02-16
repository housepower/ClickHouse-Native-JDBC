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

package com.github.housepower.protocol;

import com.github.housepower.data.Block;
import com.github.housepower.serde.BinarySerializer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.sql.SQLException;

public class DataRequest implements Request {

    public static final DataRequest EMPTY = new DataRequest("") {
        @Override
        public void encode(ByteBuf buf) {
            buf.writeByte(0x02);
            buf.writeByte(0x00);
            buf.writeByte(0xa7);
            buf.writeByte(0x83);
            buf.writeByte(0xac);
            buf.writeByte(0x6c);
            buf.writeByte(0xd5);
            buf.writeByte(0x5c);
            buf.writeByte(0x7a);
            buf.writeByte(0x7c);
            buf.writeByte(0xb5);
            buf.writeByte(0xac);
            buf.writeByte(0x46);
            buf.writeByte(0xbd);
            buf.writeByte(0xdb);
            buf.writeByte(0x86);
            buf.writeByte(0xe2);
            buf.writeByte(0x14);
            buf.writeByte(0x82);
            buf.writeByte(0x14);
            buf.writeByte(0x00);
            buf.writeByte(0x00);
            buf.writeByte(0x00);
            buf.writeByte(0x0a);
            buf.writeByte(0x00);
            buf.writeByte(0x00);
            buf.writeByte(0x00);
            buf.writeByte(0xa0);
            buf.writeByte(0x01);
            buf.writeByte(0x00);
            buf.writeByte(0x02);
            buf.writeByte(0xff);
            buf.writeByte(0xff);
            buf.writeByte(0xff);
            buf.writeByte(0xff);
            buf.writeByte(0x00);
            buf.writeByte(0x00);
            buf.writeByte(0x00);
        }
    };

    private final String name;
    private final Block block;

    public DataRequest(String name) {
        this(name, new Block());
    }

    public DataRequest(String name, Block block) {
        this.name = name;
        this.block = block;
    }

    @Override
    public ProtoType type() {
        return ProtoType.REQUEST_DATA;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException, SQLException {
        serializer.writeUTF8StringBinary(name);

        serializer.maybeEnableCompressed();
        block.writeTo(serializer);
        serializer.maybeDisableCompressed();
    }

    @Override
    public void encode0(ByteBuf buf) {
        writeUTF8Binary(buf, name);
        // TODO compress
        block.encode(buf);
    }
}
