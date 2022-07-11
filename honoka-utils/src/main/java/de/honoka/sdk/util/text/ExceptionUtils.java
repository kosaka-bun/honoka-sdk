package de.honoka.sdk.util.text;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

    /**
     * 隐式抛出一个异常，不用在方法签名处声明抛弃此异常，或使用try-catch捕获此异常
     */
    //不推荐使用此方法，因为如果抛出的异常是RuntimeException或它的子类，则不用声明抛弃此异常
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }

    /**
     * 将Exception类型的对象要输出的堆栈信息转换为字符串
     */
    public static String transfer(Throwable t) {
        String info = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            info = sw.toString();
            sw.close();
            pw.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return info;
    }
}
