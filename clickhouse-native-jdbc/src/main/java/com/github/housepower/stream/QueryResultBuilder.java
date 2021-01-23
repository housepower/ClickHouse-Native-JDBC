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

package com.github.housepower.stream;

import com.github.housepower.client.NativeContext;
import com.github.housepower.data.*;
import com.github.housepower.misc.CheckedIterator;
import com.github.housepower.misc.Validate;
import com.github.housepower.protocol.DataResponse;

import java.sql.SQLException;
import java.util.*;

/**
 * Support building QueryResult in client side, it's useful for ad-hoc building a ResultSet for JDBC interface,
 * and mock QueryResult for test.
 * <p>
 * Limitation, assume not use it to build from huge dataset, so all rows will be assembled into ONE block.
 */
public class QueryResultBuilder {

    private final int columnNum;
    private final NativeContext.ServerContext serverContext;
    private List<String> columnNames;
    private List<IDataType> columnTypes;
    private final List<List<?>> rows = new ArrayList<>();

    public static QueryResultBuilder builder(int columnsNum, NativeContext.ServerContext serverContext) {
        return new QueryResultBuilder(columnsNum, serverContext);
    }

    private QueryResultBuilder(int columnNum, NativeContext.ServerContext serverContext) {
        this.columnNum = columnNum;
        this.serverContext = serverContext;
    }

    public QueryResultBuilder columnNames(String... names) {
        return columnNames(Arrays.asList(names));
    }

    public QueryResultBuilder columnNames(List<String> names) {
        Validate.ensure(names.size() == columnNum, "size mismatch, req: " + columnNum + " got: " + names.size());
        this.columnNames = names;
        return this;
    }

    public QueryResultBuilder columnTypes(String... types) throws SQLException {
        return columnTypes(Arrays.asList(types));
    }

    public QueryResultBuilder columnTypes(List<String> types) throws SQLException {
        Validate.ensure(types.size() == columnNum, "size mismatch, req: " + columnNum + " got: " + types.size());
        this.columnTypes = new ArrayList<>(columnNum);
        for (int i = 0; i < columnNum; i++) {
            columnTypes.add(DataTypeFactory.get(types.get(i), serverContext));
        }
        return this;
    }

    public QueryResultBuilder addRow(Object... row) {
        return addRow(Arrays.asList(row));
    }

    public QueryResultBuilder addRow(List<?> row) {
        Validate.ensure(row.size() == columnNum, "size mismatch, req: " + columnNum + " got: " + row.size());
        rows.add(row);
        return this;
    }

    public QueryResult build() throws SQLException {
        Validate.ensure(columnNames != null, "columnNames is null");
        Validate.ensure(columnTypes != null, "columnTypes is null");

        // assemble header block
        IColumn[] headerColumns = new IColumn[columnNum];
        Object[] emptyObjects = new Object[columnNum];
        for (int c = 0; c < columnNum; c++) {
            headerColumns[c] = ColumnFactory.createColumn(columnNames.get(c), columnTypes.get(c), emptyObjects);
        }
        Block headerBlock = new Block(0, headerColumns);

        // assemble all rows to one data block
        IColumn[] dataColumns = new IColumn[columnNum];
        for (int c = 0; c < columnNum; c++) {
            Object[] columnObjects = new Object[rows.size()];
            for (int r = 0; r < rows.size(); r++) {
                columnObjects[r] = rows.get(r).get(c);
            }
            dataColumns[c] = ColumnFactory.createColumn(columnNames.get(c), columnTypes.get(c), columnObjects);
        }
        Block dataBlock = new Block(rows.size(), dataColumns);

        return new QueryResult() {

            @Override
            public Block header() throws SQLException {
                return headerBlock;
            }

            @Override
            public CheckedIterator<DataResponse, SQLException> data() {
                DataResponse data = new DataResponse("client_build", dataBlock);

                return new CheckedIterator<DataResponse, SQLException>() {

                    private final DataResponse dataResponse = data;
                    private boolean beforeFirst = true;

                    public boolean hasNext() {
                        return (beforeFirst);
                    }

                    public DataResponse next() {
                        if (!beforeFirst) {
                            throw new NoSuchElementException();
                        }
                        beforeFirst = false;
                        return dataResponse;
                    }
                };
            }
        };
    }
}
