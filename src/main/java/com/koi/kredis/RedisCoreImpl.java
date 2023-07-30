package com.koi.kredis;

import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author koi
 * @date 2023/7/30 11:46
 */
public class RedisCoreImpl implements RedisCore {

    private final ConcurrentSkipListMap<BytesWrapper, RedisData> map = new ConcurrentSkipListMap<>();

    private final ConcurrentHashMap<BytesWrapper, Channel> clients = new ConcurrentHashMap<>();
    private final Map<Channel, BytesWrapper> clientName = new ConcurrentHashMap<>();

    @Override
    public Set<BytesWrapper> keys() {
        return map.keySet();
    }

    @Override
    public void putClient(BytesWrapper connectionName, Channel channelContext) {
        clients.put(connectionName, channelContext);
        clientName.put(channelContext, connectionName);
    }

    @Override
    public boolean exist(BytesWrapper key) {
        return map.containsKey(key);
    }

    @Override
    public void put(BytesWrapper key, RedisData redisData) {
        map.put(key, redisData);
    }

    @Override
    public RedisData get(BytesWrapper key) {
        RedisData redisData = map.get(key);
        if (redisData==null){
            return null;
        }
        if (redisData.timeout()==-1){
            return redisData;
        }
        if (redisData.timeout()<System.currentTimeMillis()){
            map.remove(key);
            return null;
        }
        return redisData;
    }

    @Override
    public long remove(List<BytesWrapper> keys) {
        return keys.stream().peek(map::remove).count();
    }

    @Override
    public void cleanAll() {
        map.clear();
    }
}
