package com.koi.kredis.command.impl.string;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisString;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mget implements Command {
    private List<BytesWrapper> keys;


    @Override
    public CommandType type() {
        return CommandType.mget;
    }

    @Override
    public void setContent(Resp[] array) {
        keys = Stream.of(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisCore redisCore) {
        LinkedList<BytesWrapper> linkedList = new LinkedList<>();
        keys.forEach(key->{
            RedisData redisData = redisCore.get(key);
            if (redisData==null){
            }else if (redisData instanceof RedisString){
                linkedList.add(((RedisString)redisData).getValue());
            }else{
                throw new UnsupportedOperationException();
            }
            RespArray respArray = new RespArray(linkedList.stream().map(BulkString::new).toArray(Resp[]::new));
            ctx.writeAndFlush(respArray);
        });
    }
}
