package com.koi.kredis.resp;

/**
 * @author koi
 * @date 2023/7/29 23:37
 */
public class RespArray implements Resp{
    Resp[] array;

    public RespArray(Resp[] array) {
        this.array = array;
    }

    public Resp[] getArray() {
        return array;
    }
}
