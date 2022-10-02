package de.honoka.sdk.util.framework.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * 基于个人习惯，对Hibernate Session的简单封装，用于访问指定的数据库
 */
@SuppressWarnings("deprecation")
//标记为抽象类，使用者必须继承此类，以对每个不同数据源的Dao对象进行分类
public abstract class HibernateDao implements AutoCloseable {

    public final Session session;

    public final Transaction transaction;

    private boolean closed = false;

    /**
     * 获取会话，再从会话中获取事务
     */
    public HibernateDao(SessionFactory sessionFactory) {
        session = sessionFactory.openSession();
        transaction = session.getTransaction();
    }

    /**
     * 开始事务
     */
    public void begin() {
        if(!transaction.isActive())
            transaction.begin();
    }

    /**
     * 提交事务
     */
    public void commit() {
        if(transaction.isActive())
            transaction.commit();
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        if(transaction.isActive())
            transaction.rollback();
    }

    @Override
    public void close() {
        //关闭时，事务仍处于活动状态（未提交、未回滚），则回滚
        if(transaction.isActive())
            transaction.rollback();
        if(session.isOpen())
            session.close();
        closed = true;
    }

    //如果忘记关闭Dao对象，将在其将被回收时关闭
    @Override
    protected void finalize() throws Throwable {
        if(!closed) close();
        super.finalize();
    }
}
