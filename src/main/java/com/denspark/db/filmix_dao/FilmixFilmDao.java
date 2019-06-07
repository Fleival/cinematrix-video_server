package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.db.abstract_dao.CinemaDao;
import com.denspark.db.abstract_dao.CinemaDaoAdditional;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FilmixFilmDao extends CinemaDao<Film>, CinemaDaoAdditional<Film> {
    List<Integer> getAllIdsInDb();
    Film create(Film film);
    List<Film> getAllSpecific(String query);
}
