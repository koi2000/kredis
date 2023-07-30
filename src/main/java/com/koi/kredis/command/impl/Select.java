package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 23:17
 */
public class Select implements Command {
    private Integer index;

    @Override
    public CommandType type() {
        return CommandType.select;
    }

    @Override
    public void setContent(Resp[] array) {
        index = Integer.parseInt(((BulkString) array[1]).getContent().toUtf8String());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        if (index > 0) {
            SimpleString ok = new SimpleString("-ERR invalid DB index");
            ctx.writeAndFlush(ok);
        } else {
            SimpleString ok = new SimpleString("OK");
            ctx.writeAndFlush(ok);
        }
    }
}
