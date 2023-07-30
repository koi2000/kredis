package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 21:01
 */
public class Exists implements Command {
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.exists;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        boolean exist = redisCore.exist(key);
        if (exist) {
            ctx.writeAndFlush(new RespInt(1));
        } else {
            ctx.writeAndFlush(new RespInt(0));
        }
    }
}
