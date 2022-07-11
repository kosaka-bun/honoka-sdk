package de.honoka.sdk.util.various;

import de.honoka.sdk.util.code.ThrowsRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 列表运行器，可以接收很多个runnable，依次运行它们
 */
public class ListRunner {

    //此类的run方法将抛弃异常
    private static class Action implements Comparable<Action> {

        public ThrowsRunnable runnable;

        //优先级，默认为最低优先级
        public int priority = Integer.MAX_VALUE;

        public Action(ThrowsRunnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public int compareTo(Action o) {
            //如果参数等于此实例，则返回值 0；
            //如果此实例小于参数，则返回一个小于 0 的值；
            //如果此实例大于参数，则返回一个大于 0 的值。
            if(o.priority == priority) return 0;
            return priority < o.priority ? -1 : 1;
        }

        public void run() throws Throwable {
            runnable.throwsRun();
        }
    }

    private final List<Action> actions = new ArrayList<>();

    //依次执行每个操作时是否要忽略异常
    private final boolean ignoreExceptions;

    public ListRunner(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public void add(ThrowsRunnable runnable) {
        actions.add(new Action(runnable));
    }

    public void add(ThrowsRunnable runnable, int priority) {
        Action action = new Action(runnable);
        action.priority = priority;
        actions.add(action);
    }

    //为便于使用，run方法不抛出异常，而是返回异常对象
    //返回值为null时表示运行时无异常
    public Throwable run() {
        //先对列表按优先级排序
        Collections.sort(actions);
        for(Action action : actions) {
            try {
                action.run();
            } catch(Throwable t) {
                if(!ignoreExceptions) return t;
            }
        }
        return null;
    }
}
