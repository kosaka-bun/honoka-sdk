package de.honoka.sdk.util.framework.spring.task;

import de.honoka.sdk.util.various.ReflectUtils;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

/**
 * 实现类可作为任务使用
 */
public abstract class Task implements SchedulingConfigurer {

    /**
     * 由于被Spring框架所代理的对象的字段不能被直接访问，故要访问此类的可变字段，
     * 均需定义getter和setter
     */
    protected String cron;

    public String getCron() {
        return cron;
    }

    /**
     * 由Spring框架代理的此类的实例（用于传递给外部调用，使切面类生效）
     */
    public abstract Task getAgentInstance();

    /**
     * 用于提醒子类为cron字段赋初值
     * 不应在构造方法的参数处指定需要传入cron，会影响spring对此类的加载
     */
    protected abstract String initCron();

    /**
     * 任务的名称
     */
    protected abstract String getName();

    public Task() {
        cron = initCron();
    }

    /**
     * 任务是否已被提前执行
     */
    protected boolean doneInAdvance = false;

    /**
     * 任务执行时调用此方法，此方法将被对应的切面类所监视
     * 要使此方法被切面类所监视，必须在子类中重写此方法，但不需要额外实现，保持默认实现即可
     */
    public synchronized String run() throws Exception {
        if(doneInAdvance) {
            doneInAdvance = false;    //表示忽略此次执行，然后指定下一次任务未被提前执行
            return "该任务已被提前执行";
        }
        return task();
    }

    /**
     * 提前执行任务（主动调用，可多次调用）
     */
    public synchronized String runInAdvance() throws Exception {
        String reply = task();
        doneInAdvance = true;
        return reply;
    }

    /**
     * 任务要执行的具体内容
     */
    protected abstract String task() throws Exception;

    /**
     * spring计划任务注册器，用于获取计划任务列表
     */
    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    /**
     * 获取spring的计划任务列表
     */
    @SuppressWarnings("unchecked")
    private Set<ScheduledFuture<?>> getScheduledFutures() {
        return (Set<ScheduledFuture<?>>) ReflectUtils.getFieldValue(
                scheduledTaskRegistrar, "scheduledTasks");
    }

    /**
     * 存放在spring任务列表中的关于此类的计划任务实例
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * 定义和提供计划任务配置
     */
    private TriggerTask getTriggerTask() {
        //指定框架调用计划任务时需要的Runnable实例
        Runnable runnable = new TaskRunner(getAgentInstance());
        //指定任务执行后获取下一次执行时间点的方法
        Trigger trigger = triggerContext -> {
            if(cron == null) return null;    //返回null表示不再进行下一次执行
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        };
        return new TriggerTask(runnable, trigger);
    }

    /**
     * 构建计划任务，加入spring任务列表中（由spring自动调用）
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        this.scheduledTaskRegistrar = scheduledTaskRegistrar;
        //加载并启动此计划任务
		/* scheduledTaskRegistrar中的scheduler在容器加载完成前可能为null，
		   需要优先进行一次初始化，然后才能开始计划任务 */
        ReflectUtils.invokeMethod(scheduledTaskRegistrar,
                "scheduleTasks");
        start();
    }

    /**
     * 开始计划任务
     */
    public void start() {
        if(isRunning()) return;
        TriggerTask triggerTask = getTriggerTask();
        scheduledFuture = Objects.requireNonNull(scheduledTaskRegistrar.getScheduler())
                .schedule(triggerTask.getRunnable(), triggerTask.getTrigger());
        getScheduledFutures().add(scheduledFuture);
    }

    /**
     * 取消计划任务
     */
    public void cancel() {
        if(!isRunning()) return;
        scheduledFuture.cancel(true);
        getScheduledFutures().remove(scheduledFuture);
        scheduledFuture = null;
    }

    /**
     * 重新开始计划任务（指定cron值）
     */
    public void restart(String cron) {
        cancel();
        this.cron = cron;
        start();
    }

    /**
     * 是否正在运行当中（是否存在计划任务实例）
     */
    public boolean isRunning() {
        return scheduledFuture != null;
    }
}