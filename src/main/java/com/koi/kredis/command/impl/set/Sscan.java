package com.koi.kredis.command.impl.set;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.command.impl.AbstractScan;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisData;
import com.koi.kredis.datatype.RedisSet;
import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;
import com.koi.kredis.resp.RespInt;

import java.util.List;
import java.util.stream.Collectors;

public class Sscan extends AbstractScan
{
    private BytesWrapper key;

    @Override
    public CommandType type()
    {
        return CommandType.sscan;
    }

    @Override
    public void setContent(Resp[] array)
    {
        key = ((BulkString) array[1]).getContent();
    }

    @Override
    protected RespArray get(RedisCore redisCore)
    {
        RedisSet         redisSet = (RedisSet) redisCore.get(key);
        List<BulkString> collect  = redisSet.keys().stream().map(keyName -> new BulkString(keyName)).collect(Collectors.toList());
        return new RespArray(collect.toArray(new Resp[collect.size()]));
    }
}
