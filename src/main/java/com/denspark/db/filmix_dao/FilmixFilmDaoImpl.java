package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("filmixFilmDao")
public class FilmixFilmDaoImpl extends CinemaCommonDao<Film> implements FilmixFilmDao {
    @Override
    public List<Film> getAll() {
        return super.getAll("com.denspark.core.video_parser.video_models.cinema.Film.findAll");
    }

    @Override
    public List<Integer> getAllIdsInDb() {
        return super.getAllId("com.denspark.core.video_parser.video_models.cinema.Film.getAllId");
    }

    @Override
    public Film create(Film entity) {
        return super.create(entity);
    }

    @Override
    public List<Film> getAllSpecific(String query) {
        return super.getAllSpecific(query);
    }
}
