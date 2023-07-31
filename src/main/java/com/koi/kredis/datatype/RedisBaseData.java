package com.koi.kredis.datatype;

import com.koi.kredis.memory.RedisCache;

import java.lang.reflect.InvocationTargetException;

/**
 * @author koi
 * @date 2023/7/29 19:10
 */
public interface RedisBaseData {
    RedisCache REDIS_CACHE = new RedisCache();

    void clear();

    default void recovery() {
        clear();
        REDIS_CACHE.addRedisDataToCache(this);
    }
}
