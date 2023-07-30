package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 22:49
 */
public class Quit implements Command {
    @Override
    public CommandType type() {
        return CommandType.quit;
    }

    @Override
    public void setContent(Resp[] array) {

    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        ctx.writeAndFlush(SimpleString.OK);
        ctx.close();
    }
}
