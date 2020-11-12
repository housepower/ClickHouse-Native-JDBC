package com.github.housepower.jdbc.misc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionUtilTest {

    private static final Map<String, String> map1 = new HashMap<>();

    static {
        map1.put("k1", "v1");
        map1.put("k2", "v2");
    }

    private static final Map<String, String> map2 = new HashMap<>();

    static {
        map2.put("k2", "new_v2");
        map2.put("k3", "v3");
    }

    private static final Map<String, String> keepFirstMap = new HashMap<>();

    static {
        keepFirstMap.put("k1", "v1");
        keepFirstMap.put("k2", "v2");
        keepFirstMap.put("k3", "v3");
    }

    private static final Map<String, String> keepLastMap = new HashMap<>();

    static {
        keepLastMap.put("k1", "v1");
        keepLastMap.put("k2", "new_v2");
        keepLastMap.put("k3", "v3");
    }

    @Test
    public void testMergeMap() {
        assertEquals(keepFirstMap, CollectionUtil.mergeMapKeepFirst(map1, map2));
        assertEquals(keepLastMap, CollectionUtil.mergeMapKeepLast(map1, map2));
    }

    @Test
    public void testMergeMapInPlace() {
        HashMap<String, String> map1Copy1 = new HashMap<>(map1);
        CollectionUtil.mergeMapInPlaceKeepFirst(map1Copy1, map2);
        assertEquals(keepFirstMap, map1Copy1);
        HashMap<String, String> map1Copy2 = new HashMap<>(map1);
        CollectionUtil.mergeMapInPlaceKeepLast(map1Copy2, map2);
        assertEquals(keepLastMap, map1Copy2);
    }
}