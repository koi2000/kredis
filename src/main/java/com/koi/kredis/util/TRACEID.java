package com.koi.kredis.util;

/**
 * @author koi
 * @date 2023/7/30 16:03
 */
public class TRACEID {
    private static final ThreadLocal<String> TRACEID = new ThreadLocal<>();
    private static final Uid uid = WinterId.instance();

    public static String newTraceId() {
        String traceId = uid.generateDigits();
        TRACEID.set(traceId);
        return traceId;
    }

    public static String currentTraceId() {
        String result = TRACEID.get();
        if (result == null) {
            return newTraceId();
        } else {
            return result;
        }
    }

    public static void bind(String traceId) {
        TRACEID.set(traceId);
    }
}
