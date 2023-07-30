package com.koi.kredis.datatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author koi
 * @date 2023/7/29 21:44
 */
public class RedisHash implements RedisData {
    private long timeout = -1;

    private Map<BytesWrapper, BytesWrapper> map = new HashMap<>();

    public long getTimeout() {
        return timeout;
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int put(BytesWrapper field, BytesWrapper value) {
        return map.put(field, value) == null ? 1 : 0;
    }

    public Map<BytesWrapper, BytesWrapper> getMap() {
        return map;
    }

    public int del(List<BytesWrapper> fields) {
        return (int) fields.stream().peek(BytesWrapper::recovery).filter(key -> map.remove(key) != null).count();
    }

    @Override
    public void clear() {
        map  = new HashMap<>();
        timeout = -1;
    }
}
