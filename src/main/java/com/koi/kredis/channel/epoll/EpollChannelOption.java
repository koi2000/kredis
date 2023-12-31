package com.koi.kredis.channel.epoll;

import com.koi.kredis.channel.LocalChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class EpollChannelOption implements LocalChannelOption {

    private final EpollEventLoopGroup boss;
    private final EpollEventLoopGroup selectors;

    public EpollChannelOption() {
        this.boss = new EpollEventLoopGroup(4, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "Server_boss_" + index.getAndIncrement());
            }
        });

        this.selectors = new EpollEventLoopGroup(8, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Server_selector_" + index.getAndIncrement());
            }
        });
    }

    public EpollChannelOption(EpollEventLoopGroup boss, EpollEventLoopGroup selectors) {
        this.boss = boss;
        this.selectors = selectors;
    }

    @Override
    public EventLoopGroup boss() {
        return this.boss;
    }

    @Override
    public EventLoopGroup selectors() {
        return this.selectors;
    }

    @Override
    public Class getChannelClass() {
        return EpollServerSocketChannel.class;
    }
}
