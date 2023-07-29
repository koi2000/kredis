package com.koi.kredis.memory;

import com.koi.kredis.datatype.RedisBaseData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author koi
 * @date 2023/7/29 19:09
 */
public class RedisCache<T> {

    final static HashMap<Class<? extends RedisBaseData>, LinkedList<SimpleRingQueue<RedisBaseData>>> REDIS_CACHE = new HashMap<>();
    final static HashMap<Class<? extends RedisBaseData>, HashMap<Class<?>[], Constructor<? extends RedisBaseData>>> CONSTRUCTOR_CACHE = new HashMap<>();

    public <T extends RedisBaseData> T getRedisDataByType(Class<T> clazz, Object... params) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        LinkedList<SimpleRingQueue<RedisBaseData>> dataCacheLink = REDIS_CACHE.get(clazz);
        if (dataCacheLink == null || dataCacheLink.isEmpty()) {
            return getNewRedisData(clazz, params);
        }
        for (SimpleRingQueue<RedisBaseData> queue : dataCacheLink) {
            if (queue.isEmpty()) {
                continue;
            }
            return (T) queue.poll();
        }
        return getNewRedisData(clazz, params);
    }

    public <T extends RedisBaseData> void addRedisDataToCache(T t) {
        LinkedList<SimpleRingQueue<RedisBaseData>> dataCacheLink = REDIS_CACHE.computeIfAbsent(t.getClass(), v -> new LinkedList<>());
        if (dataCacheLink.isEmpty()) {
            dataCacheLink.add(new SimpleRingQueue<>(8888, 88888));
        }
        for (SimpleRingQueue<RedisBaseData> queue : dataCacheLink) {
            if (queue.isFull()) {
                continue;
            }
            queue.add(t);
        }
    }

    public <T extends RedisBaseData> T getNewRedisData(Class<T> clazz, Object... params) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class<?>[] paramsTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramsTypes[i] = params[i].getClass();
        }
        HashMap<Class<?>[], Constructor<? extends RedisBaseData>> clsLoaderMap = CONSTRUCTOR_CACHE.computeIfAbsent(clazz, k -> new HashMap<>());
        Constructor<T> redisDataConstructor = (Constructor<T>) clsLoaderMap.get(paramsTypes);
        if (redisDataConstructor == null) {
            redisDataConstructor = clazz.getConstructor(paramsTypes);
            clsLoaderMap.put(paramsTypes, redisDataConstructor);
        }
        return redisDataConstructor.newInstance(params);
    }
}
