package com.denspark.db.abstract_dao;

import java.util.List;
import java.util.Optional;

public interface CinemaDao<E> extends CinemaParentDao<E> {

    E createOrGetExist(E entity);

    Optional<E> filterNameField(String name);

    List<E> getAllSpecific(String query);


}

