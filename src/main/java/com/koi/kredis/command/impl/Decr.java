package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisString;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import com.koi.kredis.util.Format;
import io.netty.channel.ChannelHandlerContext;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author koi
 * @date 2023/7/30 17:40
 */
public class Decr implements WriteCommand {
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.decr;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            RedisString stringData = new RedisString();
            BytesWrapper bytesWrapper = new BytesWrapper("0".getBytes(UTF_8));
            stringData.setValue(bytesWrapper);
            redisCore.put(key, stringData);
            ctx.writeAndFlush(new BulkString(bytesWrapper));
        } else if (redisData instanceof RedisString) {
            try {
                BytesWrapper value = ((RedisString) redisData).getValue();
                long v = Format.parseLong(value.getByteArray(), 10);
                v--;
                BytesWrapper bytesWrapper = new BytesWrapper(Format.toByteArray(v));
                ((RedisString) redisData).setValue(bytesWrapper);
                ctx.writeAndFlush(new BulkString(bytesWrapper));
            } catch (NumberFormatException exception) {
                ctx.writeAndFlush(new SimpleString("value is not an integer or out of range"));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void handle(RedisCore redisCore) {
        RedisData redisData = redisCore.get(key);
        if (redisData == null) {
            RedisString stringData = new RedisString(new BytesWrapper("0".getBytes(UTF_8)));
            redisCore.put(key, stringData);
        } else if (redisData instanceof RedisString) {
            try {
                BytesWrapper value = ((RedisString) redisData).getValue();
                long v = Format.parseLong(value.getByteArray(), 10);
                --v;
                ((RedisString) redisData).setValue(new BytesWrapper(Format.toByteArray(v)));
            } catch (NumberFormatException exception) {
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
