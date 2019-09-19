package com.denspark.db.abstract_dao;

import com.denspark.core.video_parser.video_models.cinema.FilmixCommonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.data.util.Optionals.ifPresentOrElse;


public abstract class CinemaCommonDao<E extends FilmixCommonEntity> extends CommonDao<E>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(CinemaCommonDao.class);


    @SuppressWarnings("unchecked")
    protected List<Object[]> getAllIdDateList(String namedQuery) {
        return currentSession().createNamedQuery(namedQuery).getResultList();
    }



    public Optional<E> findById(Integer id) {
        E entity = this.currentSession().get(getEntityClass(), Objects.requireNonNull(id));
        return Optional.ofNullable(entity);
    }

    public Optional<E> loadById(Integer id) {
        E entity = this.currentSession().load(getEntityClass(), Objects.requireNonNull(id));
        return Optional.ofNullable(entity);
    }



    public Optional<E> filterNameField(String name) {
        Query query = currentSession().createNamedQuery(getEntityClass().getName() + ".findByName");
        query.setParameter("name", name);
        return Optional.ofNullable(uniqueResult(query));
    }

    protected Optional<E> filterQuery(Query query) {
        return Optional.ofNullable(uniqueResult(query));
    }

    public E createOrGetExist(E entity) {
        return createOrGetExist("name", entity);
    }

    private Optional<E> filterByFieldName(String field, String name) {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = builder.createQuery(getEntityClass());
        Root<E> entityRoot = criteriaQuery.from(getEntityClass());
        criteriaQuery.select(entityRoot).where(builder.like(
                builder.lower(entityRoot.get(field)),
                name.toLowerCase())
        );
        return Optional.ofNullable((E) uniqueResult(criteriaQuery));//not perfect but it works
    }

    private E createOrGetExist(String field, E entity) {
        final AtomicReference<E> uncheckedEntity = new AtomicReference<>();
        ifPresentOrElse(filterByFieldName(field, entity.getName()),
                (entityInDB -> {
                    uncheckedEntity.set(entityInDB);

                    LOGGER.info("Entity exist : " + uncheckedEntity.get());
                }
                ),
                () -> {
                    uncheckedEntity.set(create(entity));
                    LOGGER.info("Entity not found, creating new  : " + uncheckedEntity.get());
                }
        );

        return uncheckedEntity.get();
    }

    private E uniqueResult(Query query) throws NonUniqueResultException {
        List<E> list = query.getResultList();
        return getEntity(list);
    }

    private E uniqueResult(CriteriaQuery<E> criteriaQuery) throws NonUniqueResultException {
        List<E> list = currentSession().createQuery(criteriaQuery).getResultList();
        return getEntity(list);
    }

    private E getEntity(List<E> list) {
        int size = list.size();
        if (size == 0) {
            return null;
        }
        E first = list.get(0);
        for (int i = 1; i < size; i++) {
            if (list.get(i) != first) {
                throw new NonUniqueResultException(
                        "query did not return a unique result: " + list.size());
            }
        }
        return first;
    }

}
