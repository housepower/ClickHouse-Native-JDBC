package com.github.housepower.jdbc.misc;

import java.util.Arrays;

public class StringView {
    private final int start;
    private final int end;
    private final char[] values;

    public StringView(int start, int end, char[] values) {
        this.start = start;
        this.end = end;
        this.values = values;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public char[] values() {
        return values;
    }

    @Override
    public String toString() {
        return new String(Arrays.copyOfRange(values, start, end));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            String expectString = (String) obj;
            if (expectString.length() == end - start) {
                for (int i = 0; i < expectString.length(); i++) {
                    if (expectString.charAt(i) != values[start + i])
                        return false;
                }
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }
}
