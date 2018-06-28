package com.github.housepower.jdbc.misc;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class StringViewCoding {
    private static Field stringValuesField;
    private static Method encodeBytesMethod;

    public static byte[] bytes(StringView data) throws SQLException {
        try {
            Method encodeBytesMethod = getEncodeBytesMethod();
            return (byte[]) encodeBytesMethod.invoke(null, data.values(), data.start(), data.end() - data.start());
        } catch (Exception e) {
            throw new SQLException("Cannot get java.lang.StringCoding encode Method", e);
        }
    }

    public static char[] getValues(String data) throws SQLException {
        try {
            Field field = getStringValueReflectField();
            return (char[]) field.get(data);
        } catch (IllegalAccessException e) {
            throw new SQLException("Cannot get java.lang.String values field", e);
        }
    }

    private static Field getStringValueReflectField() throws SQLException {
        if (stringValuesField == null) {
            synchronized (StringViewCoding.class) {
                if (stringValuesField == null) {
                    try {
                        Field stringValueField = String.class.getDeclaredField("value");
                        stringValueField.setAccessible(true);
                        StringViewCoding.stringValuesField = stringValueField;
                    } catch (NoSuchFieldException e) {
                        throw new SQLException("Cannot get java.lang.String values field", e);
                    }
                }
            }
        }
        return stringValuesField;
    }

    public static Method getEncodeBytesMethod() throws SQLException {
        if (encodeBytesMethod == null) {
            synchronized (StringViewCoding.class) {
                if (encodeBytesMethod == null) {
                    try {
                        Class<?> codingClazz = Class.forName("java.lang.StringCoding");
                        Method encodeBytesMethod =
                            codingClazz.getDeclaredMethod("encode", char[].class, int.class, int.class);
                        encodeBytesMethod.setAccessible(true);
                        StringViewCoding.encodeBytesMethod = encodeBytesMethod;
                    } catch (Exception e) {
                        throw new SQLException("Cannot get java.lang.String values field", e);
                    }
                }
            }
        }
        return encodeBytesMethod;
    }
}
