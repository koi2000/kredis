package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisList;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Errors;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author koi
 * @date 2023/7/30 22:23
 */
public abstract class Push implements WriteCommand {

    BiConsumer<RedisList, List<BytesWrapper>> biConsumer;
    private BytesWrapper key;
    private List<BytesWrapper> value;

    public Push(BiConsumer<RedisList, List<BytesWrapper>> biConsumer) {
        this.biConsumer = biConsumer;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
        value = new ArrayList<>();
        for (int i = 2; i < array.length; i++) {
            value.add(((BulkString) array[i]).getContent());
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            RedisList redisList = new RedisList();
            biConsumer.accept(redisList, value);
            redisCore.put(key, redisList);
            ctx.writeAndFlush(new RespInt(redisList.size()));
        } else if (!(redisData instanceof RedisList)) {
            ctx.writeAndFlush(new Errors("wrong type"));
        } else {
            biConsumer.accept((RedisList) redisData, value);
            redisCore.put(key, redisData);
            ctx.writeAndFlush(new RespInt(((RedisList) redisData).size()));
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            RedisList redisList = new RedisList();
            biConsumer.accept(redisList, value);
            redisCore.put(key, redisList);

        } else if (redisData != null && !(redisData instanceof RedisList)) {
        } else {
            biConsumer.accept((RedisList) redisData, value);
            redisCore.put(key, redisData);
        }
    }
}
