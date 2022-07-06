package de.honoka.util.framework.spring.task;

import org.aspectj.lang.JoinPoint;

/**
 * 计划任务类中run方法的专用切面类，专用于监听计划任务的执行情况
 */
public interface TaskAspect {

    /**
     * 任务类中run方法的切点
     */
    void tasksPointcut();

    /**
     * 在每一个任务方法执行前
     */
    void beforeTask(JoinPoint joinPoint);

    /**
     * 在任务方法返回后
     */
    //AfterReturning在After之后执行
    void afterTask(JoinPoint joinPoint, String reply);

    /**
     * 在任务方法未成功执行时
     */
    void afterTaskThrowing(JoinPoint joinPoint, Throwable t);
}
