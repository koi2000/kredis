package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractScan implements Command {

    protected abstract RespArray get(RedisCore redisCore);

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        Resp[] array = new Resp[2];
        BulkString blukStrings = new BulkString(new BytesWrapper("0".getBytes(CHARSET)));
        array[0] = blukStrings;
        array[1] = get(redisCore);
        ctx.writeAndFlush(new RespArray(array));
    }
}
