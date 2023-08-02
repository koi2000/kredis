package com.koi.kredis;

import com.koi.kredis.aof.Aof;
import com.koi.kredis.channel.DefaultChannelSelectStrategy;
import com.koi.kredis.channel.LocalChannelOption;
import com.koi.kredis.channel.single.SingleSelectChannelOption;
import com.koi.kredis.util.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * @author koi
 * @date 2023/7/30 12:21
 */
public class MyRedisServer implements RedisServer {

    private static final Logger LOGGER = Logger.getLogger(MyRedisServer.class);
    private final RedisCore redisCore = new RedisCoreImpl();
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final EventExecutorGroup redisSingleEventExecutor;
    private final LocalChannelOption channelOption;

    private Aof aof;

    public MyRedisServer(EventExecutorGroup redisSingleEventExecutor) {
        this.redisSingleEventExecutor = redisSingleEventExecutor;
        channelOption = new DefaultChannelSelectStrategy().select();
    }

    public MyRedisServer(LocalChannelOption channelOption) {
        this.channelOption = channelOption;
        this.redisSingleEventExecutor = new NioEventLoopGroup(1);
    }

    public static void main(String[] args) {
        new MyRedisServer(new SingleSelectChannelOption()).start();
    }

    @Override
    public void start() {
        if (PropertiesUtil.getAppendOnly()) {
            aof = new Aof(this.redisCore);
        }
        start0();
    }

    public void start0() {
        serverBootstrap.group(channelOption.boss(), channelOption.selectors())
                .channel(channelOption.getChannelClass())
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, PropertiesUtil.getTcpKeepAlive())
                .localAddress(new InetSocketAddress(PropertiesUtil.getNodeAddress(), PropertiesUtil.getNodePort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(
                                new ResponseEncoder(),
                                new CommandDecoder(aof)
                        );
                        channelPipeline.addLast(redisSingleEventExecutor, new CommandHandler(redisCore));
                    }
                });
        try {
            ChannelFuture sync = serverBootstrap.bind().sync();
            LOGGER.info(sync.channel().localAddress().toString());
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
            throw new RuntimeException();
        }
    }

    @Override
    public void close() {
        try {
            channelOption.boss().shutdownGracefully();
            channelOption.selectors().shutdownGracefully();
            redisSingleEventExecutor.shutdownGracefully();
        } catch (Exception ignored) {
            LOGGER.warn("Exception!", ignored);
        }
    }

    @Override
    public RedisCore getRedisCore() {
        return redisCore;
    }
}
