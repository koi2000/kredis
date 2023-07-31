package com.koi.kredis.command.impl.list;

import com.koi.kredis.command.CommandType;
import com.koi.kredis.command.impl.Push;
import com.koi.kredis.datatype.RedisList;
public class Lpush extends Push {
    public Lpush(){
        super(RedisList::lpush);
    }

    @Override
    public CommandType type() {
        return CommandType.lpush;
    }
}
