package com.denspark.db.service;

import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.core.video_parser.video_models.cinema.Rating;

import java.util.*;

public interface FilmixService {
    Person createPerson(Person person);

    List<Integer> getAllPersonIdsInDb();

    List<Integer> getAllFilmIdsInDb();

    Map<Integer, Date> getMapOfFilmIdUploadDate();

    Map<Integer, Date> getMapOfPersonIdUploadDate();

    List<Genre> getAllGenres();

    Optional<Genre> findGenreById(Integer id);

    Film findFilmById(Integer id);

    Person findPersonById(Integer id);

    List<Genre> getSpecificGenres(String hibQuery);

    List<Film> getSpecificFilms(String hibQuery);

    List<Film> getSpecificFilms(String hibQuery, int start, int maxRows );

    List<Film> getPagedFilms(int page, int maxResult);

    List<Person> getSpecificPersons(String hibQuery);

    List<Person> getAllPersons();

    Genre createGenre(Genre genre);

    Genre createGenreOrGet(Genre genre);

    Film createFilm(Film film);

    Person loadPersonLazy(Integer id);

    Genre loadPersonLazyProxy(Integer id);

    Person loadPersonInit(Integer id);

    Film buildFilmAndPersist(Film film);

    Person buildPersonAndPersist(Person person);

    Integer getMaxId();

    Long getMoviesCount();

    List<Film> searchFilmLike(String search, int page, int maxResult);

    List<Film> searchFilmLike(String searchName, String year, String country, String[] genres , int page, int maxResult);

    List<String> getCountryList ();

    void saveRating(Set<XLink> xlinkSet);

    Rating getRatingById(int id);

    void updateRating(Set<XLink> xLinkSet);

    List<Film> topFilms(int page, int maxResult);

    List<Film> lastMovies(int page, int maxResult);

    List<Film> lastTvSeries(int page, int maxResult);

    List<Film> allMovies(int page, int maxResult);

    List<Film> allTvSeries(int page, int maxResult);
}
