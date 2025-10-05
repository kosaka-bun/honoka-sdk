package de.honoka.sdk.util.basic;

public class ActionUtils {

    /**
     * 忽略异常执行一段代码
     */
    public static void doIgnoreException(boolean printStackTrace, ThrowsRunnable action) {
        try {
            action.throwsRun();
        } catch(Throwable t) {
            if(printStackTrace) t.printStackTrace();
        }
    }

    public static void doIgnoreException(ThrowsRunnable action) {
        doIgnoreException(false, action);
    }

    /**
     * 控制台可视化执行一段代码
     */
    public static void doAction(String name, ThrowsRunnable action) {
        System.out.println("开始执行" + name + "……");
        try {
            action.throwsRun();
            System.out.println(name + "执行完成");
        } catch(Throwable t) {
            System.err.println(name + "未成功执行，错误信息如下：");
            t.printStackTrace();
        }
    }
}
