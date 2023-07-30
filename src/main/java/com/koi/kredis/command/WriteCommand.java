package com.koi.kredis.command;

import com.koi.kredis.RedisCore;

/**
 * @author koi
 * @date 2023/7/30 11:36
 */
public interface WriteCommand extends Command{
    void handle(RedisCore redisCore);
}
