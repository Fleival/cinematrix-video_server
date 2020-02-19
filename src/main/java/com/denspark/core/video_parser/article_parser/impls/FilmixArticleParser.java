package com.denspark.core.video_parser.article_parser.impls;

import com.denspark.config.CinemixServerConfiguration;
import com.denspark.core.video_parser.article_parser.ArticleGrabber;
import com.denspark.core.video_parser.article_parser.ArticleParser;
import com.denspark.core.video_parser.model.XLink;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.core.video_parser.model.dto.PersonNames;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.service.FilmixService;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class FilmixArticleParser extends ArticleParser {
    private static volatile FilmixArticleParser mInstance;

    private FilmixService filmixService;


    private FilmixArticleParser(String siteName, XLinkType type, int THREAD_COUNT, ApplicationContext context, int splitListSize, CinemixServerConfiguration configuration) {
        super(siteName, type, THREAD_COUNT, context, configuration);
        super.splitListSize = splitListSize;
        this.filmixService = (FilmixService) context.getBean("filmixService");
        initDBMap();
    }

    @Override
    public void initDBMap() {
        List<Genre> genres = filmixService.getAllGenres(); //! not syncronized!!!!!!
        int initValue = 0;
        genres.forEach(
                genre -> {
                    if (!genreMap.containsKey(genre.getName())) {
                        genreMap.put(genre.getName(), genre.getId());
                    }
                }
        );
        if (genres.size() > 0) {
            initValue = genres.size();
        }

        genreId = new AtomicInteger(initValue);

//        List<String> personNames = filmixService.getAllPersonNamesInDb();

//        List<Person> persons = filmixService.getAllPersons();

        List<PersonNames> persons = filmixService.getPersonNamesInDb();

        persons.forEach(
                person -> {
                    if (!personMap.containsKey(person.getName())) {
                        personMap.put(person.getName(), person.getId());
                    }
                }
        );
        if (persons.size() > 0) {
            initValue = persons.size();
        }

        personId = new AtomicInteger(initValue);
    }

    @Override
    protected Map<Integer, Date> getIdDateMapOfExistingEntries() {
        switch (type) {
            case FILM_LINKS:
                return filmixService.getMapOfFilmIdUploadDate();
            case PERSON_LINKS:
                return filmixService.getMapOfPersonIdUploadDate();
        }
        return new HashMap<>();
    }

    public static FilmixArticleParser getInstance(String siteName, XLinkType type, int THREAD_COUNT, ApplicationContext context, int splitListSize, CinemixServerConfiguration configuration) {
        if (mInstance == null) {
            synchronized (FilmixArticleParser.class) {
                if (mInstance == null) {
                    mInstance = new FilmixArticleParser(siteName, type, THREAD_COUNT, context, splitListSize, configuration);
                }
            }
        }
        return mInstance;
    }

    @Override
    public CompletableFuture<ArticleGrabber> parseXlink(XLink webPageXlink) {
        if (shouldVisit(webPageXlink)) {
            ArticleGrabber articleGrabber = new FilmixArticleGrabber(
                    type,
                    webPageXlink,
                    siteCss,
                    genreMap,
                    genreId,
                    personMap,
                    personId,
                    personXLinkMap,
                    personXLinkMapId);

            return CompletableFuture.supplyAsync(articleGrabber, executorService).exceptionally(
                    ex -> {
                        System.out.println("Exception in grabber: " + articleGrabber.getXLink().getId() + " error: " + ex.getMessage());

//                        stopParser();
                        return articleGrabber;
                    }
            );
        }
        return null;
    }


    @Override
    public void setStartPage(String startPage) {
        super.startPage = null;
    }

    @Override
    protected void stopInstance() {
        mInstance = null;
    }

    @Override
    protected void writePersonToDB(ArticleGrabber articleGrabber) {
        Person person = (Person)articleGrabber.getPerson();
        if(person != null) {
            if (person.getName() != null ) {
                if (!person.getName().equals("")) {
                    filmixService.buildPersonAndPersist(person);
                }
            }
        }
    }

    @Override
    protected void writeFilmToDB(ArticleGrabber articleGrabber) {
        Film film = (Film)articleGrabber.getFilm();
        if(film != null) {
            if (film.getName() != null & film.getNameORIG() != null) {
                if ((!film.getName().equals("")) | (!film.getNameORIG().equals(""))) {
                    filmixService.buildFilmAndPersist(film);
                }
            }
        }
    }

    @Override protected void updateFilmsRating() {
        filmixService.updateRating(xLinkSet);
    }
}
