package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("filmixPersonDao")
public class FilmixPersonDaoImpl extends CinemaCommonDao<Person> implements FilmixPersonDao {
    @Override
    public List<Person> getAll() {
        return super.getAll("com.denspark.core.video_parser.video_models.cinema.Person.findAll");
    }

    @Override
    public List<Integer> getAllIdsInDb() {
        return super.getAllId("com.denspark.core.video_parser.video_models.cinema.Person.getAllId");
    }

    @Override
    public Person loadPersonLazy(Integer id) {
        Person p = findById(1).get();
        Hibernate.initialize(p.getGenresOfFilms());
        return p;
    }

    @Override
    public List<Person> getAllSpecific(String query) {
        return super.getAllSpecific(query);
    }
}

