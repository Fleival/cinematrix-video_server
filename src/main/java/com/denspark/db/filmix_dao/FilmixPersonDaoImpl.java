package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.model.dto.PersonNames;
import com.denspark.core.video_parser.video_models.cinema.Person;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository("filmixPersonDao")
public class FilmixPersonDaoImpl extends CinemaCommonDao<Person> implements FilmixPersonDao {
    @Override
    public List<Person> getAll() {
        return super.getAll("com.denspark.core.video_parser.video_models.cinema.Person.findAll");
    }

    @Override
    public List<String> getAllPersonNamesInDb() {
//        Criteria criteria = currentSession().createCriteria(Person.class);
//        criteria = criteria.setProjection(
//                Projections.projectionList()
//                        .add(Projections.id())
//                        .add(Projections.property("name")));

        TypedQuery<String> query = currentSession().createQuery(
                "SELECT p.name FROM Person AS p", String.class);
        List<String> results = query.getResultList();
        return results;
    }

    @Override
    public List<PersonNames> getPersonNamesInDb() {
        List<PersonNames> productEntities = null;

        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<PersonNames> query = builder.createQuery(PersonNames.class);
        Root<Person> root = query.from(Person.class);
        query.select(builder.construct(PersonNames.class, root.get("id"), root.get("name")));
        productEntities = currentSession().createQuery(query).getResultList();

        return productEntities;
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

