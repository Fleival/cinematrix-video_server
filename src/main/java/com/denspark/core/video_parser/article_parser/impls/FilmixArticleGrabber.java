package com.denspark.core.video_parser.article_parser.impls;

import com.denspark.core.video_parser.article_parser.ArticleGrabber;
import com.denspark.core.video_parser.model.SiteCss;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.FilmixCommonEntity;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.utils.jsoup_utils.HtmlToPlainText;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.denspark.utils.list_utils.ListUtils.combineListsIntoOrderedMap;
import static com.denspark.utils.text_utils.GenreComparator.prepareList;
import static com.denspark.utils.text_utils.RegexTextUtils.getListOfCommaSeparatedWords;
import static com.denspark.utils.text_utils.RegexTextUtils.getListOfSpaceAndCommaSeparatedWords;

public class FilmixArticleGrabber extends ArticleGrabber {
    private Film film;
    private Person person;
    private Person personInFilm;

    AtomicInteger genreId;
    ConcurrentHashMap<String, Integer> genreMap;
    AtomicInteger personId;
    ConcurrentHashMap<String, Integer> personMap;
    ConcurrentHashMap<String, XLink> personXLinkMap;
    AtomicInteger personXLinkMapId;

    public FilmixArticleGrabber(XLinkType type, XLink xLink, SiteCss siteCss, ConcurrentHashMap<String, Integer> genreMap, AtomicInteger genreId, ConcurrentHashMap<String, Integer> personMap, AtomicInteger personId, ConcurrentHashMap<String, XLink> personXLinkMap, AtomicInteger personXLinkMapId) {
        super(type, xLink, siteCss);
        this.genreMap = genreMap;
        this.genreId = genreId;
        this.personMap = personMap;
        this.personId = personId;
        this.personXLinkMap = personXLinkMap;
        this.personXLinkMapId = personXLinkMapId;
    }

    @Override
    protected void parsePerson(Document personArticle, XLink xLink) {
        Person person = new Person();
        person.setId(xLink.getId());
        HtmlToPlainText jsoupUtils = new HtmlToPlainText();
        try {
            person.setName(personArticle.
                    selectFirst("#dle-content > article > div.full.min > div.name").text());
        } catch (NullPointerException e) {
        }
        try {
            person.setOriginName(personArticle.
                    selectFirst("#dle-content > article > div.full.min > div.origin-name").text());
        } catch (NullPointerException e) {
        }
        try {
            person.setPhotoUrl(personArticle.
                    selectFirst("#dle-content > article > div.short.min > a").attr("href"));
        } catch (NullPointerException e) {
        }
        try {
            person.setPageUrl(xLink.getUrl());
        } catch (NullPointerException e) {
        }
        try {
            person.setBirthplace(personArticle.
                    selectFirst("span:contains(Место рождения:) + span").text());
        } catch (NullPointerException e) {
        }
        try {
            person.setHeight(personArticle.
                    selectFirst("span:contains(Рост:) + span").text());
        } catch (NullPointerException e) {
        }
        try {
            person.setDateOfBirth(
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(
                            personArticle.selectFirst("span:contains(Дата рождения:) + time").attr("datetime"))
            );
        } catch (NullPointerException | ParseException e) {
        }
        try {
            person.setBio(
                    jsoupUtils.getPlainText(
                            personArticle.selectFirst("#dle-content > article > div.bio-actor > div.about")
                    )
            );
        } catch (NullPointerException e) {
        }
        try {
            person.setGenresOfFilms(getParsedEntityList(personArticle, "span:contains(Жанры:) + span", Genre.class));
        } catch (NullPointerException e) {
        }
        this.person = person;
        this.personInFilm = person;
    }

    private void updateGenreMap(List<String> names) {
        names.forEach(
                name -> {
                    if (!genreMap.containsKey(name)) {
                        genreMap.put(name, genreId.incrementAndGet());
                    }
                }
        );
    }

    private void updatePersonMap(List<String> names) {
        names.forEach(
                name -> {
                    if (!personMap.containsKey(name)) {
                        personMap.put(name, personId.incrementAndGet());
                    }
                }
        );
    }

