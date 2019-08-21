package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Film;
import org.hibernate.Session;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

public class CriteriaPaginationResult<E> {
    private int totalRecords;
    private int currentPage;
    private Class<E> clazz;
    private List<E> list;
    private int maxResult;
    private int totalPages;

    private int maxNavigationPage;

    private List<Integer> navigationPages;

    @SuppressWarnings("unchecked")
    public CriteriaPaginationResult(Class<E> clazz, Session currentSession, int page, int maxResult){
        //0  1  2  3  4  5  6  7  8  9  10  11  12  13  14  15  16
        //
        this.clazz = clazz;

        final int pageIndex = page - 1 < 0 ? 0 : page - 1;

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = criteriaBuilder
                .createQuery(Long.class);
        countQuery.select(criteriaBuilder
                .count(countQuery.from(clazz)));
        Long count = currentSession.createQuery(countQuery)
                .getSingleResult();
        this.totalRecords = count.intValue();

        CriteriaQuery<E> criteriaQuery = criteriaBuilder
                .createQuery(clazz);
        Root<E> from = criteriaQuery.from(clazz);
        CriteriaQuery<E> select = criteriaQuery.select(from);

        Order uploadDateDescOrder = criteriaBuilder.desc(from.get("uploadDate"));
        select.orderBy(uploadDateDescOrder);

        TypedQuery<E> typedQuery = currentSession.createQuery(select);
//        while (pageNumber < count.intValue()) {
//            typedQuery.setFirstResult(pageNumber - 1);
//            typedQuery.setMaxResults(pageSize);
//            System.out.println("Current page: " + typedQuery.getResultList());
//            pageNumber += pageSize;
//        }
        int fromRecordIndex = pageIndex * maxResult;
        this.currentPage = pageIndex + 1;
        this.maxResult = maxResult;

        typedQuery.setFirstResult(fromRecordIndex);
        typedQuery.setMaxResults(this.maxResult);
        List<E> results = typedQuery.getResultList();

        this.list = results;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public List<E> getList() {
        return list;
    }

}
