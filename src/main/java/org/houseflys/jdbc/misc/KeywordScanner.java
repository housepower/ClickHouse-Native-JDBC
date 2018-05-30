package org.houseflys.jdbc.misc;

/**
 */
public class KeywordScanner {

    public static int scanTo(int pos, char dest, String str) {
        for (int cursor = pos; cursor < str.length(); cursor++) {
            if (str.charAt(cursor) == dest) {
                return cursor;
            } else if (str.charAt(cursor) == '\\') {
                cursor++;
            } else if (str.charAt(cursor) == '\'') {
                cursor = scanTo(cursor + 1, '\'', str);
                if(cursor == -1) return cursor;
            } else if (str.charAt(cursor) == '(') {
                cursor = scanTo(cursor + 1, ')', str);
                if(cursor == -1) return cursor;
            }
        }
        return -1;
    }

    public static int skipSpace(int pos, String str) {
        while (pos < str.length() && isWhitespace(str.charAt(pos))) {
            pos++;
        }
        return pos;
    }

    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
    }
}
