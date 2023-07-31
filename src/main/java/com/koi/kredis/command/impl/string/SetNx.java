package com.koi.kredis.command.impl.string;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisString;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespInt;
import io.netty.channel.ChannelHandlerContext;

public class SetNx implements WriteCommand {

    private BytesWrapper key;
    private BytesWrapper value;


    @Override
    public CommandType type() {
        return CommandType.setex;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
        value = ((BulkString) array[2]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        boolean exist = redisCore.exist(key);
        if (exist){
            ctx.writeAndFlush(new RespInt(0));
        }else{
            RedisString redisString = new RedisString();
            redisString.setValue(value);
            redisCore.put(key,redisString);
            ctx.writeAndFlush(new RespInt(1));
        }
    }

    @Override
    public void handle(RedisCore redisCore) {

    }
}
