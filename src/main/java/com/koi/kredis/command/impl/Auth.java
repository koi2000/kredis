package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.SimpleString;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author koi
 * @date 2023/7/30 12:58
 */
public class Auth implements Command {
    private String password;


    @Override
    public CommandType type() {
        return CommandType.auth;
    }

    @Override
    public void setContent(Resp[] array) {
        BulkString bulkStrings = (BulkString) array[1];
        byte[] content = bulkStrings.getContent().getByteArray();
        if (content.length==0){
            password = "";
        }else{
            password = new String(content);
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        SimpleString ok = new SimpleString("OK");
        ctx.writeAndFlush(ok);
    }
}
