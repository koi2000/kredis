package com.koi.kredis.command;

import com.koi.kredis.command.impl.*;
import com.koi.kredis.command.impl.hash.Hdel;
import com.koi.kredis.command.impl.hash.Hscan;
import com.koi.kredis.command.impl.hash.Hset;
import com.koi.kredis.command.impl.list.Lpush;
import com.koi.kredis.command.impl.list.Lrange;
import com.koi.kredis.command.impl.list.Lrem;
import com.koi.kredis.command.impl.set.Sadd;
import com.koi.kredis.command.impl.set.Scan;
import com.koi.kredis.command.impl.set.Srem;
import com.koi.kredis.command.impl.set.Sscan;
import com.koi.kredis.command.impl.string.*;
import com.koi.kredis.command.impl.zset.Zadd;
import com.koi.kredis.command.impl.zset.Zrem;
import com.koi.kredis.command.impl.zset.Zrevrange;


import java.util.function.Supplier;

/**
 * @author koi
 * @date 2023/7/30 11:36
 */
public enum CommandType {
    auth(Auth::new), config(Config::new), scan(Scan::new),
    info(Info::new), client(Client::new), set(Set::new), type(Type::new),//
    ttl(Ttl::new), get(Get::new), quit(Quit::new),//
    setnx(SetNx::new), lpush(Lpush::new), lrange(Lrange::new), lrem(Lrem::new), rpush(Rpush::new), del(Del::new), sadd(Sadd::new),//
    sscan(Sscan::new), srem(Srem::new), hset(Hset::new), hscan(Hscan::new), hdel(Hdel::new),//
    zadd(Zadd::new), zrevrange(Zrevrange::new), zrem(Zrem::new), setex(SetEx::new), exists(Exists::new), expire(Expire::new),
    ping(Ping::new), select(Select::new), keys(Keys::new), incr(Incr::new), decr(Decr::new), mset(Mset::new), mget(Mget::new),
    ;

    private final Supplier<Command> supplier;

    CommandType(Supplier supplier) {
        this.supplier = supplier;
    }

    public Supplier<Command> getSupplier() {
        return supplier;
    }
}
