/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.misc;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRUCache is a simple LRUCache implementation, based on <code>LinkedHashMap</code>.
 */
public class LRUCache<K, V> {
    private static final float HASH_TABLE_LOAD_FACTOR = 0.75f;

    private final int cacheSize;
    private final LinkedHashMap<K, V> map;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.map = new LinkedHashMap<K, V>(cacheSize, HASH_TABLE_LOAD_FACTOR, true) {
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > LRUCache.this.cacheSize;
            }
        };
    }

    public synchronized V get(K key) {
        return map.get(key);
    }

    public synchronized void put(K key, V value) {
        map.remove(key);
        map.put(key, value);
    }

    public synchronized void putIfAbsent(K key, V value) {
        map.putIfAbsent(key, value);
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized int cacheSize() {
        return map.size();
    }
}
