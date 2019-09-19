package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.db.abstract_dao.CinemaDao;
import com.denspark.db.abstract_dao.CinemaDaoAdditional;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FilmixFilmDao extends CinemaDao<Film>, CinemaDaoAdditional<Film> {
    List<Integer> getAllIdsInDb();
    Map<Integer, Date> getIdDateMap();
    Film create(Film film);
    List<Film> getAllSpecific(String query);
    List<Film> getAllSpecific(String query, int start, int maxRows);
    List<Film> getPagedFilms(int page, int maxResult);
    Integer getMaxId();
    Long getMoviesCount();
    List<Film> searchFilmLike(String search, int page, int maxResult);
    List<Film> searchFilmLike(String searchName, String year, String country,  String[] genres , int page, int maxResult);
    List<String> getCountryList ();
    void updateRating(int id, int pos, int neg);
    List<Film> topFilms(int page, int maxResult);
    List<Film> lastMovies(int page, int maxResult);
    List<Film> lastTvSeries(int page, int maxResult);
}
