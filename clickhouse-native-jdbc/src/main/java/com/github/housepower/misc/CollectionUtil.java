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

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {

    public static <T> List<T> concat(List<T> first, List<T> second) {
        return Stream.concat(first.stream(), second.stream()).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<T> concat(List<T> originList, T... elements) {
        return Stream.concat(originList.stream(), Stream.of(elements)).collect(Collectors.toList());
    }

    public static <T> List<T> repeat(int time, List<T> origin) {
        assert time > 0;
        List<T> result = origin;
        for (int i = 0; i < time - 1; i++) {
            result = concat(result, origin);
        }
        return result;
    }

    public static List<String> filterIgnoreCase(List<String> set, String keyword) {
        return set.stream()
                .filter(key -> key.equalsIgnoreCase(keyword))
                .collect(Collectors.toList());
    }

    public static Map<String, String> filterKeyIgnoreCase(Properties properties, String keyword) {
        Map<String, String> props = new HashMap<>();
        properties.forEach((k, v) -> props.put(k.toString(), v.toString()));
        return filterKeyIgnoreCase(props, keyword);
    }

    public static <V> Map<String, V> filterKeyIgnoreCase(Map<String, V> map, String keyword) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(keyword))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> mergeMapKeepFirst(Map<K, V> one, Map<K, V> other) {
        return Stream.concat(one.entrySet().stream(), other.entrySet().stream()).collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (former, latter) -> former));
    }

    public static <K, V> Map<K, V> mergeMapKeepLast(Map<K, V> one, Map<K, V> other) {
        return Stream.concat(one.entrySet().stream(), other.entrySet().stream()).collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (former, latter) -> latter));
    }

    public static <K, V> void mergeMapInPlaceKeepFirst(Map<K, V> one, @Nullable Map<K, V> other) {
        if (other != null)
            other.forEach(one::putIfAbsent);
    }

    public static <K, V> void mergeMapInPlaceKeepLast(Map<K, V> one, @Nullable Map<K, V> other) {
        if (other != null)
            other.forEach(one::put);
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && collection.size() > 0;
    }
}
