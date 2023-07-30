package com.koi.kredis.util;

/**
 * @author koi
 * @date 2023/7/30 0:08
 */
public class StringUtil {
    static final char[] EscapeCharacter = "([{/^-$¦}])?*+.".toCharArray();

    public static String EscapeString(String content) {
        if (content == null) {
            return content;
        } else if (content.length() == 0) {
            return content;
        } else {
            content = content.trim();
            StringBuilder sb = new StringBuilder();
            char[] temp = content.toCharArray();
            for (char c :
                    temp) {
                for (char e : EscapeCharacter) {
                    if (e == c) {
                        sb.append('\\');
                    }
                }
                sb.append(c);
            }
            return sb.toString();
        }
    }

}
