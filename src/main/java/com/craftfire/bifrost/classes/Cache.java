package com.craftfire.bifrost.classes;

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.enums.CacheGroup;

public class Cache {

    public static void put(CacheGroup group, Object object) {
        put(group, 1, object);
    }

    public static void put(CacheGroup group, Object id, Object object) {
        if (object == null) {
            return;
        }
        Bifrost.getInstance().getCacheManager().put(group.toString(), id, object);
    }

    public static Object get(CacheGroup group) {
        return get(group, 1);
    }

    public static Object get(CacheGroup group, Object id) {
        return Bifrost.getInstance().getCacheManager().get(group.toString(), id);
    }

    public static boolean contains(CacheGroup group) {
        return contains(group, 1);
    }

    public static boolean contains(CacheGroup group, Object id) {
         return Bifrost.getInstance().getCacheManager().contains(group.toString(), id);
    }
}
