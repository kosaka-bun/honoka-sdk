package de.honoka.sdk.util.concurrent;

import de.honoka.sdk.util.basic.CodeUtils;

import java.util.Iterator;
import java.util.concurrent.Callable;

public class LockUtils {

    public static <T> T synchronizedItems(Iterable<?> iterable, Callable<T> callable) {
        return synchronizedItems(iterable.iterator(), callable);
    }

    private static <T> T synchronizedItems(Iterator<?> iterator, Callable<T> callable) {
        Object obj = null;
        while(obj == null && iterator.hasNext()) {
            obj = iterator.next();
        }
        if(obj != null) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized(obj) {
                return synchronizedItems(iterator, callable);
            }
        } else {
            try {
                return callable.call();
            } catch(Throwable t) {
                CodeUtils.sneakyThrows(t);
                return null;
            }
        }
    }
}
