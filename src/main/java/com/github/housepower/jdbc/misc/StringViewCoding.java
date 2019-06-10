package com.github.housepower.jdbc.misc;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Arrays;

public class StringViewCoding {
    private static Field stringValuesField;
    private static Method encodeBytesMethod;

    // https://github.com/housepower/ClickHouse-Native-JDBC/issues/82
    // TODO Support jdk9,jdk10, so we temperoary use `new String`
    public static byte[] bytes(StringView data) throws SQLException {
        return new String(data.values()).substring(data.start(), data.end()).getBytes();
//        try {
//            Method encodeBytesMethod = getEncodeBytesMethod();
//            return (byte[]) encodeBytesMethod.invoke(null, data.values(), data.start(), data.end() - data.start());
//        } catch (Exception e) {
//            throw new SQLException("Cannot get java.lang.StringCoding encode Method", e);
//        }
    }

    public static char[] getValues(String data) throws SQLException {
        return data.toCharArray();
//        try {
//            Field field = getStringValueReflectField();
//            return (char[]) field.get(data);
//        } catch (IllegalAccessException e) {
//            throw new SQLException("Cannot get java.lang.String values field", e);
//        }
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


    private static byte[] toBytes(char[] chars, int start, int end) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                                          byteBuffer.position() + start, byteBuffer.position() + end);
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
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
