package com.denspark.db.service;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.filmix_dao.FilmixFilmDao;
import com.denspark.db.filmix_dao.FilmixGenreDao;
import com.denspark.db.filmix_dao.FilmixPersonDao;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("filmixService")
public class FilmixServiceImpl implements FilmixService {

    @Autowired
    private FilmixPersonDao filmixPersonDao;
    @Autowired
    private FilmixGenreDao filmixGenreDao;
    @Autowired
    private FilmixFilmDao filmixFilmDao;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Person createPerson(Person person) {
        return filmixPersonDao.create(person);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Genre> getAllGenres() {
        return filmixGenreDao.getAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Person> getAllPersons() {
        return filmixPersonDao.getAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Integer> getAllPersonIdsInDb() {
        return filmixPersonDao.getAllIdsInDb();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Integer> getAllFilmIdsInDb() {
        return filmixFilmDao.getAllIdsInDb();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Genre createGenre(Genre genre) {
        return filmixGenreDao.create(genre);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Film createFilm(Film film) {
        return filmixFilmDao.create(film);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Person loadPersonLazy(Integer id) {
        Person p = filmixPersonDao.findById(1).get();
        return p;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Genre loadPersonLazyProxy(Integer id) {
        Genre g = filmixGenreDao.loadById(1).get();
        return g;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Person loadPersonInit(Integer id) {
        Person p = filmixPersonDao.findById(1).get();
        Hibernate.initialize(p.getGenresOfFilms());
        return p;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Genre createGenreOrGet(Genre genre) {
        return filmixGenreDao.ifPresentOrCreate(genre.getName());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Film buildFilmAndPersist(Film film) {
        if (film.getActors() != null) {
            Set<Person> persistPersonSet = new LinkedHashSet<>();
            film.getActors().forEach(
                    person -> {
                        if (person != null) {
                            if (person.getName() != null) {
                                if (!person.getName().equals("")) {
                                    persistPersonSet.add(filmixPersonDao.findById(person.getId()).orElseGet(
                                            () -> filmixPersonDao.create(person)
                                    ));
                                }
                            }
                        }
                    }
            );
            film.setActors(persistPersonSet);
        }
        if (film.getGenres() != null) {
            Set<Genre> persistGenreSet = new LinkedHashSet<>();
            film.getGenres().forEach(
                    genre -> {
                        if (genre != null) {
                            persistGenreSet.add(filmixGenreDao.findById(genre.getId()).orElseGet(
                                    () -> filmixGenreDao.create(genre)
                            ));
                        }

                    }
            );
            film.setGenres(persistGenreSet);
        }

        return filmixFilmDao.create(film);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Person buildPersonAndPersist(Person person) {
        if (person.getGenresOfFilms() != null) {
            Set<Genre> persistGenreSet = new LinkedHashSet<>();
            person.getGenresOfFilms().forEach(
                    genre -> {
                        if (genre != null) {
                            persistGenreSet.add(filmixGenreDao.findById(genre.getId()).orElseGet(
                                    () -> filmixGenreDao.create(genre)
                            ));
                        }
                    }
            );

            person.setGenresOfFilms(persistGenreSet);
        }

        return filmixPersonDao.create(person);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Optional<Genre> findGenreById(Integer id) {
        return filmixGenreDao.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Film findFilmById(Integer id) {
        Film f = filmixFilmDao.findById(id).get();
        Hibernate.initialize(f.getGenres());
        Hibernate.initialize(f.getActors());
        f.setActorsId(
                f.getActors()
                        .stream()
                        .map(actor -> actor.getId())
                        .collect(Collectors.toSet())
        );
        f.setGenresId(
                f.getGenres()
                        .stream()
                        .map(genre -> genre.getId())
                        .collect(Collectors.toSet())
        );
        return f;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Person findPersonById(Integer id) {
        Person p = filmixPersonDao.findById(id).get();
        Hibernate.initialize(p.getGenresOfFilms());
        Hibernate.initialize(p.getFilmsParticipation());
        p.setFilmsParticipationId(
                p.getFilmsParticipation()
                        .stream()
                        .map(Film::getId)
                        .collect(Collectors.toSet())
        );
        p.setFilmsGenresId(
                p.getGenresOfFilms()
                        .stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet())
        );
        return p;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<Genre> getSpecificGenres(String hibQuery) {
        return filmixGenreDao.getAllSpecific(hibQuery);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<Film> getSpecificFilms(String hibQuery) {
        List<Film> films = filmixFilmDao.getAllSpecific(hibQuery);
        films.forEach(
                film -> {
                    Hibernate.initialize(film.getGenres());
                    Hibernate.initialize(film.getActors());
                    film.setActorsId(
                            film.getActors()
                                    .stream()
                                    .map(actor -> actor.getId())
                                    .collect(Collectors.toSet())
                    );
                    film.setGenresId(
                            film.getGenres()
                                    .stream()
                                    .map(genre -> genre.getId())
                                    .collect(Collectors.toSet())
                    );
                }
        );
        return films;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<Person> getSpecificPersons(String hibQuery) {
        List<Person> persons = filmixPersonDao.getAllSpecific(hibQuery);
        persons.forEach(
                person -> {
                    Hibernate.initialize(person.getGenresOfFilms());
                    Hibernate.initialize(person.getFilmsParticipation());
                    person.setFilmsParticipationId(
                            person.getFilmsParticipation()
                                    .stream()
                                    .map(film -> film.getId())
                                    .collect(Collectors.toSet())
                    );
                    person.setFilmsGenresId(
                            person.getGenresOfFilms()
                                    .stream()
                                    .map(genre -> genre.getId())
                                    .collect(Collectors.toSet())
                    );
                }
        );
        return persons;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override public Integer getMaxId() {
        return filmixFilmDao.getMaxId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override public Long getMoviesCount() {
        return filmixFilmDao.getMoviesCount();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "ehcache")
    @Override public List<Film> getSpecificFilms(String hibQuery, int page, int maxResult) {
        List<Film> films = filmixFilmDao.getAllSpecific(hibQuery,page,maxResult);
        films.forEach(
                film -> {
                    Hibernate.initialize(film.getGenres());
                    Hibernate.initialize(film.getActors());
                    film.setActorsId(
                            film.getActors()
                                    .stream()
                                    .map(actor -> actor.getId())
                                    .collect(Collectors.toSet())
                    );
                    film.setGenresId(
                            film.getGenres()
                                    .stream()
                                    .map(genre -> genre.getId())
                                    .collect(Collectors.toSet())
                    );
                }
        );
        return films;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override public List<Film> getPagedFilms(int page, int maxResult) {
        List<Film> films = filmixFilmDao.getPagedFilms(page,maxResult);
        films.forEach(
                film -> {
                    Hibernate.initialize(film.getGenres());
                    film.setGenresId(
                            film.getGenres()
                                    .stream()
                                    .map(genre -> genre.getId())
                                    .collect(Collectors.toSet())
                    );
//                    Hibernate.initialize(film.getActors());
//                    film.setActorsId(
//                            film.getActors()
//                                    .stream()
//                                    .map(actor -> actor.getId())
//                                    .collect(Collectors.toSet())
//                    );
                }
        );
        return films;
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override public List<Film> searchFilmLike(String search, int page, int maxResult){
        search = "%"+search+"%";
        List<Film> films = filmixFilmDao.searchFilmLike(search, page, maxResult);
        films.forEach(
                film -> {
                    Hibernate.initialize(film.getGenres());
                    film.setGenresId(
                            film.getGenres()
                                    .stream()
                                    .map(genre -> genre.getId())
                                    .collect(Collectors.toSet())
                    );
                }
        );
        return films;

    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override
    public List<Film> searchFilmLike(String searchName, String year, String country, String[] genres , int page, int maxResult){
//        String[] genreS = new String[]{"комедия", "боевик"};
        List<Film> films = filmixFilmDao.searchFilmLike(searchName, year, country, genres, page, maxResult);

        films.forEach(
                film -> {
                    Hibernate.initialize(film.getGenres());
                    film.setGenresId(
                            film.getGenres()
                                    .stream()
                                    .map(genre -> genre.getId())
                                    .collect(Collectors.toSet())
                    );
                }
        );

        return films;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override public List<String> getCountryList() {
        return filmixFilmDao.getCountryList();
    }
}

