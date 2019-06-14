package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.hibernate.query.Query;
import org.hibernate.type.Type;
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

    @Override public Integer getMaxId() {
        return currentSession()
                    .createNamedQuery("com.denspark.core.video_parser.video_models.cinema.Film.getMaxID", Integer.class)
                    .getSingleResult();
    }

    @Override public Long getMoviesCount() {
        return currentSession()
                .createNamedQuery("com.denspark.core.video_parser.video_models.cinema.Film.getMoviesCount", Long.class)
                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override public List<Film> getAllSpecific(String query, int start, int maxRows) {
        Query q = currentSession()
                .createQuery(query, Film.class);
        q.setFirstResult(start);
        q.setMaxResults(maxRows);
        return q.list();
    }
}
