package com.denspark.resources;

import com.denspark.CinematrixVideoConfiguration;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/test_spring")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class FilmixApiResource {

    private final ApplicationContext context;
    private FilmixService filmixService;
    private final CinematrixVideoConfiguration configuration;


    @Context
    private UriInfo uriInfo;

    public FilmixApiResource(ApplicationContext context, CinematrixVideoConfiguration configuration) {
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
    @Path("/film/{id}")
    public Response getFilm(@PathParam("id") int id) {
        Film f = filmixService.findFilmById(id);
        Response response = Response.ok(f).build();
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
    @Path("/parse_async_persons")
    public String parseAsyncPersons(@QueryParam("threads") int i, @QueryParam("start") boolean start,@QueryParam("split") int splitListSize) {
        String siteName = "Filmix";
        XLinkType type = XLinkType.fromString("PERSON_LINKS");

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
        Long moviesCount = filmixService.getMoviesCount();
        ServiceData serviceData = new ServiceData(maxId,moviesCount);
        Response response = Response.ok(serviceData).build();
        return response;
    }
}
