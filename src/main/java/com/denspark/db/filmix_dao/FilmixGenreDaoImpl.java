package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Genre;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.data.util.Optionals.ifPresentOrElse;

@Repository("filmixGenreDao")
public class FilmixGenreDaoImpl extends CinemaCommonDao<Genre> implements FilmixGenreDao {
    @Override
    public List<Genre> getAll() {
        return getAll("com.denspark.core.video_parser.video_models.cinema.Genre.findAll");
    }

    @Override
    public Genre create(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return create(genre);
    }

    @Override
    public Genre createOrUpdate(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return createOrUpdate(genre);
    }

    @Override
    public Genre ifPresentOrCreate(String name) {
        final AtomicReference<Genre> uncheckedGenre = new AtomicReference<>();
        ifPresentOrElse(
                filterNameField(name),
                uncheckedGenre::set,
                ()->{
                    Genre genre = new Genre();
                    genre.setName(name);
                    uncheckedGenre.set(create(genre));
                }
        );
        return uncheckedGenre.get();
    }

    @Override
    public List<Genre> getAllSpecific(String query) {
        return super.getAllSpecific(query);
    }
}