package de.honoka.sdk.util.test;

import de.honoka.sdk.util.code.DateBuilder;
import de.honoka.sdk.util.system.SystemInfoBean;
import de.honoka.sdk.util.test.util.TestUtils;
import de.honoka.sdk.util.text.ExceptionUtils;
import de.honoka.sdk.util.text.TextUtils;
import de.honoka.sdk.util.various.ReflectUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("JUnit3StyleTestMethodInJUnit4Class")
public class AllTest {

    @Test
    public void test12() {
        Class<?>[] classes = {
                int.class, Integer.class,
                String.class,
                Double.class, double.class,
                boolean.class, Boolean.class
        };
        List<String> list = new ArrayList<>();
        for(Class<?> clazz : classes) {
            list.add(clazz.getSimpleName());
        }
        System.out.println(list);
    }

    //@Test
    public void test11() {
        String[] parts = "\n\nvaerber\navrwvrvar\n\n\n".split("\n", -1);
        System.out.println(Arrays.asList(parts));
    }

    //@Test
    public void test10() {
        Color color = new Color(12, 34, 56);
        System.out.printf("#%02X%02X%02X\n", color.getRed(), color.getGreen(),
                color.getBlue());
    }

    @SneakyThrows
    //@Test
    public void test9() {
        byte[] strBytes = "中文".getBytes(StandardCharsets.UTF_8);
        byte[] strBytesGbk = "中文".getBytes("GBK");
        TestUtils.showBytes(strBytes);
        TestUtils.showBytes(strBytesGbk);
        String str = new String(strBytes, StandardCharsets.UTF_8);
        System.out.println(str);
        TestUtils.showBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    //@Test
    public void test8() {
        List<Double> decimals = Arrays.asList(
                0.10, 0.11, 0.010, 0.0010, 0.00010, 0.0000100
        );
        decimals.forEach(d -> System.out.println(
                SystemInfoBean.getDecimalStr(d, 3)));
    }

    //@Test
    public void test7() {
        SystemInfoBean sib = new SystemInfoBean();
        System.out.println(sib.getHeapInit());
        System.out.println(sib.getHeapUsed());
        System.out.println(sib.getHeapCommited());
        System.out.println(sib.getHeapMax());
        System.out.println(sib.getHeapPercentUsage());
    }

    //@Test
    public void test6() {
        String regex = "0*";
        List<String> strs = Arrays.asList(
                "", "0", "00", "01", "000", "001", "100", "0 0"
        );
        strs.forEach(s -> System.out.println(Pattern.matches(regex, s)));
    }

    //@Test
    public void test5() {
        List<Long> byteNums = Arrays.asList(
                1023L, 1024L, 1025L, 1024L * 1024 - 1, 1024L * 1024,
                1024L * 1024 * 1024 - 1, 1024L * 1024 * 1024,
                1024L * 1024 * 1024 * 1024 - 1, 1024L * 1024 * 1024 * 1024
        );
        byteNums.forEach(e -> System.out.println(SystemInfoBean.byteNumToStr(e)));
    }

    //@Test
    public void test4() {
        System.out.println(StringUtils.countMatches("abababa", "aba"));
    }

    //@Test
    public void test3() {
        class ClassA {

            public final int i = 1;
        }
        ClassA a = new ClassA();
        System.out.println(a.i);
        //a.str = "a";
        ReflectUtils.setFieldValue(a, "i", 0);
        System.out.println(a.i);
    }

    //@Test
    public void test2() {
        DateFormat dateFormat = TextUtils.getSimpleDateFormat();
        Date now = new Date();
        System.out.println(dateFormat.format(now));
        Date newDate = DateBuilder.of(now).hour(0.5).get();
        System.out.println(dateFormat.format(now));
        System.out.println(dateFormat.format(newDate));
    }

    //@Test
    public void test1() {
        //true
        System.out.println(Exception.class.isAssignableFrom(RuntimeException.class));
        //false
        System.out.println(RuntimeException.class.isAssignableFrom(Exception.class));
    }

    //@Test
    public void sneakyThrowTest() {
        ExceptionUtils.sneakyThrow(new Exception());
    }
}
