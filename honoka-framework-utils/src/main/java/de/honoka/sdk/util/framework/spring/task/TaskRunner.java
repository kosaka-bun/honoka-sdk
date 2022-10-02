package de.honoka.sdk.util.framework.spring.task;

/**
 * Spring框架在指定的时间执行此计划任务时需要调用的Runnable类
 * Spring框架在执行计划任务时，不直接调用计划任务的任务方法，而是通过调用某个Runnable类的
 * run方法，来间接调用计划任务的任务方法。
 */
public class TaskRunner implements Runnable {

    private final Task task;

    public TaskRunner(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        try {
            task.run();
        } catch(Exception e) {
            //不打印堆栈信息，异常的堆栈信息将由切面类的方法进行处理
        }
    }
}
