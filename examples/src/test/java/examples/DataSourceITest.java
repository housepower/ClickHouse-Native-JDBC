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

package examples;

import com.github.housepower.jdbc.AbstractITest;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public abstract class DataSourceITest extends AbstractITest {

    protected void runSql(DataSource ds) throws Exception {
        withNewConnection(ds, connection -> {
            Statement statement = connection.createStatement();

            statement.executeQuery("DROP TABLE IF EXISTS test");
            statement.executeQuery("CREATE TABLE test(test_uInt16 UInt16, test_Int16 Int16)ENGINE=Log");
            statement.executeQuery("INSERT INTO test VALUES(" + Short.MAX_VALUE + "," + Short.MIN_VALUE + ")");
            ResultSet rs = statement.executeQuery("SELECT * FROM test ORDER BY test_uInt16");
            assertTrue(rs.next());
            assertEquals(Short.MAX_VALUE, rs.getShort(1));
            assertEquals(Short.MIN_VALUE, rs.getShort(2));
            assertFalse(rs.next());
            statement.executeQuery("DROP TABLE IF EXISTS test");
        });
    }
}
