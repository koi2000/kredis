package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import com.koi.kredis.util.TRACEID;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 13:17
 */
public class Client implements Command {
    private String subCommand;
    private Resp[] array;


    @Override
    public CommandType type() {
        return CommandType.client;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
        subCommand = ((BulkString)array[1]).getContent().toUtf8String();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        String traceId = TRACEID.currentTraceId();
        LOGGER.debug("traceId:{} 当前的子命令是：{}"+traceId+subCommand);
        switch (subCommand)
        {
            case "setname":
                BytesWrapper connectionName = ((BulkString) array[2]).getContent();
                redisCore.putClient(connectionName, ctx.channel());
                break;
            default:
                throw new IllegalArgumentException();
        }
        ctx.writeAndFlush(new SimpleString("OK"));
    }
}
