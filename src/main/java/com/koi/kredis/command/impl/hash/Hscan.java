package com.koi.kredis.command.impl.hash;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.impl.AbstractScan;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisHash;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;

import java.util.Map;
import java.util.stream.Stream;

public class Hscan extends AbstractScan {
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.hscan;
    }

    @Override
    public void setContent(Resp[] array) {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    protected RespArray get(RedisCore redisCore) {
        RedisHash redisHash = (RedisHash) redisCore.get(key);
        Map<BytesWrapper, BytesWrapper> map = redisHash.getMap();
        return new RespArray(map.entrySet().stream().flatMap(entry->{
            Resp[] resps = new Resp[2];
            resps[0] = new BulkString(entry.getKey());
            resps[1] = new BulkString(entry.getValue());
            return Stream.of(resps);
        }).toArray(Resp[]::new));
    }
}
