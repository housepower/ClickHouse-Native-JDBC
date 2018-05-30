package org.houseflys.jdbc.type.creator.complex;

import org.houseflys.jdbc.misc.KeywordScanner;
import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinaryDeserializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.type.ColumnCreator;
import org.houseflys.jdbc.type.ParseResult;
import org.houseflys.jdbc.type.column.complex.Enum16Column;
import org.houseflys.jdbc.type.column.complex.Enum8Column;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class EnumColumnCreator implements ColumnCreator {

    //Enum8('hello' = 1, 'world' = 2)
    private static final Pattern REGEX = Pattern.compile("(Enum\\d{1,2})\\((.*)\\)");

    private Map<Integer, String> nameMap;
    private int enumSize;

    public EnumColumnCreator(String type, Map<Integer, String> nameMap) {
        this.enumSize = Integer.parseInt(type.replace("Enum", ""));
        this.nameMap = nameMap;
    }

    @Override
    public Column createColumn(int rows, String name, String type, BinaryDeserializer deserializer)
        throws IOException, SQLException {
        switch (enumSize) {
            case 8:
                return createEnum8Column(rows, name, type, deserializer);
            case 16:
                return createEnum16Column(rows, name, type, deserializer);
        }
        return null;
    }

    private Column createEnum8Column(int rows, String name, String type,
                                     BinaryDeserializer deserializer) throws IOException {
        byte[] data = new byte[rows];
        String[] dataName = new String[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readByte();
            dataName[row] = nameMap.get(Integer.valueOf(data[row]));
        }
        return new Enum8Column(name, type, data, dataName);
    }

    private Column createEnum16Column(int rows, String name, String type,
                                      BinaryDeserializer deserializer) throws IOException {
        short[] data = new short[rows];
        String[] dataName = new String[rows];
        for (int row = 0; row < rows; row++) {
            data[row] = deserializer.readShort();
            dataName[row] = nameMap.get(Integer.valueOf(data[row]));
        }
        return new Enum16Column(name, type, data, dataName);
    }

    public static ParseResult parseEnumTypeName(String type, int pos) throws SQLException {
        Matcher matcher = REGEX.matcher(type);
        Validate
            .isTrue(matcher.find(pos) && matcher.start() == pos,
                    "Unknown data type family:" + type);

        Map<Integer, String> nameMap = new HashMap<Integer, String>();

        int index = 0;
        String str = matcher.group(2);
        int keyStart, keyEnd, numStart, numEnd;
        int number;
        while (index < str.length()) {
            keyStart = KeywordScanner.scanTo(index, '\'', str) + 1;
            keyEnd = KeywordScanner.scanTo(keyStart + 1, '\'', str);

            numStart = KeywordScanner.scanTo(keyEnd + 1, '=', str) + 1;
            numEnd = KeywordScanner.scanTo(numStart + 1, ',', str);
            if (numEnd == -1) {
                numEnd = str.length();
            }

            Validate.isTrue(keyEnd > keyStart && numEnd > numStart && numStart > keyEnd,
                            "Unknown data type family:" + type);

            number = Integer.parseInt(str.substring(numStart, numEnd).trim());
            nameMap.put(number, str.substring(keyStart, keyEnd));
            index = numEnd + 1;
        }
        return new ParseResult(matcher.end(), matcher.group(1),
                               new EnumColumnCreator(matcher.group(1), nameMap));
    }

}
