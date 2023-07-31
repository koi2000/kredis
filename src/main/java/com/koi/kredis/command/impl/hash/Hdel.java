package com.koi.kredis.command.impl.hash;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisHash;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hdel implements WriteCommand {

    private BytesWrapper key;
    private List<BytesWrapper> fields;


    @Override
    public CommandType type() {
        return CommandType.hdel;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
        fields = Stream.of(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisHash redisHash = (RedisHash)redisCore.get(key);
        int del = redisHash.del(fields);
        ctx.writeAndFlush(new RespInt(del));
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisHash redisHash = (RedisHash) redisCore.get(key);
        redisHash.del(fields);
    }
}
