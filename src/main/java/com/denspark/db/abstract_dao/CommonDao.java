package com.denspark.db.abstract_dao;

import io.dropwizard.util.Generics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class CommonDao<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDao.class);
    private final Class<?> entityClass;

    @Autowired
    private SessionFactory sessionFactory;

    public CommonDao() {
        this.entityClass = Generics.getTypeParameter(this.getClass());
    }

    @SuppressWarnings("unchecked")
    public Class<E> getEntityClass() {
        return (Class<E>) this.entityClass;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public E create(E entity) {
        Session session = currentSession();
//        System.out.println(session);
        currentSession().saveOrUpdate(entity);
        return entity;
    }

    public E createOrUpdate(E entity) {
//        System.out.println(currentSession());
        currentSession().saveOrUpdate(entity);
        return entity;
    }

    public E mergeAndClear(E entity) {
        Session session = currentSession();
//        System.out.println(session);
        session.merge(entity);

        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<E> getAll(String namedQuery) {
        return currentSession().createNamedQuery(namedQuery).getResultList();
    }

    @SuppressWarnings("unchecked")
    protected List<E> getAllSpecific(String query) {
        return currentSession().createQuery(query).list();
    }

    @SuppressWarnings("unchecked")
    protected List<Integer> getAllId(String namedQuery) {
        return currentSession().createNamedQuery(namedQuery).getResultList();
    }

    public void remove(E entity) {
        currentSession().remove(entity);
    }

    public void flushSession() {
        currentSession().flush();
    }
}
