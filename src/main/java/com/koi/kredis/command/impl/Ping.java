package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 21:39
 */
public class Ping implements Command {

    @Override
    public CommandType type() {
        return CommandType.lrem;
    }

    @Override
    public void setContent(Resp[] array) {

    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        ctx.write(new SimpleString("PONG"));
        ctx.flush();
    }
}
