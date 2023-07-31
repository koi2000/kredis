package com.koi.kredis.command.impl.string;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisBaseData;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisString;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import io.netty.channel.ChannelHandlerContext;

public class Get implements Command {
    private BytesWrapper key;


    @Override
    public CommandType type() {
        return CommandType.get;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            ctx.writeAndFlush(BulkString.NullBulkString);
        } else if (redisData instanceof RedisString) {
            BytesWrapper value = ((RedisString) redisData).getValue();
            ctx.writeAndFlush(new BulkString(value));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
