package com.denspark.db.abstract_dao;

public interface CinemaParentDao<E> {
    E create(E entity);

    E createOrUpdate(E entity);

    void remove(E entity);

    E mergeAndClear(E entity);

    void flushSession();


}
