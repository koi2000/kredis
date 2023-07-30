package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 23:18
 */
public class Ttl implements WriteCommand {
    private BytesWrapper key;


    @Override
    public CommandType type() {
        return CommandType.ttl;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            ctx.writeAndFlush(new RespInt(-2));
        } else if (redisData.timeout() == -1) {
            ctx.writeAndFlush(new RespInt(-1));
        } else {
            long second = (redisData.timeout() - System.currentTimeMillis()) / 1000;
            ctx.writeAndFlush(new RespInt((int) second));
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
        } else if (redisData.timeout() == -1) {
        } else {
            long second = (redisData.timeout() - System.currentTimeMillis()) / 1000;
        }
    }
}
