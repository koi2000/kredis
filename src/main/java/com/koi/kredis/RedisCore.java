package com.koi.kredis;

import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * @author koi
 * @date 2023/7/30 11:40
 */
public interface RedisCore {

    Set<BytesWrapper> keys();

    void putClient(BytesWrapper connectionName, Channel channelContext);

    boolean exist(BytesWrapper key);

    void put(BytesWrapper key, RedisData redisData);

    RedisData get(BytesWrapper key);

    long remove(List<BytesWrapper> keys);

    void cleanAll();
}
