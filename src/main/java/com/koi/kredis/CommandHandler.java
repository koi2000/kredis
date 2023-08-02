package com.koi.kredis;

import com.koi.kredis.command.Command;
import com.koi.kredis.util.TRACEID;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

public class CommandHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = Logger.getLogger(CommandHandler.class);
    private final RedisCore redisCore;

    public CommandHandler(RedisCore redisCore){
        this.redisCore = redisCore;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        String traceId = TRACEID.currentTraceId();
        LOGGER.debug("tractId:"+traceId+" 本次处理的命令："+command.type().name());
        try{
            command.handle(ctx,redisCore);
        }catch (Exception e){
            LOGGER.error("处理数据时",e);
        }
        LOGGER.debug("traceId:"+traceId+" 命令处理完毕");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(" ExceptionCaught: ",cause);
    }
}
