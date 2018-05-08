//package org.houseflys.jdbc.connect;
//
//import org.houseflys.jdbc.protocol.*;
//import org.houseflys.jdbc.settings.ClickHouseConfig;
//import org.houseflys.jdbc.type.Block;
//import org.houseflys.jdbc.type.Column;
//import org.junit.Test;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.List;
//import java.util.Properties;
//import java.util.UUID;
//
//import static org.houseflys.jdbc.settings.ClickHouseDefines.*;
//
//public class PhysicalConnectionTest {
//
//    @Test
//    public void successfullyConnection() throws Exception {
//        ClickHouseConfig config = new ClickHouseConfig("localhost", 9000, "default", new Properties());
//        PhysicalConnection connection = new PhysicalConnection(config);
//
//        connection.connect();
//
//        RequestOrResponse response =
//            connection.sendRequest(new HelloRequest("client", 54380, "default", "default", ""));
//
//        HelloResponse hrs = (HelloResponse) response;
//        System.out.println(
//            "Connect Successfully. displayName[" + hrs.serverDisplayName() + "], version[" + hrs.reversion() + "] "
//                + "timeZone[" + hrs.serverTimeZone() + "]");
//
//        String queryString = "SELECT 1 AS s, 2 AS b";
//        QueryResponse queryResponse = (QueryResponse) connection.sendRequest(new QueryRequest(
//            UUID.randomUUID().toString(), getOrCreateClientInfo(), QueryRequest.COMPLETE_STAGE, true,
//            queryString));
//
//        Block header = queryResponse.header();
//        List<DataResponse> data = queryResponse.data();
//
//        System.out.println("Execute SQL : " + queryString);
//        for (long i = 0; i < header.columns(); i++) {
//            System.out.print(header.getByPosition((int) i).name() + "\t");
//        }
//
//        System.out.println("");
//        for (DataResponse datum : data) {
//            Block block = datum.block();
//
//            for (long row = 0; row < block.rows(); row++) {
//                for (long column = 0; column < block.columns(); column++) {
//                    System.out.print(block.getByPosition((int) column).data((int) row) + " \t");
//                }
//                System.out.println("");
//            }
//        }
//    }
//
//
//    private QueryRequest.ClientInfo getOrCreateClientInfo() throws UnknownHostException {
//        //TODO: client hostname
//        InetAddress address = InetAddress.getLocalHost();
//        String initialAddress = address.toString();
//
//        System.out.println("Initial Address :" + initialAddress);
//        return new QueryRequest.ClientInfo(QueryRequest.ClientInfo.INITIAL_QUERY, "", "",
//            address.getHostAddress() + ":0", "",
//            "localhost", DBMS_NAME + " " + "client", DBMS_VERSION_MAJOR.longValue(), DBMS_VERSION_MINOR.longValue(),
//            DBMS_CLIENT_REVERSION.longValue(), "");
//    }
//}
