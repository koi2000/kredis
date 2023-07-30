package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
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
 * @date 2023/7/30 21:04
 */
public class Expire implements WriteCommand {

    private BytesWrapper key;
    private int second;

    @Override
    public CommandType type() {
        return CommandType.expire;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
        second = Integer.parseInt(((BulkString) array[2]).getContent().toUtf8String());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            ctx.writeAndFlush(new RespInt(0));
        } else {
            redisData.setTimeout(System.currentTimeMillis() + (second * 1000));
            ctx.writeAndFlush(new RespInt(1));
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
        } else {
            redisData.setTimeout(System.currentTimeMillis() + (second * 1000));
        }
    }
}
