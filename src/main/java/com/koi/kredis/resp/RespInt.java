package com.koi.kredis.resp;

public class RespInt implements Resp {
    int value;

    public RespInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
