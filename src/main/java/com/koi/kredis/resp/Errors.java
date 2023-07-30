package com.koi.kredis.resp;

/**
 * @author koi
 * @date 2023/7/29 23:34
 */
public class Errors implements Resp {
    String content;

    public Errors(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
