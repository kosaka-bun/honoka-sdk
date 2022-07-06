package de.honoka.util.framework.hibernate;

public abstract class HibernateDaoFactory<T extends HibernateDao> {

    public abstract T newDao();
}
