package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisHash;
import com.koi.kredis.datatype.RedisList;
import com.koi.kredis.datatype.RedisSet;
import com.koi.kredis.datatype.RedisString;
import com.koi.kredis.datatype.RedisZset;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 23:18
 */
public class Type implements Command {

    private BytesWrapper key;


    @Override
    public CommandType type() {
        return CommandType.type;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            ctx.writeAndFlush(new SimpleString("none"));
        } else if (redisData instanceof RedisString) {
            ctx.writeAndFlush(new SimpleString("string"));
        } else if (redisData instanceof RedisList) {
            ctx.writeAndFlush(new SimpleString("list"));
        } else if (redisData instanceof RedisSet) {
            ctx.writeAndFlush(new SimpleString("set"));
        } else if (redisData instanceof RedisHash) {
            ctx.writeAndFlush(new SimpleString("hash"));
        } else if (redisData instanceof RedisZset) {
            ctx.writeAndFlush(new SimpleString("zset"));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
