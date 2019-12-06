package com.denspark.db.filmix_dao;

import com.denspark.config.CinematrixServerConfiguration;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.db.abstract_dao.CinemaCommonDao;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.*;

@Repository("filmixFilmDao")
public class FilmixFilmDaoImpl extends CinemaCommonDao<Film> implements FilmixFilmDao {
    private static final Logger logger = LoggerFactory.getLogger(FilmixFilmDaoImpl.class);

    @Autowired
    private CinematrixServerConfiguration serverConfiguration;

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

    @Override public Long countAllMovies() {
        return currentSession()
                .createNamedQuery("com.denspark.core.video_parser.video_models.cinema.Film.getMoviesCount", Long.class)
                .getSingleResult();
    }

    @Override public Long countYesterdayMovies() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateNow = DateTime.now(DateTimeZone.forID("Europe/Kiev"));
        DateTime dateDayEarlier = dateNow.minusDays(1);
        String dateNowS = dtf.print(dateNow);
        String dateDayEarlierS = dtf.print(dateDayEarlier);
        String sql = "SELECT COUNT(*) FROM films " +
                "WHERE films.upload_date >= " + "'" + dateDayEarlierS + "' " +
                "AND films.upload_date < " + "'" + dateNowS + "'";
        Query query = currentSession().createNativeQuery(sql);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    @Override public Long countLastUpdMovies() {
        return 0L;
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
        String sqlQuery =
                "SELECT STRAIGHT_JOIN  f.* \n" +
                        "FROM films f \n" +
                        "WHERE f.id NOT IN ( \n" +
                        "                    SELECT fg.film_id \n" +
                        "                    FROM films_genres fg \n" +
                        "                    WHERE fg.genre_id IN (\n" +
                        "                        SELECT genres.ID FROM genres WHERE genres.genre_name IN (\n" +
                        listToSQLString(serverConfiguration.cinemixConfig.getFilmGenreExclude()) +
                        "                            )\n" +
                        "                        ) \n" +
                        "                    GROUP BY fg.film_id ) \n" +
                        "ORDER BY (f.positive_rating - f.negative_rating) DESC";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

    @Override public List<Film> lastMovies(int page, int maxResult) {
        String sqlQuery =
                "SELECT STRAIGHT_JOIN  f.* \n" +
                        "FROM films f \n" +
                        "WHERE f.id NOT IN ( \n" +
                        "                    SELECT fg.film_id \n" +
                        "                    FROM films_genres fg \n" +
                        "                    WHERE fg.genre_id IN (\n" +
                        "                        SELECT genres.ID FROM genres WHERE genres.genre_name IN (\n" +
                        listToSQLString(serverConfiguration.cinemixConfig.getFilmGenreExclude()) +
                        "                            )\n" +
                        "                        ) \n" +
                        "                    GROUP BY fg.film_id ) \n" +
                        "ORDER BY f.upload_date DESC";

        Query query = currentSession().createNativeQuery(sqlQuery, Film.class);
        final int pageIndex = page - 1 < 0 ? 0 : page - 1;
        int fromRecordIndex = pageIndex * maxResult;
        query.setFirstResult(fromRecordIndex);
        query.setMaxResults(maxResult);
        List<Film> resultList = query.getResultList();
        return resultList;
    }

    @Override public List<Film> lastTvSeries(int page, int maxResult) {
        String sqlQuery =
                "SELECT STRAIGHT_JOIN  f.* " +
                        "FROM films f " +
                        "WHERE f.id IN ( " +
                        "SELECT fg.film_id" +
                        " FROM films_genres fg" +
                        " WHERE fg.genre_id IN (SELECT genres.ID FROM genres WHERE genres.genre_name IN(" +
                        listToSQLString(serverConfiguration.cinemixConfig.getTvSeriesGenre()) +
                        ")" +
                        ")" +
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
                " WHERE fg.genre_id IN (" +
                "SELECT genres.ID FROM genres WHERE genres.genre_name IN(" +
                listToSQLString(serverConfiguration.cinemixConfig.getFilmGenreExclude()) +
                ")" +
                ")" +
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
        String sqlQuery =
                "SELECT STRAIGHT_JOIN  f.* " +
                        "FROM films f " +
                        "WHERE f.id IN ( " +
                        "SELECT fg.film_id" +
                        " FROM films_genres fg" +
                        " WHERE fg.genre_id IN (" +
                        "SELECT genres.ID FROM genres WHERE genres.genre_name IN(" +
                        listToSQLString(serverConfiguration.cinemixConfig.getTvSeriesGenre()) +
                        ")" +
                        ")" +
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

    private String listToSQLString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append("'");
            sb.append(list.get(i));
            if (i == (list.size() - 1)) {
                sb.append("'");
            } else {
                sb.append("',");
            }
        }
        return sb.toString();
    }
}