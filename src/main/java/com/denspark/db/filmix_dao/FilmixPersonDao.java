package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.model.dto.PersonNames;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.abstract_dao.CinemaDao;
import com.denspark.db.abstract_dao.CinemaDaoAdditional;

import java.util.List;


public interface FilmixPersonDao extends CinemaDao<Person>, CinemaDaoAdditional<Person> {
    List<Integer> getAllIdsInDb();

    Person loadPersonLazy(Integer id);

    List<Person> getAllSpecific(String query);

    List<String> getAllPersonNamesInDb();

    List<PersonNames> getPersonNamesInDb();
}
