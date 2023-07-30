package com.koi.kredis.datatype;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author koi
 * @date 2023/7/29 21:39
 */
public class RedisSet implements RedisData{
    private long timeout = -1;

    private Set<BytesWrapper> set = new HashSet<>();

    @Override
    public long timeout()
    {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public int sadd(List<BytesWrapper> members)
    {
        return (int) members.stream().filter(set::add).count();
    }

    public Collection<BytesWrapper> keys()
    {
        return set;
    }

    public int srem(List<BytesWrapper> members)
    {
        return (int) members.stream().peek(RedisBaseData::recovery).filter(set::remove).count();
    }

    @Override
    public void clear() {
        set = new HashSet<>();
        timeout = -1;
    }
}
