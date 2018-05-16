package org.houseflys.jdbc.protocol;

import org.houseflys.jdbc.settings.ClickHouseDefines;
import org.houseflys.jdbc.tool.HistoryBinarySerializer;
import org.houseflys.jdbc.tool.Uniquely;
import org.junit.Test;

public class HelloRequestTest {

    private HistoryBinarySerializer serializer;

    @Test
    public void successfullySerializerHelloRequest() throws Exception {
        serializer = new HistoryBinarySerializer();
        long reversion = Uniquely.uniquely();
        String userName = Uniquely.uniquelyWithPrefix("USER_NAME");
        String password = Uniquely.uniquelyWithPrefix("PASSWORD");
        String clientName = Uniquely.uniquelyWithPrefix("CLIENT_NAME");
        String defaultDatabase = Uniquely.uniquelyWithPrefix("DEFAULT_DATABASE");
        HelloRequest request = new HelloRequest(clientName, reversion, defaultDatabase, userName, password);

        request.writeImpl(serializer);

        serializer.assertAll(
            0L, ClickHouseDefines.DBMS_NAME + " " + clientName, ClickHouseDefines.DBMS_VERSION_MAJOR.longValue(),
            ClickHouseDefines.DBMS_VERSION_MINOR.longValue(), reversion, defaultDatabase, userName, password);
    }
}