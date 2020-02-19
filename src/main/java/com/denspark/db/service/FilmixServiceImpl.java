package com.denspark.db.service;

import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.dto.PersonNames;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.core.video_parser.video_models.cinema.Rating;
import com.denspark.db.filmix_dao.FilmixFilmDao;
import com.denspark.db.filmix_dao.FilmixGenreDao;
import com.denspark.db.filmix_dao.FilmixPersonDao;
import com.denspark.db.filmix_dao.FilmixRatingDao;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("filmixService")
public class FilmixServiceImpl implements FilmixService {

    @Autowired
    private FilmixPersonDao filmixPersonDao;
    @Autowired
    private FilmixGenreDao filmixGenreDao;
    @Autowired
    private FilmixFilmDao filmixFilmDao;
    @Autowired
    private FilmixRatingDao filmixRatingDao;

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Person createPerson(Person person) {
        return filmixPersonDao.create(person);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Genre> getAllGenres() {
        return filmixGenreDao.getAll();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Person> getAllPersons() {
        return filmixPersonDao.getAll();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Integer> getAllPersonIdsInDb() {
        return filmixPersonDao.getAllIdsInDb();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public List<Integer> getAllFilmIdsInDb() {
        return filmixFilmDao.getAllIdsInDb();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<Integer, Date> getMapOfPersonIdUploadDate() {
        List<Integer> personIdList = filmixPersonDao.getAllIdsInDb();
        Map<Integer, Date> resultMap = new HashMap<>();
        personIdList.forEach(
                id -> {
                    resultMap.put(id, null);
                }
        );
        return resultMap;
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<Integer, Date> getMapOfFilmIdUploadDate() {
        return filmixFilmDao.getIdDateMap();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Genre createGenre(Genre genre) {
        return filmixGenreDao.create(genre);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Film createFilm(Film film) {
        return filmixFilmDao.create(film);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Person loadPersonLazy(Integer id) {
        Person p = filmixPersonDao.findById(1).get();
        return p;
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Genre loadPersonLazyProxy(Integer id) {
        Genre g = filmixGenreDao.loadById(1).get();
        return g;
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Person loadPersonInit(Integer id) {
        Person p = filmixPersonDao.findById(1).get();
        Hibernate.initialize(p.getGenresOfFilms());
        return p;
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Genre createGenreOrGet(Genre genre) {
        return filmixGenreDao.ifPresentOrCreate(genre.getName());
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
    @Override
    public Optional<Genre> findGenreById(Integer id) {
        return filmixGenreDao.findById(id);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
    @Override
    public List<Genre> getSpecificGenres(String hibQuery) {
        return filmixGenreDao.getAllSpecific(hibQuery);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
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


    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
    @Override public Integer getMaxId() {
        return filmixFilmDao.getMaxId();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
    @Override public Long countAllMovies() {
        return filmixFilmDao.countAllMovies();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
    @Override public Long countYesterdayMovies() {
        return filmixFilmDao.countYesterdayMovies();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED)
    @Override public Long countLastUpdMovies() {
        return filmixFilmDao.countLastUpdMovies();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "ehcache")
    @Override public List<Film> getSpecificFilms(String hibQuery, int page, int maxResult) {
        List<Film> films = filmixFilmDao.getAllSpecific(hibQuery, page, maxResult);
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override public List<Film> getPagedFilms(int page, int maxResult) {
        return initializeFilms(filmixFilmDao.getPagedFilms(page, maxResult));
    }


    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override public List<Film> searchFilmLike(String search, int page, int maxResult) {
        search = "%" + search + "%";
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override
    public List<Film> searchFilmLike(String searchName, String year, String country, String[] genres, int page, int maxResult) {
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

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Cacheable(value = "pagedFilms")
    @Override public List<String> getCountryList() {
        return filmixFilmDao.getCountryList();
    }


    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveRating(Set<XLink> xLinkSet) {
        xLinkSet.forEach(
                xLink -> {
                    Rating rating = new Rating(xLink.getId(), xLink.getPosRating(), xLink.getNegRating());
                    filmixRatingDao.createOrUpdate(rating);
                }
        );
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public Rating getRatingById(int id) {
        Rating r = filmixRatingDao.findById(id).get();
        return r;
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override public void updateRating(Set<XLink> xLinkSet) {
        xLinkSet.forEach(
                xLink -> filmixFilmDao.updateRating(xLink.getId(), xLink.getPosRating(), xLink.getNegRating()));
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<Film> topFilms(int page, int maxResult) {
        return initializeFilms(filmixFilmDao.topFilms(page, maxResult));
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<Film> lastMovies(int page, int maxResult) {
        return filmixFilmDao.lastMovies(page, maxResult);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<Film> lastTvSeries(int page, int maxResult) {
        return filmixFilmDao.lastTvSeries(page, maxResult);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<Film> allMovies(int page, int maxResult) {
        return filmixFilmDao.allMovies(page, maxResult);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<Film> allTvSeries(int page, int maxResult) {
        return filmixFilmDao.allTvSeries(page, maxResult);
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<String> getAllPersonNamesInDb() {
        return filmixPersonDao.getAllPersonNamesInDb();
    }

    @Transactional(transactionManager = "transactionSfManager", propagation = Propagation.REQUIRED, readOnly = true)
    @Override public List<PersonNames> getPersonNamesInDb() {
        return filmixPersonDao.getPersonNamesInDb();
    }


    private List<Film> initializeFilms(List<Film> queuedFilmList) {
        queuedFilmList.forEach(
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
        return queuedFilmList;
    }
}

