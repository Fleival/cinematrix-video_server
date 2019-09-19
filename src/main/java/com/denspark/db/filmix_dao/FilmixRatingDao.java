package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Rating;
import com.denspark.db.abstract_dao.CinemaParentDao;

import java.util.Optional;

public interface FilmixRatingDao extends CinemaParentDao<Rating> {
    Optional<Rating> findById(Integer id);
}
