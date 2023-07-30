package com.koi.kredis.command;

import com.koi.kredis.resp.BulkString;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;
import com.koi.kredis.resp.SimpleString;
import com.koi.kredis.util.TRACEID;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author koi
 * @date 2023/7/30 12:30
 */
public class CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(CommandFactory.class);

    static Map<String, Supplier<Command>> map = new HashMap<>();

    static {
        for (CommandType each : CommandType.values()) {
            map.put(each.name(), each.getSupplier());
        }
    }

    public static Command from(RespArray arrays) {
        Resp[] array = arrays.getArray();
        String commandName = ((BulkString) array[0]).getContent().toUtf8String();
        Supplier<Command> supplier = map.get(commandName);
        if (supplier == null) {
            LOGGER.debug("traceId:" + TRACEID.currentTraceId() + " 不支持的命令：" + commandName);
            System.out.println("不支持的命令：" + commandName);
            return null;
        } else {
            try {
                Command command = supplier.get();
                command.setContent(array);
                return command;
            } catch (Throwable e) {
                LOGGER.debug("traceId:" + TRACEID.currentTraceId() + " 不支持的命令：{},数据读取异常" + commandName);
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Command from(SimpleString string) {
        String commandName = string.getContent().toLowerCase();
        Supplier<Command> supplier = map.get(commandName);
        if (supplier == null) {
            LOGGER.debug("traceId:" + TRACEID.currentTraceId() + " 不支持的命令：" + commandName);
            System.out.println("不支持的命令：" + commandName);
            return null;
        } else {
            try {
                return supplier.get();
            } catch (Throwable e) {
                LOGGER.debug("traceId:" + TRACEID.currentTraceId() + " 不支持的命令：{},数据读取异常" + commandName);
                e.printStackTrace();
                return null;
            }
        }
    }

}
