package com.denspark.core.video_parser.video_models.cinema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "films")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Film.findAll",
                        query = "SELECT f FROM Film f"
                ),
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Film.findByName",
                        query = "SELECT f FROM Film f"
                                + " where f.name like :name "
                ),
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Film.getAllId",
                        query = "SELECT f.id FROM Film f"
                ),

                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Film.getMaxID",
                        query = "SELECT MAX(f.id) AS max_id FROM Film f"
                ),

                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Film.getMoviesCount",
                        query = "SELECT COUNT(*) AS movies_count FROM Film f"
                )
        }
)
public class Film implements FilmixCommonEntity,CinemaEntity, com.denspark.core.video_parser.model.Film {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "name_rus")
    private String name;
    @Column(name = "name_orig")
    private String nameORIG;
    @Column(name = "quality")
    private String quality;
    @Column(name = "upload_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss.S", timezone = "Europe/Moscow")
    private Date uploadDate;
    @Column(name = "description_story")
    @Type(type = "text")
    private String descriptionStory;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "films_persons",
            joinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "ID")}
    )
    private Set<Person> actors;

    @Transient
    @JsonInclude
    private Set<Integer> actorsId;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "films_genres",
            joinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id", referencedColumnName = "ID")}
    )
    private Set<Genre> genres;
    @Transient
    @JsonInclude
    private Set<Integer> genresId;

    @Column(name = "year")
    private String year;
    @Column(name = "country")
    private String country;
    @Column(name = "duration")
    private String duration;
    @Column(name = "url_film_page")
    private String filmPageUrl;
    @Column(name = "url_film_poster")
    private String filmPosterUrl;

    public Film() {
    }

    public Film(String name, String nameORIG, String quality, Date uploadDate, String descriptionStory, Set<Person> actors, Set<Genre> genres, String year, String country, String duration, String filmPageUrl, String filmPosterUrl) {
        this.name = name;
        this.nameORIG = nameORIG;
        this.quality = quality;
        this.uploadDate = uploadDate;
        this.descriptionStory = descriptionStory;
        this.actors = actors;
        this.genres = genres;
        this.year = year;
        this.country = country;
        this.duration = duration;
        this.filmPageUrl = filmPageUrl;
        this.filmPosterUrl = filmPosterUrl;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getNameORIG() {
        return nameORIG;
    }

    public void setNameORIG(String nameORIG) {
        this.nameORIG = nameORIG;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDescriptionStory() {
        return descriptionStory;
    }

    public void setDescriptionStory(String descriptionStory) {
        this.descriptionStory = descriptionStory;
    }

    @JsonIgnore
    public Set<Person> getActors() {
        return actors;
    }

    public void setActors(Set<Person> actors) {
        this.actors = actors;
    }

    @JsonIgnore
    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFilmPageUrl() {
        return filmPageUrl;
    }

    public void setFilmPageUrl(String filmPageUrl) {
        this.filmPageUrl = filmPageUrl;
    }

    public String getFilmPosterUrl() {
        return filmPosterUrl;
    }

    public void setFilmPosterUrl(String filmPosterUrl) {
        this.filmPosterUrl = filmPosterUrl;
    }

    public Set<Integer> getActorsId() {
        return actorsId;
    }

    public void setActorsId(Set<Integer> actorsId) {
        this.actorsId = actorsId;
    }

    public Set<Integer> getGenresId() {
        return genresId;
    }

    public void setGenresId(Set<Integer> genresId) {
        this.genresId = genresId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id) &&
                Objects.equals(name, film.name) &&
                Objects.equals(nameORIG, film.nameORIG);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, nameORIG);
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id + "\n" +
                ", name='" + name + '\'' + "\n" +
                ", nameORIG='" + nameORIG + '\'' + "\n" +
                ", quality='" + quality + '\'' + "\n" +
                ", uploadDate=" + uploadDate + "\n" +
                ", descriptionStory='" + descriptionStory + '\'' + "\n" +
                ", actors=" + actors + "\n" +
                ", genres=" + genres + "\n" +
                ", year='" + year + '\'' + "\n" +
                ", country='" + country + '\'' + "\n" +
                ", duration='" + duration + '\'' + "\n" +
                ", filmPageUrl='" + filmPageUrl + '\'' + "\n" +
                ", filmPosterUrl='" + filmPosterUrl + '\'' + "\n" +
                '}';
    }
}

