package de.honoka.sdk.util.code;

import java.util.Date;

/**
 * 用于根据一个时间对象构建出需要的时间
 */
public class DateBuilder {

    /**
     * 利用传入的时间对象新创建的时间对象，对它进行调整不会影响原有时间对象
     */
    private final Date date;

    private DateBuilder(Date date) {
        //如果直接使用传入的date对象，那么在对它进行修改时就会影响此传入对象原本的值
        this.date = new Date(date.getTime());
    }

    private DateBuilder(long timeMillis) {
        this.date = new Date(timeMillis);
    }

    public static DateBuilder of(Date date) {
        return new DateBuilder(date);
    }

    public static DateBuilder of(long timeMillis) {
        return new DateBuilder(timeMillis);
    }

    /**
     * 在现有时间的基础上加上n秒
     */
    public DateBuilder second(double n) {
        date.setTime((long) (date.getTime() + n * 1000));
        return this;
    }

    public DateBuilder minute(double n) {
        return second(n * 60);
    }

    public DateBuilder hour(double n) {
        return minute(n * 60);
    }

    public DateBuilder day(double n) {
        return hour(n * 24);
    }

    public Date get() {
        return date;
    }
}
