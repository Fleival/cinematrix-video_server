package com.denspark.core.experiments.db;

import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.db.filmix_dao.FilmixGenreDao;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class CallableDbWriter implements Callable<CallableDbWriter> {
    private final FilmixGenreDao genreDao;
    private final ApplicationContext context;
    private AtomicLong idCounter;

    public CallableDbWriter(AtomicLong idCounter, ApplicationContext context) {
        this.context = context;
        this.genreDao = (FilmixGenreDao)context.getBean("filmixGenreDao");
        this.idCounter = idCounter;
    }

    @Override
    public CallableDbWriter call() throws Exception {

        writeTestValues();

        return this;
    }

    private void writeTestValues() {

        for (int i = 0; i < 5; i++) {
            Genre genre = new Genre("TestGenre " + idCounter.get());
            idCounter.incrementAndGet();
            genreDao.create(genre);
        }

    }
}

