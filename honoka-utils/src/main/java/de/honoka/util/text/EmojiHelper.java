package de.honoka.util.text;

import java.io.UnsupportedEncodingException;

/**
 * emoji字符处理类，只适用于由两个字符组成的emoji字符
 */
public class EmojiHelper {

    /**
     * 将unicode码转为emoji字符
     *
     * @param unicode unicode码
     * @return emoji字符
     */
    public static String unicodeToEmoji(int unicode) {
        String emoji;
        StringBuilder hex = new StringBuilder(Integer.toHexString(unicode));
        while(hex.length() < 8) {
            hex.insert(0, "0");
        }
        char[] chars = hex.toString().toCharArray();
        byte[] bytes = new byte[4];
        for(int i = 0; i < chars.length; i += 2) {
            bytes[i / 2] = (byte) (Integer.parseInt("" + chars[i] + chars[i + 1], 16));
        }
        try {
            emoji = new String(bytes, "utf-32");
        } catch(UnsupportedEncodingException e) {
            emoji = "";
        }
        return emoji;
    }

    /**
     * 将emoji字符转为unicode码
     *
     * @param emoji emoji字符
     * @return unicode码
     */
    public static int emojiToUnicode(String emoji) {
        int unicode;
        try {
            byte[] bytes = emoji.getBytes("utf-32");
            StringBuilder sb = new StringBuilder();
            String temp;
            for(byte aByte : bytes) {
                temp = Integer.toHexString(aByte & 0xFF);
                if(temp.length() == 1) {
                    //1得到一位的进行补0操作
                    sb.append("0");
                }
                sb.append(temp);
            }
            unicode = Integer.parseInt(sb.toString(), 16);
        } catch(Exception e) {
            return 0;
        }
        return unicode;
    }
}
