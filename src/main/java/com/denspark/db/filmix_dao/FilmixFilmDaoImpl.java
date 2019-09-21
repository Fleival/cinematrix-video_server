package com.denspark.db.filmix_dao;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("filmixFilmDao")
public class FilmixFilmDaoImpl extends CinemaCommonDao<Film> implements FilmixFilmDao {
    private static final Logger logger = LoggerFactory.getLogger(FilmixFilmDaoImpl.class);

    @Override
    public List<Film> getAll() {
        return super.getAll("com.denspark.core.video_parser.video_models.cinema.Film.findAll");
    }

    @Override
    public List<Integer> getAllIdsInDb() {
        return super.getAllId("com.denspark.core.video_parser.video_models.cinema.Film.getAllId");
    }

    @Override
    public Map<Integer, Date> getIdDateMap() {
        List<Object[]> objectsList = super.getAllIdDateList("com.denspark.core.video_parser.video_models.cinema.Film.getIdAndDate");
        HashMap<Integer, Date> maps = new HashMap<>();
        for (Object[] ob : objectsList) {
            Integer key = (Integer) ob[0];
            Date value = (Date) ob[1];
            maps.put(key, value);
        }
        return maps;
    }

    @Override
    public Film create(Film entity) {
        return super.create(entity);
    }

    @Override
    public List<Film> getAllSpecific(String query) {
        return super.getAllSpecific(query);
    }

    @Override public Integer getMaxId() {
        return currentSession()
                .createNamedQuery("com.denspark.core.video_parser.video_models.cinema.Film.getMaxID", Integer.class)
                .getSingleResult();
    }

    @Override public Long getMoviesCount() {
        return currentSession()
                .createNamedQuery("com.denspark.core.video_parser.video_models.cinema.Film.getMoviesCount", Long.class)
                .getSingleResult();
    }


    @Override public List<Film> getAllSpecific(String query, int page, int maxResult) {
        Query q = currentSession()
                .createQuery(query, Film.class);
//        int page = 1;
//        int maxResult = 20;
        q.setCacheable(true);
        int maxNavigationResult = 10;
        PaginationResult<Film> result = new PaginationResult<Film>(q, page, maxResult, maxNavigationResult);
        List<Film> films = result.getList();
        int totalPages = result.getTotalPages();
        logger.info("totalPages = " + result.getTotalPages());
        logger.info("filmsListSize = " + films.size());
        int totalRecords = result.getTotalRecords();
        logger.info("totalRecords = " + result.getTotalRecords());

        // 1 2 3 4 5 ... 11 12 13
        List<Integer> navPages = result.getNavigationPages();

        return films;
    }

    @Override public List<Film> getPagedFilms(int page, int maxResult) {
        CriteriaPaginationResult<Film> filmPagedListResult = new CriteriaPaginationResult<>(Film.class, currentSession(), page, maxResult);

        List<Film> films = filmPagedListResult.getList();


        return films;
    }

