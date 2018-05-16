package org.houseflys.jdbc.tool;

public class Uniquely {

    public static String uniquelyWithPrefix(String prefix) {
        return uniquelyWithPrefixAndSuffix(prefix, null);
    }

    public static String uniquelyWithSuffix(String suffix) {
        return uniquelyWithPrefixAndSuffix(null, suffix);
    }

    public static String uniquelyWithPrefixAndSuffix(String prefix, String suffix) {
        String uniquelyString = "";
        return (prefix == null ? "" : prefix) +
            uniquelyString + (suffix == null ? "" : suffix);
    }

    public static long uniquely() {
        return 0;
    }
}
