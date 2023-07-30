package com.koi.kredis.datatype;

/**
 * @author koi
 * @date 2023/7/29 20:53
 */
public class RedisString implements RedisData{

    private volatile long timeout;
    private BytesWrapper value;

    public RedisString(){}

    public RedisString(BytesWrapper value){
        this.value = value;
        this.timeout = -1;
    }

    public BytesWrapper getValue()
    {
        return value;
    }

    public void setValue(BytesWrapper value)
    {
        this.value = value;
    }

    @Override
    public void clear() {
        this.value = null;
        this.timeout = -1;
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
