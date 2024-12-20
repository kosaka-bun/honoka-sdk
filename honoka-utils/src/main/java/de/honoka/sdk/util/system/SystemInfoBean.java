package de.honoka.sdk.util.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * 用于获取内存使用信息
 */
public strictfp class SystemInfoBean {

    /**
     * 堆、非堆内存用量
     */
    public MemoryUsage heap, nonHeap;

    public static String byteNumToStr(long byteNum) {
        if(byteNum < 1024)
            return byteNum + "B";
        if(byteNum < 1024 * 1024)
            return getDecimalStr(byteNum / 1024.0, 2) + "KB";
        if(byteNum < 1024 * 1024 * 1024)
            return getDecimalStr(byteNum / 1024.0 / 1024.0, 2) + "MB";
        return getDecimalStr(byteNum / 1024.0 / 1024.0 / 1024.0,
                2) + "GB";
    }

    public static String getDecimalStr(double decimal, int digit) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(20);
        numberFormat.setGroupingUsed(false);
        //小数转字符串
        String str = numberFormat.format(decimal);
        int pointIndex = str.indexOf(".");
        //没有小数点
        if(pointIndex == -1) return str;
        //有小数点，截取指定位数小数
        int endIndex = pointIndex + digit + 1;
        if(endIndex < str.length()) {
            str = str.substring(0, endIndex);
        }
        //判断小数部分是否全0，是则只取整数部分
        String[] parts = str.split("\\.");
        if(Pattern.matches("0*", parts[1])) {
            return parts[0];
        }
        //判断最后一位不是0的数
        for(int i = str.length() - 1; i > pointIndex; i--) {
            char c = str.charAt(i);
            if(c != '0') return str.substring(0, i + 1);
        }
        throw new NumberFormatException(String.valueOf(decimal));
    }

    public SystemInfoBean() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        heap = memoryMXBean.getHeapMemoryUsage();
        nonHeap = memoryMXBean.getNonHeapMemoryUsage();
    }

    private String getInit(MemoryUsage mu) {
        return byteNumToStr(mu.getInit());
    }

    private String getUsed(MemoryUsage mu) {
        return byteNumToStr(mu.getUsed());
    }

    private String getPercentUsage(MemoryUsage mu) {
        String usageStr = getDecimalStr((mu.getUsed() /
                (double) mu.getCommitted()) * 100, 2);
        return usageStr + "%";
    }

    private String getMax(MemoryUsage mu) {
        return byteNumToStr(mu.getMax());
    }

    private String getCommited(MemoryUsage mu) {
        return byteNumToStr(mu.getCommitted());
    }

    //Heap

    public String getHeapInit() {
        return getInit(heap);
    }

    public String getHeapUsed() {
        return getUsed(heap);
    }

    public String getHeapPercentUsage() {
        return getPercentUsage(heap);
    }

    public String getHeapMax() {
        return getMax(heap);
    }

    public String getHeapCommited() {
        return getCommited(heap);
    }

    //Non Heap

    public String getNonHeapInit() {
        return getInit(nonHeap);
    }

    public String getNonHeapUsed() {
        return getUsed(nonHeap);
    }

    public String getNonHeapPercentUsage() {
        return getPercentUsage(nonHeap);
    }

    public String getNonHeapMax() {
        return getMax(nonHeap);
    }

    public String getNonHeapCommited() {
        return getCommited(nonHeap);
    }
}