    @Override
    protected void parseFilm(Document filmArticle) {
        Film film = new Film();
        film.setId(xLink.getId());
        try {
            film.setFilmPageUrl(xLink.getUrl());
        } catch (NullPointerException e) {
        }
        try {
            film.setName(filmArticle.selectFirst("#dle-content > article > div.full.min > div.name-block > h1.name").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setNameORIG(filmArticle.selectFirst("#dle-content > article > div.full.min > div.name-block > div.origin-name").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setCountry(filmArticle.selectFirst("span:contains(Страна:) + span").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setDescriptionStory(filmArticle.selectFirst("#dle-content > article > div.full.min > div.about > div.full-story").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setUploadDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(filmArticle.selectFirst("#dle-content > article > div.full.min > div.top-date > div.block-date > time > meta").attr("content")));
        } catch (NullPointerException | ParseException e) {
        }
        try {
            film.setDuration(filmArticle.selectFirst("span:contains(Время:) + span").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setQuality(filmArticle.selectFirst("#dle-content > article > div.full.min > div.top-date > div.quality").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setYear(filmArticle.selectFirst("span:contains(Год:) + span").text());
        } catch (NullPointerException e) {
        }
        try {
            film.setFilmPosterUrl(filmArticle.selectFirst("#dle-content > article > div.short.min > span > a").attr("href"));
        } catch (NullPointerException e) {
        }

        try {
            film.setActors(getParsedEntityList(filmArticle, "span:contains(В ролях:) + span", Person.class));
        } catch (NullPointerException e) {
        }

        try {
            film.setGenres(getParsedEntityList(filmArticle, "span:contains(Жанр:) + span", Genre.class));
        } catch (NullPointerException e) {
        }

        this.film = film;

    }


    private <T extends FilmixCommonEntity> Set<T> getParsedEntityList(Document document, String selector, Class<T> _class) {
        ArrayList<String> parsedList;
        ArrayList<String> urlList = new ArrayList<>();
        Set<T> entitySet = new LinkedHashSet<>();
        if (_class.isAssignableFrom(Person.class)) {
            Element element = document.selectFirst(selector);
            String input = element.text();
            parsedList = getListOfCommaSeparatedWords(input);

            Elements elements = element.select("span.item-content > span");
            elements.forEach(
                    span -> urlList.add(span.select("a").attr("href"))
            );
            Map<String, String> name_url;
            try {
                name_url = combineListsIntoOrderedMap(parsedList, urlList);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("IllegalArgumentException: " + e.getMessage() + " " + xLink.getUrl());
            }

            parsedList.removeIf(String::isEmpty);

            updatePersonMap(parsedList);

            name_url.forEach(
                    (name, url) -> {
                        if (url.equals("")) {
                            if (personMap.containsKey(name)) {
                                try {
                                    T entity = _class.getDeclaredConstructor().newInstance();
                                    entity.setName(name);
                                    entity.setId(personMap.get(name));
                                    entitySet.add(entity);
                                } catch (NoSuchMethodException
                                        |
                                        InstantiationException
                                        |
                                        InvocationTargetException
                                        |
                                        IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if (personMap.containsKey(name)) {
                                try {
                                    T entity = _class.getDeclaredConstructor().newInstance();
                                    entity.setId(personMap.get(name));
                                    entity.setName(name);
                                    entitySet.add(entity);
                                } catch (NoSuchMethodException
                                        |
                                        InstantiationException
                                        |
                                        InvocationTargetException
                                        |
                                        IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                entitySet.add((T) parsePerson(url));
                            }
                        }
                    }
            );
        } else if (_class.isAssignableFrom(Genre.class)) {
            parsedList = getListOfSpaceAndCommaSeparatedWords(document.selectFirst(selector).text());
            ArrayList<String> newNames = new ArrayList<>();
            parsedList.forEach(
                    name -> {
                        newNames.add(name);

                        if (name.equalsIgnoreCase("передачи с")) {
                            newNames.remove(name);
                            newNames.add("передачи c тв");

                        }
                        if (name.equalsIgnoreCase("Реальное")) {
                            newNames.remove(name);
                            newNames.add("реальное тв");
                        }
                        if ((name.equalsIgnoreCase("ТВ"))) {
                            newNames.remove(name);
                        }
                    }
            );
            parsedList = newNames;

            parsedList.replaceAll(String::toLowerCase);

            parsedList.removeIf(String::isEmpty);

            parsedList = prepareList(genreMap, parsedList);

            updateGenreMap(parsedList);

            parsedList.forEach(
                    name -> {
                        if (genreMap.containsKey(name)) {

                            try {
                                T entity = _class.getDeclaredConstructor().newInstance();
                                entity.setName(name);
                                entity.setId(genreMap.get(name));
                                entitySet.add(entity);
                            } catch (NoSuchMethodException
                                    |
                                    InstantiationException
                                    |
                                    InvocationTargetException
                                    |
                                    IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }


                    }
            );
        }
        return entitySet;
    }

    private Person parsePerson(String url) {
        Connection targetConnection = Jsoup.connect(url);
        targetConnection.timeout(TIMEOUT);
        XLink xLinkS = new XLink(personXLinkMapId.getAndIncrement(), XLinkType.PERSON_LINKS.toString(), url, false);
        personXLinkMap.putIfAbsent(url, xLinkS);
        try {
            Document personPage = targetConnection.get();
            parsePerson(personPage, xLinkS);
            xLinkS.setExistsInDb(true);
        } catch (IOException e) {
        }
        this.person = null;
        return personInFilm;
    }

    @Override
    public Film getFilm() {
        return film;
    }

    @Override
    public Person getPerson() {
        return person;
    }
}
