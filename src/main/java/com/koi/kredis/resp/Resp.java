package com.koi.kredis.resp;

import com.koi.kredis.datatype.BytesWrapper;
import io.netty.buffer.ByteBuf;
/**
 * @author koi
 * @date 2023/7/29 23:27
 */
public interface Resp {

    static void write(Resp resp,ByteBuf buffer){
        if (resp instanceof SimpleString){
            buffer.writeByte(RespType.STATUS.getCode());
            String content = ((SimpleString) resp).getContent();
            char[] charArray = content.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
            buffer.writeByte(RespType.R.getCode());
            buffer.writeByte(RespType.N.getCode());
        }else if (resp instanceof Errors) {
            buffer.writeByte(RespType.ERROR.getCode());
            String content = ((Errors) resp).getContent();
            char[] charArray = content.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
            buffer.writeByte(RespType.R.getCode());
            buffer.writeByte(RespType.N.getCode());
        } else if (resp instanceof RespInt) {
            buffer.writeByte(RespType.INTEGER.getCode());
            String content = String.valueOf(((RespInt) resp).getValue());
            char[] charArray = content.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
            buffer.writeByte(RespType.R.getCode());
            buffer.writeByte(RespType.N.getCode());
        } else if (resp instanceof BulkString) {
            buffer.writeByte(RespType.BULK.getCode());
            BytesWrapper content = ((BulkString) resp).getContent();
            if (content == null) {
                buffer.writeByte(RespType.ERROR.getCode());
                buffer.writeByte(RespType.ONE.getCode());
                buffer.writeByte(RespType.R.getCode());
                buffer.writeByte(RespType.N.getCode());
            } else if (content.getByteArray().length == 0) {
                buffer.writeByte(RespType.ZERO.getCode());
                buffer.writeByte(RespType.R.getCode());
                buffer.writeByte(RespType.N.getCode());
                buffer.writeByte(RespType.R.getCode());
                buffer.writeByte(RespType.N.getCode());
            } else {
                String length = String.valueOf(content.getByteArray().length);
                char[] charArray = length.toCharArray();
                for (char each : charArray) {
                    buffer.writeByte((byte) each);
                }
                buffer.writeByte(RespType.R.getCode());
                buffer.writeByte(RespType.N.getCode());
                buffer.writeBytes(content.getByteArray());
                buffer.writeByte(RespType.R.getCode());
                buffer.writeByte(RespType.N.getCode());
            }
        } else if (resp instanceof RespArray) {
            buffer.writeByte(RespType.MULTYBULK.getCode());
            Resp[] array = ((RespArray) resp).getArray();
            String length = String.valueOf(array.length);
            char[] charArray = length.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
            buffer.writeByte(RespType.R.getCode());
            buffer.writeByte(RespType.N.getCode());
            for (Resp each : array) {
                write(each, buffer);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

}
