package com.koi.kredis.command.impl;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author koi
 * @date 2023/7/30 21:26
 */
public class Keys implements Command {

    String pattern = "";


    @Override
    public CommandType type() {
        return CommandType.keys;
    }

    @Override
    public void setContent(Resp[] array) {
        //需要转译的字符(    [     {    /    ^    -    $     ¦    }    ]    )    ?    *    +    .
        pattern = "." + ((BulkString) array[1]).getContent().toUtf8String();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        Set<BytesWrapper> keySet = redisCore.keys();
        Resp[] resps = keySet.stream().filter(k -> {
            String content = null;
            try {
                content = k.toUtf8String();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return Pattern.matches(pattern, content);
        }).flatMap(key -> {
            Resp[] info = new Resp[1];
            info[0] = new BulkString(key);
            return Stream.of(info);
        }).toArray(Resp[]::new);
        ctx.writeAndFlush(new RespArray(resps));
    }
}
