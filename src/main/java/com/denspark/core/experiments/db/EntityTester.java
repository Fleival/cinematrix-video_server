package com.denspark.core.experiments.db;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.service.FilmixService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityTester {
    ConcurrentHashMap<String, Integer> genreMap = new ConcurrentHashMap<>();
    AtomicInteger genreId = new AtomicInteger(1);

    FilmixService filmixService;

    public EntityTester(ApplicationContext context) {
        this.filmixService = (FilmixService) context.getBean("filmixService");


    }

    public void test() {
        List<String> names_1 = new ArrayList<>();
        names_1.add("драма");
        names_1.add("комедия");
        names_1.add("боевик");
        names_1.add("драма");
        names_1.add("фантастика");

        updateGenreMap(names_1);
        System.out.println(genreMap);

        List<String> names_2 = new ArrayList<>();
        names_2.add("триллер");
        names_2.add("мелодрама");
        names_2.add("боевик");
        names_2.add("драма");
        names_2.add("приключения");

        updateGenreMap(names_2);
        System.out.println(genreMap);

    }

    public void updateGenreMap(List<String> names) {
        names.forEach(
                name -> {
                    if (!genreMap.containsKey(name)) {
                        genreMap.put(name, genreId.getAndIncrement());
                    }
                }
        );
    }


    public void testDB() {
        Person p = new Person();
        p.setId(1);
        p.setName("FOOO");
        Set<Genre> genreSet = new LinkedHashSet<>();
        genreSet.add(new Genre(1, "bar"));
        genreSet.add(new Genre(2, "cola"));
        p.setGenresOfFilms(genreSet);
        filmixService.createPerson(p);
        Person per = filmixService.loadPersonInit(1);
        System.out.println(per);

        Film f = new Film();
        f.setId(1);
        f.setName("AVATAR");
        Set<Person> people = new LinkedHashSet<>();
        people.add(per);
        f.setActors(people);
        Set<Genre> filmGenreSet = new LinkedHashSet<>();
        Genre g1 = new Genre(1, "bar");

        Genre g2 = new Genre(3, "corn");
        filmGenreSet.add(g1);
        filmGenreSet.add(g2);
        f.setGenres(filmGenreSet);

        filmixService.buildFilmAndPersist(f);
    }
}
