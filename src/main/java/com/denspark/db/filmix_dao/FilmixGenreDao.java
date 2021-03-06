package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.db.abstract_dao.CinemaDao;
import com.denspark.db.abstract_dao.CinemaDaoAdditional;

import java.util.List;


public interface FilmixGenreDao extends CinemaDao<Genre>, CinemaDaoAdditional<Genre> {
    Genre create(String name);

    Genre createOrUpdate(String name);

    Genre ifPresentOrCreate(String name);

    List<Genre> getAllSpecific(String query);
}
