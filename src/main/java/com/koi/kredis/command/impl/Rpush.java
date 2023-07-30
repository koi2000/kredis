package com.koi.kredis.command.impl;

import com.koi.kredis.command.CommandType;
import com.koi.kredis.datatype.BytesWrapper;
import com.koi.kredis.datatype.RedisList;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author koi
 * @date 2023/7/30 23:16
 */
public class Rpush extends Push {

    public Rpush() {
        super(RedisList::rpush);
    }

    @Override
    public CommandType type() {
        return CommandType.rpush;
    }
}
