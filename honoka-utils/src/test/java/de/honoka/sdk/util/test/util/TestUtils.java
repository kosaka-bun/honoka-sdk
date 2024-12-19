package de.honoka.sdk.util.test.util;

import lombok.SneakyThrows;

public class TestUtils {

    @SneakyThrows
    public static void showStringBytes(String s, String charset) {
        byte[] bytes = s.getBytes(charset);
        showBytes(bytes);
    }

    public static void showBytes(byte[] bytes) {
        //System.out.println(Arrays.asList(ArrayUtils.toObject(bytes)));
    }
}
