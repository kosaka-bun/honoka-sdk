package de.honoka.sdk.util.framework.hibernate;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * 用于利用Hibernate的Session进行指定的查询
 */
public class HibernateQuerier<T extends HibernateDao> {

    protected final Session session;

    public HibernateQuerier(T dao) {
        session = dao.session;
    }

    protected int getCount(String hql) {
        Query<?> q = session.createQuery(hql);
        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * 单项查询
     */
    protected <BEAN> BEAN getFirst(Query<BEAN> q, boolean lock) {
        if(lock) lockQuery(q);
        List<BEAN> list = q.list();
        if(list.size() > 0) return list.get(0);
        return null;
    }

    /**
     * 为一个查询加锁
     */
    public static void lockQuery(Query<?> q) {
        q.setLockOptions(LockOptions.UPGRADE);
    }
}
