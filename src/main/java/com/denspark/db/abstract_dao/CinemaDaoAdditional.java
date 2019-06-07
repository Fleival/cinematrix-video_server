package com.denspark.db.abstract_dao;

import java.util.List;
import java.util.Optional;

public interface CinemaDaoAdditional<E> extends CinemaDao<E> {

    Optional<E> findById(Integer id);

    Optional<E> loadById(Integer id);

    List<E> getAll();
}
