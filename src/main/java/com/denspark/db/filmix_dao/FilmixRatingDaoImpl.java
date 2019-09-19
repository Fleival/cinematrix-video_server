package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Rating;
import com.denspark.db.abstract_dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository("filmixRatingDao")
public class FilmixRatingDaoImpl extends CommonDao<Rating> implements FilmixRatingDao {
    public Optional<Rating> findById(Integer id) {
        Rating rating = this.currentSession().get(getEntityClass(), Objects.requireNonNull(id));
        return Optional.ofNullable(rating);
    }

}
