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

package com.github.housepower.jdbc.data;

import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.BlockSettings.Setting;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.serde.BinaryDeserializer;
import com.github.housepower.jdbc.serde.BinarySerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Block {

    public static Block readFrom(BinaryDeserializer deserializer,
                                 NativeContext.ServerContext serverContext) throws IOException, SQLException {
        BlockSettings info = BlockSettings.readFrom(deserializer);

        int columns = (int) deserializer.readVarInt();
        int rows = (int) deserializer.readVarInt();

        IColumn[] cols = new IColumn[columns];

        for (int i = 0; i < columns; i++) {
            String name = deserializer.readUTF8StringBinary();
            String type = deserializer.readUTF8StringBinary();

            IDataType dataType = DataTypeFactory.get(type, serverContext);
            Object[] arr = dataType.deserializeBinaryBulk(rows, deserializer);
            cols[i] = ColumnFactory.createColumn(name, dataType, arr);
        }

        return new Block(rows, cols, info);
    }

    private final IColumn[] columns;
    private final BlockSettings settings;
    private final Map<String, Integer> nameWithPosition;

    private final Object[] objects;
    private final int[] columnIndexAdds;
    private int rows;

    public Block() {
        this(0, new IColumn[0]);
    }

    public Block(int rows, IColumn[] columns) {
        this(rows, columns, new BlockSettings(Setting.values()));
    }

    public Block(int rows, IColumn[] columns, BlockSettings settings) {
        this.rows = rows;
        this.columns = columns;
        this.settings = settings;

        this.objects = new Object[columns.length];
        this.columnIndexAdds = new int[columns.length];
        nameWithPosition = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            nameWithPosition.put(columns[i].name(), i + 1);
            columnIndexAdds[i] = i;
        }
    }

    public void appendRow() throws SQLException {
        int i = 0;
        try {
            for (i = 0; i < columns.length; i++) {
                columns[i].write(objects[i]);
            }
            rows++;
        } catch (IOException | ClassCastException e) {
            throw new SQLException("Exception processing value " + objects[i] + " for column: " + columns[i].name(), e);
        }
    }

    public void setObject(int i, Object object) {
        objects[columnIndexAdds[i]] = object;
    }

    public void setConstObject(int i, Object object) {
        objects[i] = object;
    }

    public void incIndex(int i) {
        for (int j = i; j < columnIndexAdds.length; j++) {
            columnIndexAdds[j] += 1;
        }
    }

    public void writeTo(BinarySerializer serializer) throws IOException, SQLException {
        settings.writeTo(serializer);

        serializer.writeVarInt(columns.length);
        serializer.writeVarInt(rows);

        for (IColumn column : columns) {
            column.flushToSerializer(serializer, true);
        }
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns.length;
    }

    public IColumn getByPosition(int column) throws SQLException {
        Validate.isTrue(column < columns.length,
                "Position " + column +
                        " is out of bound in Block.getByPosition, max position = " + (columns.length - 1));
        return columns[column];
    }

    public int getPositionByName(String name) throws SQLException {
        Validate.isTrue(nameWithPosition.containsKey(name), "Column '" + name + "' does not exist");
        return nameWithPosition.get(name);
    }

    public Object getObject(int index) throws SQLException {
        Validate.isTrue(index < columns.length,
                "Position " + index +
                        " is out of bound in Block.getByPosition, max position = " + (columns.length - 1));
        return objects[index];
    }

    public void initWriteBuffer() {
        for (IColumn column : columns) {
            column.setColumnWriterBuffer(new ColumnWriterBuffer());
        }
    }
}
