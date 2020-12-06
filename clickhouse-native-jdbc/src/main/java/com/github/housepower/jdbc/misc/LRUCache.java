package com.github.housepower.jdbc.misc;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class LRUCache<Key, Value> {
    private static final float   hashTableLoadFactor = 0.75f;

    private int                  cacheSize;
    private LinkedHashMap<Key, Value> map;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.map = new LinkedHashMap<Key, Value>(cacheSize, hashTableLoadFactor, true) {
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > LRUCache.this.cacheSize;
            }
        };
    }

    public synchronized Value get(Key key) {
        return map.get(key);
    }

    public synchronized void put(Key key, Value value) {
        map.put(key, value);
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized int size() {
        return map.size();
    }
}
