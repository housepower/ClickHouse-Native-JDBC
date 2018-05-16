package org.houseflys.jdbc.type;

import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.creator.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColumnFactory {

    private static final Map<String, ColumnCreator> creators = initialCreators();

    public static Column createColumn(int rows, BinaryDeserializer deserializer) throws IOException {
        String name = deserializer.readStringBinary();
        String type = deserializer.readStringBinary();

        System.out.println("Column Type : " + type);
        return creators.get(type).create(rows, deserializer, name, type);
    }

    private static Map<String, ColumnCreator> initialCreators() {
        Map<String, ColumnCreator> creators = new HashMap<String, ColumnCreator>();

        creators.put("Int8", new Int8ColumnCreator());
        creators.put("Int16", new Int16ColumnCreator());
        creators.put("Int32", new Int32ColumnCreator());
        creators.put("Int64", new Int64ColumnCreator());

        creators.put("UInt8", new Int8ColumnCreator());
        creators.put("UInt16", new Int16ColumnCreator());
        creators.put("UInt32", new Int32ColumnCreator());
        creators.put("UInt64", new Int64ColumnCreator());

        creators.put("String", new StringColumnCreator());
        return creators;
    }

}
