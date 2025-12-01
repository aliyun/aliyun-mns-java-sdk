package com.aliyun.mns.common.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author yuanzhi
 * @date 2025/4/25.
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isAllEmpty(Collection<?>... collections) {
        if (collections == null) {
            return true;
        }
        for (Collection<?> collection : collections) {
            if (isNotEmpty(collection)) {
                return false;
            }
        }
        return true;
    }

    public static <T> HashSet<T> newHashSet(T... elements) {
        HashSet<T> set = new HashSet<>();
        if (elements != null) {
            for (T element : elements) {
                set.add(element);
            }
        }
        return set;
    }
}
