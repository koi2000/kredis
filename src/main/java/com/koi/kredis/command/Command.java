package com.koi.kredis.command;

import com.koi.kredis.RedisCore;
import com.koi.kredis.resp.Resp;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author koi
 * @date 2023/7/30 11:37
 */
public interface Command {
    Charset CHARSET = StandardCharsets.UTF_8;
    static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Command.class);

    // 获取接口类型
    CommandType type();

    // 注入属性
    void setContent(Resp[] array);

    void handle(ChannelHandlerContext ctx, RedisCore redisCore);
}
