package com.koi.kredis.channel;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

public interface LocalChannelOption<C extends Channel> {
    // 返回获取tcp线程
    EventLoopGroup boss();

    // 返回处理tcp线程
    EventLoopGroup selectors();

    // 返回管道类型
    Class<? extends C> getChannelClass();
}
