package de.honoka.sdk.util.text;

import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 用于编码、解码URL中的专有字符
 */
public class EncodingUtils {

    /**
     * Decodes the passed UTF-8 String using an algorithm that's
     * compatible with JavaScript's <code>decodeURIComponent</code>
     * function. Returns <code>null</code> if the String is <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    @SneakyThrows
    public static String decodeURIComponent(String s) {
        if(s == null) {
            return null;
        }
        String result;
        result = URLDecoder.decode(s, "UTF-8");
        return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's
     * compatible with JavaScript's <code>encodeURIComponent</code>
     * function. Returns <code>null</code> if the String is <code>null</code>.
     *
     * @param s The String to be encoded
     * @return the encoded String
     */
    @SneakyThrows
    public static String encodeURIComponent(String s) {
        String result;
        result = URLEncoder.encode(s, "UTF-8")
            .replaceAll("\\+", "%20")
            .replaceAll("%21", "!")
            .replaceAll("%27", "'")
            .replaceAll("%28", "(")
            .replaceAll("%29", ")")
            .replaceAll("%7E", "~");
        return result;
    }

    /**
     * Private constructor to prevent this class from being instantiated.
     */
    private EncodingUtils() {
        super();
    }
}
