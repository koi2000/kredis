package com.koi.kredis.command.impl.string;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisString;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

public class SetEx implements WriteCommand {
    private BytesWrapper key;
    private int seconds;
    private BytesWrapper value;


    @Override
    public CommandType type() {
        return CommandType.setex;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
        seconds = Integer.parseInt(((BulkString) array[2]).getContent().toUtf8String());
        value = ((BulkString) array[3]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisString redisString = new RedisString();
        redisString.setValue(value);
        redisString.setTimeout(System.currentTimeMillis() + (seconds * 1000L));
        redisCore.put(key, redisString);
        ctx.writeAndFlush(SimpleString.OK);
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisString redisString = new RedisString();
        redisString.setValue(value);
        redisString.setTimeout(System.currentTimeMillis() + (seconds * 1000L));
        redisCore.put(key, redisString);
    }
}
