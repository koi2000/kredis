package com.koi.kredis;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;

/**
 * @author koi
 * @date 2023/7/30 12:21
 */
public class MyRedisServer implements RedisServer{

    private static final Logger LOGGER = Logger.getLogger(MyRedisServer.class);
    private final RedisCore redisCore = new RedisCoreImpl();
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final EventExecutorGroup redisSingleEventExecutor;
    private final LocalChannelOption channelOption;

    public MyRedisServer(EventExecutorGroup redisSingleEventExecutor) {
        this.redisSingleEventExecutor = redisSingleEventExecutor;
    }


    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public RedisCore getRedisCore() {
        return null;
    }
}
