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

package com.github.housepower.jdbc.benchmark;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import ru.yandex.clickhouse.ClickHouseStatement;
import ru.yandex.clickhouse.domain.ClickHouseFormat;

import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class RowBinaryStringIBenchmark extends AbstractInsertIBenchmark {
    private final String columnType = "String";

    @Benchmark
    @Test
    public void benchInsertNative() throws Exception {
        withConnection(connection -> {
            wideColumnPrepare(connection, columnType);
            String params = Strings.repeat("?, ", columnNum);
            PreparedStatement
                    pstmt = connection.prepareStatement("INSERT INTO " + getTableName() + " values(" + params.substring(0, params.length() - 2) + ")");
            for (int i = 0; i < batchSize; i++) {
                for (int j = 0; j < columnNum; j++) {
                    pstmt.setObject(j + 1, j + 1 + "");
                }
                pstmt.addBatch();
            }
            int[] res = pstmt.executeBatch();
            assertEquals(res.length, batchSize);
            wideColumnAfter(connection);
        }, ConnectionType.NATIVE);
    }

    @Benchmark
    @Test
    public void benchInsertHttpRowBinary() throws Exception {
        withConnection(connection -> {
            wideColumnPrepare(connection, columnType);
            ClickHouseStatement sth = (ClickHouseStatement) connection.createStatement();
            sth.write().send("INSERT INTO " + getTableName(), stream -> {
                for (int i = 0; i < batchSize; i++) {
                    for (int j = 0; j < columnNum; j++) {
                        stream.writeString(j + 1 + "");
                    }
                }
            }, ClickHouseFormat.RowBinary);

            wideColumnAfter(connection);
        }, ConnectionType.HTTP);
    }

}
