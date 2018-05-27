package org.houseflys.jdbc.type.column.complex;

import org.houseflys.jdbc.misc.Validate;
import org.houseflys.jdbc.serializer.BinarySerializer;
import org.houseflys.jdbc.type.Column;
import org.houseflys.jdbc.wrapper.SQLStruct;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TupleColumn extends Column {

    private final Struct[] data;

    public TupleColumn(String name, String type, Struct[] data) {
        super(name, type);
        this.data = data;
    }

    @Override
    public Object[] data() {
        return data;
    }

    @Override
    public Object data(int rows) {
        return data[rows];
    }

    @Override
    public int typeWithSQL() throws SQLException {
        return Types.STRUCT;
    }

    @Override
    public void writeImpl(BinarySerializer serializer) throws IOException {

    }

    public static class ClickHouseStruct extends SQLStruct {
        private final Object[] attributes;

        public ClickHouseStruct(Object[] attributes) {
            this.attributes = attributes;
        }

        @Override
        public Object[] getAttributes() throws SQLException {
            return attributes;
        }

        @Override
        public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
            Pattern regex = Pattern.compile("\\_(\\d+)");
            int i = 0;
            Object[] res = new Object[map.size()];
            for (String attrName : map.keySet()) {
                Class<?> clazz = map.get(attrName);
                Matcher matcher = regex.matcher(attrName);
                Validate.isTrue(matcher.matches(), "Can't find " + attrName + ".");

                int attrIndex = Integer.parseInt(matcher.group(1)) - 1;
                Validate.isTrue(attrIndex < attributes.length, "Can't find " + attrName + ".");
                Validate.isTrue(clazz.isInstance(attributes[attrIndex]),
                    "Can't cast " + attrName + " to " + clazz.getName());

                res[i++] = clazz.cast(attributes[attrIndex]);
            }
            return res;
        }
    }
}
