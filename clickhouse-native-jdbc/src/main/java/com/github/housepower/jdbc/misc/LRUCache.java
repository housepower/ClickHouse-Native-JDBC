package com.github.housepower.jdbc.misc;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRUCache is a simple LRUCache implemention, based on <code>LinkedHashMap</code>.
 */
public class LRUCache<K, V> {
    private static final float   hashTableLoadFactor = 0.75f;

    private int                  cacheSize;
    private LinkedHashMap<K, V> map;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.map = new LinkedHashMap<K, V>(cacheSize, hashTableLoadFactor, true) {
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > LRUCache.this.cacheSize;
            }
        };
    }

    public synchronized V get(K key) {
        return map.get(key);
    }

    public synchronized void put(K key, V value) {
        map.put(key, value);
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized int size() {
        return map.size();
    }
}
