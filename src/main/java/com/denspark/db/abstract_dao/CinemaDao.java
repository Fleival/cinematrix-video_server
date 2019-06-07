package com.denspark.db.abstract_dao;

import java.util.List;
import java.util.Optional;

public interface CinemaDao<E> {
    E create(E entity);

    E createOrUpdate(E entity);

    void remove(E entity);

    E createOrGetExist(E entity);

    Optional<E> filterNameField(String name);

    E mergeAndClear(E entity);

    void flushSession();

    List<E> getAllSpecific(String query);
}

