package de.honoka.sdk.util.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TextUtils {

    /**
     * 将字符串集合转换为单行集合
     */
    public static String toSingleLineString(Collection<String> collection) {
        StringBuilder str = new StringBuilder();
        Iterator<String> itr = collection.iterator();
        while(itr.hasNext()) {
            String item = itr.next();
            if(itr.hasNext()) str.append(item).append(", ");
            else str.append(item);
        }
        return str.toString();
    }

    public static DateFormat getSimpleDateFormat() {
        return getSimpleDateFormat("normal");
    }

    /**
     * 获取一个常用的SimpleDateFormat
     */
    public static DateFormat getSimpleDateFormat(String type) {
        type = type.toLowerCase();
        switch(type) {
            case "chinese":
                return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            case "normal":
            default:
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 将bool值转换为“开”或“关”
     */
    public static String boolSwitchToString(boolean b) {
        return b ? "开" : "关";
    }

    /**
     * 获取每一行（供JDK 1.8及以下使用）
     */
    public static List<String> getLines(String s) {
        s = s.replace("\r", "");
        return Arrays.asList(s.split("\n"));
    }
    
    /**
     * 获取字符串半角长度（全角字符算2个单位，半角字符算1个单位）
     */
    public static int getHalfWidthLength(String s) {
        int count = 0;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            count += c < 0x800 ? 1 : 2;
        }
        return count;
    }
}
