package de.honoka.sdk.util.framework.hibernate;

import de.honoka.sdk.util.various.Retrier;
import org.hibernate.StaleObjectStateException;

import javax.persistence.OptimisticLockException;
import java.util.Arrays;

/**
 * 用于乐观锁重做事务
 */
public class HibernateRetrier extends Retrier {

    public HibernateRetrier() {
        super(Arrays.asList(
                OptimisticLockException.class,
                StaleObjectStateException.class)
        );
    }
}
