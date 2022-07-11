package de.honoka.sdk.util.various;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import java.util.HashMap;
import java.util.Map;

/**
 * AOP类使用的工具方法
 */
public class AspectUtils {

    /**
     * 提取JointPoint中的，切点对应方法所获得的所有参数和参数值，到Map中
     */
    //获取参数Map集合，可以获取到切点中的方法在被调用时，所获取到的参数与对应值
    public static Map<String, Object> getNameAndValue(JoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature())
                .getParameterNames();
        for(int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }
}
