package com.denspark.resources;

import com.denspark.config.CinematrixServerConfiguration;
import com.denspark.core.experiments.db.EntityTester;
import com.denspark.core.experiments.db.MtDbWriter;
import com.denspark.core.video_parser.Parser;
import com.denspark.core.video_parser.ParserFactory;
import com.denspark.core.video_parser.article_parser.ArticleParser;
import com.denspark.core.video_parser.link_parser.LinkParser;
import com.denspark.core.video_parser.model.XLinkType;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.core.video_parser.video_models.cinema.Rating;
import com.denspark.db.ServiceData;
import com.denspark.db.service.FilmixService;
import com.google.common.annotations.VisibleForTesting;
import io.dropwizard.jersey.errors.ErrorMessage;
import org.springframework.context.ApplicationContext;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.PatternSyntaxException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/filmix")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class FilmixApiResource {

    private final ApplicationContext context;
    private FilmixService filmixService;
    private final CinematrixServerConfiguration configuration;


    @Context
    private UriInfo uriInfo;

    public FilmixApiResource(ApplicationContext context, CinematrixServerConfiguration configuration) {
        this.context = context;
        this.filmixService = (FilmixService) context.getBean("filmixService");
        this.configuration = configuration;

    }

    @GET
    public List<Genre> getAllGenres() {
        return filmixService.getAllGenres();
    }

    private Response notFoundResponse(int id) {
        Response.Status status = Response.Status.NOT_FOUND;
        return Response.status(status)
                .entity(new ErrorMessage(status.getStatusCode(), "Genre not found with id " + id))
                .type(APPLICATION_JSON)
                .build();
    }

    @VisibleForTesting
    void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @GET
    @Path("/test_mt")
    public void testMT() {
        MtDbWriter dbWriter = new MtDbWriter(4, context);
        dbWriter.starExecutor();
    }


    @GET
    @Path("/genre/{id}")
    public Response getGenre(@PathParam("id") int id) {
        Optional<Genre> genre = filmixService.findGenreById(id);
        if (genre.isPresent()) {
            return Response.ok(genre.get()).build();
        }
        return notFoundResponse(id);
    }

    @GET
    @Path("/load_genres")
    public Response loadGenres() {
        List<Genre> genres = filmixService.getAllGenres();
        return Response.ok(genres).build();
    }

    @GET
    @Path("/film/{id}")
    public Response getFilm(@PathParam("id") int id) {
        Film f = filmixService.findFilmById(id);
        Response response = Response.ok(f).build();
        return response;
    }

    @GET
    @Path("/search_films")
    public Response getFilm(@QueryParam("search") String search, @QueryParam("page") int page, @QueryParam("maxResult") int maxResult) {
        List<Film> films = filmixService.searchFilmLike(search, page, maxResult);
        Response response = Response.ok(films).build();
        return response;
    }

    @GET
    @Path("/person/{id}")
    public Response getPerson(@PathParam("id") int id) {
        Person p = filmixService.findPersonById(id);
        Response response = Response.ok(p).build();
        return response;
    }

    @GET
    @Path("/get_genres")
    public Response getSpecGenres(@QueryParam("query") String query) {
        List<Genre> genres = filmixService.getSpecificGenres(query);

        return Response.ok(genres).build();
    }

    @GET
    @Path("/get_films")
    public Response getSpecFilms(@QueryParam("query") String query) {
        List<Film> films = filmixService.getSpecificFilms(query);

        return Response.ok(films).build();
    }

    @GET
    @Path("/get_ex_films")
    public Response getSpecFilms(@QueryParam("query") String query, @QueryParam("page") int page, @QueryParam("maxResult") int maxResult) {
        List<Film> films = filmixService.getSpecificFilms(query, page, maxResult);

        return Response.ok(films).build();
    }

    @GET
    @Path("/films")
    public Response getPagedFilms(@QueryParam("page") int page, @QueryParam("maxResult") int maxResult) {
        List<Film> films = filmixService.getPagedFilms(page, maxResult);

        return Response.ok(films).build();
    }

    @GET
    @Path("/get_persons")
    public Response getSpecPersons(@QueryParam("query") String query) {
        List<Person> persons = filmixService.getSpecificPersons(query);

        return Response.ok(persons).build();
    }

    @POST
    public Response createGenre(@Valid Genre newGenre) {
        Genre savedGenre = filmixService.createGenre(newGenre);
        URI location = uriInfo.getRequestUriBuilder()
                .path(savedGenre.getId().toString())
                .build();
        return Response.created(location).entity(savedGenre).build();
    }

    @GET
    @Path("/init_parser")
    public void initParser() {
        Parser parser = ParserFactory.getInstance().getFilmixParser(
                "Filmix",
                XLinkType.PERSON_LINKS,
                1,
                context,
                configuration);
        parser.updateConfig();
    }

    @GET
    @Path("/parse_async_person_links")
    public String parseAsyncPersonLinks(@QueryParam("threads") int i, @QueryParam("start") boolean start) {

        String siteName = "Filmix";
        XLinkType type = XLinkType.fromString("PERSON_LINKS");

        String info;
        if (start) {
            info = "working";
        } else {
            info = "stopped";
        }

        LinkParser linkParser = ParserFactory.getInstance().getFilmixLinkParser(
                siteName,
                type,
                i,
                context,
                configuration);

        linkParser.setStartPage(linkParser.getSiteCss().getPersonsSectionUrl());
        linkParser.setLastPage(linkParser.getSiteCss().getPersonListLastPageIndex());

        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> {
                    if (start) {
                        linkParser.startParser();
                    } else {
                        linkParser.stopParser();
                    }
                }
        );
        return "Async link parser " + info;
    }

    @GET
    @Path("/parse_async_film_links")
    public String parseAsyncFilmLinks(@QueryParam("threads") int i, @QueryParam("start") boolean start) {

        String siteName = "Filmix";
        XLinkType type = XLinkType.fromString("FILM_LINKS");

        String info;
        if (start) {
            info = "working";
        } else {
            info = "stopped";
        }

        LinkParser linkParser = ParserFactory.getInstance().getFilmixLinkParser(
                siteName,
                type,
                i,
                context,
                configuration);
        linkParser.setStartPage(linkParser.getSiteCss().getUrl());
        linkParser.setLastPage(linkParser.getSiteCss().getArticleListLastPageIndex());

        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> {
                    if (start) {
                        linkParser.startParser();
                    } else {
                        linkParser.stopParser();
                    }
                }
        );
        return "Async link parser " + info;
    }


    @GET
    @Path("/parse_persons")
    public String parsePersons(@QueryParam("threads") int i, @QueryParam("start") boolean start, @QueryParam("split") int splitListSize) {
        String siteName = "Filmix";
        XLinkType type = XLinkType.fromString("PERSON_LINKS");

        ArticleParser personParser = ParserFactory.getInstance().getFilmixArticleParser(
                siteName,
                type,
                i,
                context,
                splitListSize,
                configuration);

        personParser.startParser();

        return "Async persons parser ";
    }

    @GET
    @Path("/parse_async_films")
    public String parseAsyncFilms(@QueryParam("threads") int i, @QueryParam("start") boolean start, @QueryParam("split") int splitListSize) {
        String siteName = "Filmix";
        XLinkType type = XLinkType.fromString("FILM_LINKS");

        String info;
        if (start) {
            info = "working";
        } else {
            info = "stopped";
        }
        ArticleParser personParser = ParserFactory.getInstance().getFilmixArticleParser(
                siteName,
                type,
                i,
                context,
                splitListSize,
                configuration);

        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> {
                    if (start) {
                        personParser.startParser();
                    } else {
                        personParser.stopParser();
                    }
                }
        );


        return "Async link parser " + info;
    }

    @GET
    @Path("/parse_films")
    public String parseFilms(@QueryParam("threads") int i, @QueryParam("start") boolean start, @QueryParam("split") int splitListSize) {
        String siteName = "Filmix";
        XLinkType type = XLinkType.fromString("FILM_LINKS");

        String info;
        if (start) {
            info = "working";
        } else {
            info = "stopped";
        }
        ArticleParser articleParser = ParserFactory.getInstance().getFilmixArticleParser(
                siteName,
                type,
                i,
                context,
                splitListSize,
                configuration);

        if (start) {
            articleParser.startParser();
        } else {
            articleParser.stopParser();
        }


        return "Async link parser " + info;
    }


    @GET
    @Path("/test_query")
    public String testQuery(@QueryParam("threads") int i, @QueryParam("start") boolean start) {

        return "Query params: threads= " + i + " start = " + start;
    }

    @GET
    @Path("/test_db")
    public String testDB() {
        EntityTester tester = new EntityTester(context);
        tester.testDB();
        return "Persist is done";
    }

    @GET
    @Path("/get_db_state")
    public Response getDBstate() {
        Integer maxId = filmixService.getMaxId();
        Long moviesCount = filmixService.countAllMovies();
        ServiceData serviceData = new ServiceData(maxId, moviesCount);
        Response response = Response.ok(serviceData).build();
        return response;
    }


    @GET
    @Path("/filtered_film_search")
    public Response searchFilm(
            @QueryParam("search") String searchName,
            @QueryParam("year") String year,
            @QueryParam("country") String country,
            @QueryParam("genres") String genres,
            @QueryParam("page") int page,
            @QueryParam("maxResult") int maxResult) {

        String[] splitArray = null;
        try {
            if (genres != null) {
                splitArray = genres.split("((?:\\*))");
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        List<Film> films = filmixService.searchFilmLike(searchName, year, country, splitArray, page, maxResult);
        Response response = Response.ok(films).build();
        return response;
    }

    @GET
    @Path("/countries")
    public Response getCountryList() {
        List<String> countryList = filmixService.getCountryList();
        return Response.ok(countryList).build();
    }

    @GET
    @Path("/test_map")
    public Response getMapOfIdUploadDate() {
        Map<Integer, Date> testMap = filmixService.getMapOfFilmIdUploadDate();
        return Response.ok(testMap).build();
    }

    @GET
    @Path("/rating_for_film")
    public Response getRatingForFilm(@QueryParam("id") int id) {
        Rating r = filmixService.getRatingById(id);
        return Response.ok(r).build();
    }

    @GET
    @Path("/top_films")
    public Response getTopFilm(@QueryParam("page") int page, @QueryParam("limit") int maxResult) {
        return Response.ok(filmixService.topFilms(page, maxResult)).build();
    }

    @GET
    @Path("/last_movies")
    public Response getLastMovies(@QueryParam("page") int page, @QueryParam("limit") int maxResult) {
        return Response.ok(filmixService.lastMovies(page, maxResult)).build();
    }

    @GET
    @Path("/last_tv_series")
    public Response getLastTvSeries(@QueryParam("page") int page, @QueryParam("limit") int maxResult) {
        return Response.ok(filmixService.lastTvSeries(page, maxResult)).build();
    }

    @GET
    @Path("/all_movies")
    public Response allMovies(@QueryParam("page") int page, @QueryParam("limit") int maxResult) {
        return Response.ok(filmixService.allMovies(page, maxResult)).build();
    }

    @GET
    @Path("/all_tv_series")
    public Response allTvSeries(@QueryParam("page") int page, @QueryParam("limit") int maxResult) {
        return Response.ok(filmixService.allTvSeries(page, maxResult)).build();
    }
}
