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

package com.github.housepower.data;

import com.github.housepower.client.NativeContext;
import com.github.housepower.data.BlockSettings.Setting;
import com.github.housepower.io.ByteBufHelper;
import com.github.housepower.misc.NettyUtil;
import com.github.housepower.misc.Validate;
import com.github.housepower.protocol.Encodable;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Block implements ByteBufHelper, Encodable {

    private static final ByteBufHelper helper = ByteBufHelper.DEFAULT;

    public static Block readFrom(ByteBuf buf, NativeContext.ServerContext serverContext) {
        BlockSettings info = BlockSettings.readFrom(buf);

        int columnCnt = (int) helper.readVarInt(buf);
        int rowCnt = (int) helper.readVarInt(buf);

        IColumn[] columns = new IColumn[columnCnt];

        for (int i = 0; i < columnCnt; i++) {
            String name = helper.readUTF8Binary(buf);
            String type = helper.readUTF8Binary(buf);

            IDataType dataType = DataTypeFactory.get(type, serverContext);
            Object[] arr = dataType.decodeBulk(buf, rowCnt);
            columns[i] = ColumnFactory.createColumn(name, dataType, arr);
        }

        return new Block(rowCnt, columns, info);
    }

    private final IColumn[] columns;
    private final BlockSettings settings;
    // position start with 1
    private final Map<String, Integer> nameAndPositions;
    private final Object[] rowData;
    private final int[] placeholderIndexes;
    private int rowCnt;

    public Block() {
        this(0, new IColumn[0]);
    }

    public Block(int rowCnt, IColumn[] columns) {
        this(rowCnt, columns, new BlockSettings(Setting.defaultValues()));
    }

    public Block(int rowCnt, IColumn[] columns, BlockSettings settings) {
        this.rowCnt = rowCnt;
        this.columns = columns;
        this.settings = settings;

        this.rowData = new Object[columns.length];
        this.nameAndPositions = new HashMap<>();
        this.placeholderIndexes = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            nameAndPositions.put(columns[i].name(), i + 1);
            placeholderIndexes[i] = i;
        }
    }

    public int rowCnt() {
        return rowCnt;
    }

    public int columnCnt() {
        return columns.length;
    }

    public void appendRow() throws SQLException {
        int i = 0;
        try {
            for (; i < columns.length; i++) {
                columns[i].write(rowData[i]);
            }
            rowCnt++;
        } catch (IOException | ClassCastException e) {
            throw new SQLException("Exception processing value " + rowData[i] + " for column: " + columns[i].name(), e);
        }
    }

    public void setObject(int columnIdx, Object object) {
        rowData[columnIdx] = object;
    }

    public int paramIdx2ColumnIdx(int paramIdx) {
        return placeholderIndexes[paramIdx];
    }

    public void incPlaceholderIndexes(int columnIdx) {
        for (int i = columnIdx; i < placeholderIndexes.length; i++) {
            placeholderIndexes[i] += 1;
        }
    }

    @Override
    public void encode(ByteBuf buf) {
        settings.encode(buf);

        writeVarInt(buf, columns.length);
        writeVarInt(buf, rowCnt);

        for (IColumn column : columns) {
            column.flush(buf, true);
        }
    }

    // idx start with 0
    public IColumn getColumn(int columnIdx) throws SQLException {
        Validate.isTrue(columnIdx < columns.length,
                "Position " + columnIdx +
                        " is out of bound in Block.getByPosition, max position = " + (columns.length - 1));
        return columns[columnIdx];
    }

    // position start with 1
    public int getPositionByName(String columnName) throws SQLException {
        Validate.isTrue(nameAndPositions.containsKey(columnName), "Column '" + columnName + "' does not exist");
        return nameAndPositions.get(columnName);
    }

    public Object getObject(int columnIdx) throws SQLException {
        Validate.isTrue(columnIdx < columns.length,
                "Position " + columnIdx +
                        " is out of bound in Block.getByPosition, max position = " + (columns.length - 1));
        return rowData[columnIdx];
    }

    public void initWriteBuffer() {
        for (IColumn column : columns) {
            column.setBuf(NettyUtil.alloc().buffer());
        }
    }
}
