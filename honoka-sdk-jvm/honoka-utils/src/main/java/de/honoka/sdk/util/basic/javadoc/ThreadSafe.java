package de.honoka.sdk.util.basic.javadoc;

import java.lang.annotation.*;

/**
 * 标识一个元素是线程安全的。
 * <p>
 * 如果注解在类上，表示这个类中的所有方法都是线程安全的。类中的任何一个方法在被一个线程调用时，
 * 另一个线程可以同时调用同一个方法或任何一个其他方法，除非某个方法被单独用{@link NotThreadSafe}
 * 注解所标识，如某些私有方法或包级私有方法。
 * <p>
 * 如果注解在方法上，表示这个方法是线程安全的。单个方法可以被多个线程同时调用。
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafe {}
