package com.github.housepower.jdbc;

import com.github.housepower.jdbc.misc.Validate;
import com.github.housepower.jdbc.wrapper.SQLStruct;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickHouseStruct extends SQLStruct {
    private static final Pattern REGEX = Pattern.compile("\\_(\\d+)");

    private final String type;
    private final Object[] attributes;

    public ClickHouseStruct(String type, Object[] attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return type;
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        return attributes;
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        int i = 0;
        Object[] res = new Object[map.size()];
        for (String attrName : map.keySet()) {
            Class<?> clazz = map.get(attrName);
            Matcher matcher = REGEX.matcher(attrName);
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
