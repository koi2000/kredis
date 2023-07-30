package com.koi.kredis.datatype;

/**
 * @author koi
 * @date 2023/7/29 20:52
 */
public interface RedisData extends RedisBaseData{
    long timeout();

    void setTimeout(long timeout);
}