    @Override
    public List searchFilmLike(String searchName, int page, int maxResult) {
        Query query = currentSession().createNamedQuery("com.denspark.core.video_parser.video_models.cinema.Film.findByName");
        final int pageIndex = Math.max(page - 1, 0);
        int fromRecordIndex = pageIndex * maxResult;
        query.setParameter("name", searchName);
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List resultList = query.getResultList();
        return resultList;

    }

//    @Override
//    public List searchFilmLike(String searchName, String year,  String[] genres , int page, int maxResult) {
//
//        String q ="SELECT f FROM Film f join f.genres g  where ";
//        if (searchName != null) {
//           q += "(f.name like '%"+searchName+"%')";
//        }
//        if (year != null) {
//            q += " AND(f.year like '%"+year+"%')";
//        }
//
//        if (genres != null) {
//            q += " AND (g.name in (:genres))";
//        }
//
//        Query  query =  currentSession().createQuery(q);
//        query.setParameter("name", searchName);
//
//
//
//        final int pageIndex = Math.max(page - 1, 0);
//        int fromRecordIndex = pageIndex * maxResult;
//        query.setFirstResult(fromRecordIndex);
//        query.setMaxResults(maxResult);
//        List resultList = query.getResultList();
//        return resultList;
//
//    }

//    @Override
//    public List<Film> searchFilmLike(String searchName, String year, String[] genres, int page, int maxResult) {
//        // Create CriteriaBuilder
//        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
//
//        // Create CriteriaQuery
//        CriteriaQuery<Film> criteria = cb.createQuery(Film.class);
//
//        Root<Film> filmRoot = criteria.from(Film.class);
//
//        Join<Film, Genre> filmGenreJoin = null;
//        if (genres != null) {
//            filmGenreJoin = filmRoot.join(Film_.genres);
//            Path<String> genresPath = filmGenreJoin.get(Genre_.NAME);
//        }
//
//        Path<String> namePath = filmRoot.get(Film_.NAME);
//        Path<String> yearPath = filmRoot.get(Film_.YEAR);
//        List<Predicate> filterListAnd = new ArrayList<>();
//
//        if (searchName != null) {
//            Predicate p = cb.and(cb.like(namePath, "%" + searchName + "%"));
//            filterListAnd.add(p);
//        }
//        if (year != null) {
//            Predicate p = cb.and(cb.like(yearPath, "%" + year + "%"));
//            filterListAnd.add(p);
//        }
//        if (genres != null) {
//            List<String> genreList = Arrays.asList(genres);
//            Predicate p = cb.and(filmGenreJoin.get(Genre_.NAME).in(genreList));
//            filterListAnd.add(p);
//        }
//
//        if (!filterListAnd.isEmpty()) {
//            Predicate[] predicatesAnd = new Predicate[filterListAnd.size()];
//            filterListAnd.toArray(predicatesAnd);
//            criteria.select(filmRoot).where((predicatesAnd));
//            if (genres != null) {
//                criteria.groupBy(filmRoot);
//                criteria.having(cb.equal(cb.countDistinct(filmGenreJoin), Arrays.asList(genres).size()));
//            }
//
//        }
//        TypedQuery<Film> query = currentSession().createQuery(criteria);
//
//        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
//        int fromRecordIndex = pageIndex * maxResult;
//        query.setFirstResult(fromRecordIndex);
//        query.setMaxResults(maxResult);
//        List resultList = query.getResultList();
//        return resultList;
//    }


//    SELECT f.*
//    FROM films f
//    INNER JOIN (
//            SELECT fg.film_id
//            FROM films_genres fg
//            INNER JOIN films f
//            ON f.ID = fg.film_id
//            INNER JOIN genres g
//            ON g.ID = fg.genre_id
//            WHERE g.genre_name IN ('комедия' , 'мелодрама' , 'ужасы' )
//            GROUP BY fg.film_id
//            HAVING COUNT(fg.film_id)=3 ) ff
//      ON f.id = ff.film_id
//    WHERE f.name_rus LIKE '%ар%'

