package com.koi.kredis.channel;

import com.koi.kredis.channel.epoll.EpollChannelOption;
import com.koi.kredis.channel.kqueue.KqueueChannelOption;
import com.koi.kredis.channel.select.NioSelectChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;

public class DefaultChannelSelectStrategy implements ChannelSelectStrategy {

    @Override
    public LocalChannelOption select() {
        if (KQueue.isAvailable()) {
            return new KqueueChannelOption();
        }
        if (Epoll.isAvailable()) {
            return new EpollChannelOption();
        }
        return new NioSelectChannelOption();
    }
}
