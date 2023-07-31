package com.koi.kredis.command.impl.set;


import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisSet;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sadd implements WriteCommand {
    List<BytesWrapper> member;
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.sadd;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
        member = Stream.of(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            RedisSet redisSet = new RedisSet();
            int sadd = redisSet.sadd(member);
            redisCore.put(key, redisSet);
            ctx.writeAndFlush(new RespInt(sadd));
        } else if (redisData instanceof RedisSet) {
            RedisSet redisSet = (RedisSet) redisData;
            int sadd = redisSet.sadd(member);
            ctx.writeAndFlush(new RespInt(sadd));
        } else {
            throw new IllegalArgumentException("类型不匹配");
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            RedisSet redisSet = new RedisSet();
            redisSet.sadd(member);
            redisCore.put(key, redisSet);
        } else if (redisData instanceof RedisSet) {
            RedisSet redisSet = (RedisSet) redisData;
            redisSet.sadd(member);
        } else {
            throw new IllegalArgumentException("类型不匹配");
        }
    }
}