    @Override
    public List<Film> searchFilmLike(String searchName, String year, String country, String[] genres, int page, int maxResult) {

        String sqlQuery = "SELECT f.* FROM films f ";

        if (genres != null) {
            int size = genres.length;

            String[] genresMod = Arrays.stream(genres).map(
                    s -> s = "'" + s + "'").toArray(String[]::new);
            String inCondition = String.join(",", genresMod);
            String genreFilterJoinSql = "INNER JOIN (" +
                    "            SELECT fg.film_id" +
                    "            FROM films_genres fg" +
                    "            INNER JOIN films f" +
                    "            ON f.ID = fg.film_id" +
                    "            INNER JOIN genres g" +
                    "            ON g.ID = fg.genre_id" +
                    "            WHERE g.genre_name IN (" + inCondition + ")" +
                    "            GROUP BY fg.film_id" +
                    "            HAVING COUNT(fg.film_id)=" + size + " ) ff" +
                    "      ON f.id = ff.film_id ";

            sqlQuery += genreFilterJoinSql;
        }
        if (searchName != null | year != null | country != null) {
            String whereConstSql = "WHERE";

            List<String> predicates = new ArrayList<>();
            if (searchName != null) {
                String condition = " f.name_rus LIKE '%" + searchName + "%' ";
                predicates.add(condition);
            }
            if (country != null) {
                String condition = " f.country LIKE '%" + country + "%' ";
                predicates.add(condition);
            }
            if (year != null) {
                String condition = " f.year LIKE '%" + year + "%' ";
                predicates.add(condition);
            }

            String predicatesSql = String.join("AND", predicates);
            sqlQuery += whereConstSql + predicatesSql;
        }


        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

//    public static void main(String[] args) {
//        String searchName = "CPU";
////        String searchName = null;
//        String[] genres = {"комедия", "драма", "боевик"};
////        String[] genres = null;
////        String year = "2019";
//        String year = null;
//
//
//        String sqlQuery = "SELECT f.* FROM films f ";
//
//        if (genres != null) {
//            int size = genres.length;
//
//            String[] genresMod = Arrays.stream(genres).map(
//                    s -> s = "'" + s + "'").toArray(String[]::new);
//            String inCondition = String.join(",", genresMod);
//            String genreFilterJoinSql = "INNER JOIN (" +
//                    "            SELECT fg.film_id" +
//                    "            FROM films_genres fg" +
//                    "            INNER JOIN films f" +
//                    "            ON f.ID = fg.film_id" +
//                    "            INNER JOIN genres g" +
//                    "            ON g.ID = fg.genre_id" +
//                    "            WHERE g.genre_name IN (" + inCondition + ")" +
//                    "            GROUP BY fg.film_id" +
//                    "            HAVING COUNT(fg.film_id)=" + size + " ) ff" +
//                    "      ON f.id = ff.film_id ";
//
//            sqlQuery += genreFilterJoinSql;
//        }
//        if (searchName != null | year != null) {
//            String whereConstSql = "WHERE";
//
//            List<String> predicates = new ArrayList<>();
//            if (searchName != null) {
//                String condition = " f.name_rus LIKE '%" + searchName + "%' ";
//                predicates.add(condition);
//            }
//            if (year != null) {
//                String condition = " f.year LIKE '%" + year + "%' ";
//                predicates.add(condition);
//            }
//            String predicatesSql = String.join("AND", predicates);
//            sqlQuery += whereConstSql + predicatesSql;
//        }
//
//        System.out.println(sqlQuery);
//    }

    @Override
    public List<String> getCountryList() {
        String sql = "SELECT DISTINCT f.country FROM films f WHERE f.country NOT LIKE '%,%' ORDER BY f.country ASC";
        Query query = currentSession().createNativeQuery(sql);
        List<String> resultList = query.getResultList();
        return resultList;
    }

    @Override public void updateRating(int id, int pos, int neg) {
        String hqlUpdateRating = "UPDATE Film set posRating = :pos, negRating= :neg where id = :id";
        currentSession().createQuery(hqlUpdateRating)
                .setParameter("id", id)
                .setParameter("pos", pos)
                .setParameter("neg", neg)
                .executeUpdate();
    }

    @Override public List<Film> topFilms(int page, int maxResult) {
        String sqlQuery = "SELECT STRAIGHT_JOIN  f.* " +
                "FROM films f " +
                "WHERE f.id NOT IN ( " +
                "SELECT fg.film_id" +
                " FROM films_genres fg" +
                " WHERE fg.genre_id IN (10,21,23,27,28,29,30,31,33,34,37,38,39,40,42)" +
                " GROUP BY fg.film_id\n" +
                " ) " +
                "ORDER BY f.positive_rating DESC  ";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

    @Override public List<Film> lastMovies(int page, int maxResult) {
        String sqlQuery = "SELECT STRAIGHT_JOIN  f.* " +
                "FROM films f " +
                "WHERE f.id NOT IN ( " +
                "SELECT fg.film_id" +
                " FROM films_genres fg" +
                " WHERE fg.genre_id IN (10,21,23,27,28,29,30,31,33,34,37,38,39,40,42)" +
                " GROUP BY fg.film_id\n" +
                " ) " +
                "ORDER BY f.upload_date DESC  ";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

    @Override public List<Film> lastTvSeries(int page, int maxResult) {
        String sqlQuery = "SELECT STRAIGHT_JOIN  f.* " +
                "FROM films f " +
                "WHERE f.id IN ( " +
                "SELECT fg.film_id" +
                " FROM films_genres fg" +
                " WHERE fg.genre_id IN (34,42)" +
                " GROUP BY fg.film_id\n" +
                " ) " +
                "ORDER BY f.upload_date DESC  ";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

    @Override public List<Film> allMovies(int page, int maxResult) {
        String sqlQuery = "SELECT STRAIGHT_JOIN  f.* " +
                "FROM films f " +
                "WHERE f.id NOT IN ( " +
                "SELECT fg.film_id" +
                " FROM films_genres fg" +
                " WHERE fg.genre_id IN (10,21,23,27,28,29,30,31,33,34,37,38,39,40,42)" +
                " GROUP BY fg.film_id\n" +
                " ) ";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

    @Override public List<Film> allTvSeries(int page, int maxResult) {
        String sqlQuery = "SELECT STRAIGHT_JOIN  f.* " +
                "FROM films f " +
                "WHERE f.id IN ( " +
                "SELECT fg.film_id" +
                " FROM films_genres fg" +
                " WHERE fg.genre_id IN (10,21,23,27,28,29,30,31,33,34,37,38,39,40,42)" +
                " GROUP BY fg.film_id\n" +
                " ) ";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }
}