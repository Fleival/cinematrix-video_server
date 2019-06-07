package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.abstract_dao.CinemaDao;
import com.denspark.db.abstract_dao.CinemaDaoAdditional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface FilmixPersonDao extends CinemaDao<Person>, CinemaDaoAdditional<Person> {
     List<Integer> getAllIdsInDb();
     Person loadPersonLazy (Integer id);
     List<Person> getAllSpecific(String query);
}
