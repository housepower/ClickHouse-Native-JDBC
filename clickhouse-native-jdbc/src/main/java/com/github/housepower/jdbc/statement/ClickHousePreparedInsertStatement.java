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

package com.github.housepower.jdbc.statement;

import com.github.housepower.jdbc.ClickHouseConnection;
import com.github.housepower.jdbc.connect.NativeContext;
import com.github.housepower.jdbc.data.Block;
import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.stream.ValuesWithParametersInputFormat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ClickHousePreparedInsertStatement extends AbstractPreparedStatement {

    private final int posOfData;
    private final String fullQuery;
    private final String insertQuery;
    private boolean blockInit;

    public ClickHousePreparedInsertStatement(int posOfData,
                                             String fullQuery,
                                             ClickHouseConnection conn,
                                             NativeContext nativeContext) throws SQLException {
        super(conn, nativeContext, null);
        this.blockInit = false;
        this.posOfData = posOfData;
        this.fullQuery = fullQuery;
        this.insertQuery = fullQuery.substring(0, posOfData);

        initBlockIfPossible();
    }

    private void initBlockIfPossible() throws SQLException {
        if (this.blockInit) {
            return;
        }
        this.block = getSampleBlock(insertQuery);
        this.block.initWriteBuffer();
        this.blockInit = true;
        new ValuesWithParametersInputFormat(fullQuery, posOfData).fillBlock(block);
    }

    @Override
    public boolean execute() throws SQLException {
        return executeQuery() != null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        addParameters();
        int result = connection.sendInsertRequest(block);
        this.blockInit = false;
        this.block.initWriteBuffer();
        return result;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        executeUpdate();
        return null;
    }

    @Override
    public void addBatch() throws SQLException {
        addParameters();
    }

    @Override
    public void setObject(int index, Object x) throws SQLException {
        initBlockIfPossible();
        block.setObject(index - 1, x);
    }

    private void addParameters() throws SQLException {
        block.appendRow();
    }

    @Override
    public void clearBatch() throws SQLException {
    }

    @Override
    public int[] executeBatch() throws SQLException {
        int rows = connection.sendInsertRequest(block);
        int[] result = new int[rows];
        Arrays.fill(result, 1);
        clearBatch();
        this.blockInit = false;
        this.block.initWriteBuffer();
        return result;
    }

    @Override
    public void close() throws SQLException {
        if (blockInit) {
            // Empty insert when close.
            this.connection.sendInsertRequest(new Block());
            this.blockInit = false;
            this.block.initWriteBuffer();
        }
        super.close();
    }

    private static int computeQuestionMarkSize(String query, int start) throws SQLException {
        int param = 0;
        boolean inQuotes = false, inBackQuotes = false;
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if (ch == '`') {
                inBackQuotes = !inBackQuotes;
            } else if (ch == '\'') {
                inQuotes = !inQuotes;
            } else if (!inBackQuotes && !inQuotes) {
                if (ch == '?') {
                    Validate.isTrue(i > start, "");
                    param++;
                }
            }
        }
        return param;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(": ");
        try {
            sb.append(insertQuery).append(" (");
            for (int i = 0; i < block.columns(); i++) {
                Object obj = block.getObject(i);
                if (obj == null) {
                    sb.append("?");
                } else if (obj instanceof Number) {
                    sb.append(obj);
                } else {
                    sb.append("'").append(obj).append("'");
                }
                if (i < block.columns() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
