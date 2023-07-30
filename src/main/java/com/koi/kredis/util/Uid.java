package com.koi.kredis.util;

/**
 * @author koi
 * @date 2023/7/30 0:05
 */
public interface Uid {

    long base = 1548989749033L;
    int short_mask = 0x3f;

    byte[] generateBytes();
    String generate();
    long generateLong();
    String generateDigits();
}
