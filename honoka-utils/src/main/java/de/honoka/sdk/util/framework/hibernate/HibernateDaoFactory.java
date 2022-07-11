package de.honoka.sdk.util.framework.hibernate;

public abstract class HibernateDaoFactory<T extends HibernateDao> {

    public abstract T newDao();
}
