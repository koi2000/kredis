package com.koi.kredis;

/**
 * @author koi
 * @date 2023/7/30 11:43
 */
public interface RedisServer {

    void start();

    void close();

    RedisCore getRedisCore();
}
