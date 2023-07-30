package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author koi
 * @date 2023/7/30 21:00
 */
public class Del implements WriteCommand {

    private List<BytesWrapper> keys;

    @Override
    public CommandType type() {
        return CommandType.del;
    }

    @Override
    public void setContent(Resp[] array) {
        keys = Stream.of(array).skip(1)
                .map(resp -> ((BulkString) resp).getContent())
                .collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        long remove = redisCore.remove(keys);
        ctx.writeAndFlush(new RespInt((int) remove));
    }

    @Override
    public void handle(RedisCore redisCore) {
        redisCore.remove(keys);
    }
}
