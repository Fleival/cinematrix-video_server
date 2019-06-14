package com.denspark.db.service;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;

import java.util.List;
import java.util.Optional;

public interface FilmixService {
    Person createPerson(Person person);

    List<Integer> getAllPersonIdsInDb();

    List<Integer> getAllFilmIdsInDb();

    List<Genre> getAllGenres();

    Optional<Genre> findGenreById(Integer id);

    Film findFilmById(Integer id);

    Person findPersonById(Integer id);

    List<Genre> getSpecificGenres(String hibQuery);

    List<Film> getSpecificFilms(String hibQuery);

    List<Film> getSpecificFilms(String hibQuery, int start, int maxRows );

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

    Long getMoviesCount();;
}
