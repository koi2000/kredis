package com.koi.kredis.resp;

import com.koi.kredis.datatype.BytesWrapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author koi
 * @date 2023/7/29 23:39
 */
public class BulkString implements Resp{
    public static final BulkString NullBulkString = new BulkString(null);
    static final Charset CHARSET = StandardCharsets.UTF_8;

    BytesWrapper content;

    public BulkString(BytesWrapper content)
    {
        this.content = content;
    }

    public BytesWrapper getContent()
    {
        return content;
    }
}
