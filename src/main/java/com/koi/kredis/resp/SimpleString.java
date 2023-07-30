package com.koi.kredis.resp;

/**
 * @author koi
 * @date 2023/7/29 23:30
 */
public class SimpleString implements Resp{
    public static final SimpleString OK = new SimpleString("OK");
    private final String content;

    public SimpleString(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
