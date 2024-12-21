package de.honoka.sdk.util.basic.javadoc;

import java.lang.annotation.*;

/**
 * 标识一个元素是否不是线程安全的。
 * <p>
 * 如果注解在类上，表示这个类中的所有方法都不是线程安全的（即类中的任何一个方法在被一个线程调用时，
 * 另一个线程不可以同时调用同一个方法或任何一个其他方法，即使有方法被单独用{@link ThreadSafe}注解
 * 所标识，也仅表示该方法本身是线程安全的，不表示该方法和非线程安全的方法可以被同时调用）。
 * <p>
 * 如果注解在方法上，表示这个方法不是线程安全的（单个方法不可以被多个线程同时调用）。
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface NotThreadSafe {}
