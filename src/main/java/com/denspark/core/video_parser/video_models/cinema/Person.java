package com.denspark.core.video_parser.video_models.cinema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "persons")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Person.findAll",
                        query = "SELECT p FROM Person p"
                ),
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Person.findByName",
                        query = "SELECT p FROM Person p"
                                + " where p.name like :name "
                ),
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Person.getAllId",
                        query = "SELECT p.id FROM Person p"
                )

        })

//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "film")
public class Person implements FilmixCommonEntity, CinemaEntity, com.denspark.core.video_parser.model.Person {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "name_RUS")
    private String name;
    @Column(name = "name_ENG")
    private String originName;
    @Column(name = "url_photo")
    private String photoUrl;
    @Column(name = "url_page")
    private String pageUrl;
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "birthplace")
    private String birthplace;
    @Column(name = "height")
    private String height;
    @Column(name = "bio")
    @Type(type = "text")
    private String bio;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "persons_genres",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id", referencedColumnName = "ID")}
    )
    private Set<Genre> genresOfFilms = new LinkedHashSet<>(0);

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "actors"
    )
    private Set<Film> filmsParticipation;

    @JsonInclude()
    @Transient
    private Set<Integer> filmsParticipationId;

    @JsonInclude()
    @Transient
    private Set<Integer> filmsGenresId;



    public Person() {
    }
    public Person(String name) {
        this.name = name;
    }

    public Person(String name, String originName, String photoUrl, String pageUrl, Date dateOfBirth, String birthplace, String height) {
        this.name = name;
        this.originName = originName;
        this.photoUrl = photoUrl;
        this.pageUrl = pageUrl;
        this.dateOfBirth = dateOfBirth;
        this.birthplace = birthplace;
        this.height = height;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @JsonIgnore
    public Set<Genre> getGenresOfFilms() {
        return genresOfFilms;
    }

    public void setGenresOfFilms(Set<Genre> genresOfFilms) {
        this.genresOfFilms = genresOfFilms;
    }

    public Set<Integer> getFilmsParticipationId() {
        return filmsParticipationId;
    }

    public void setFilmsParticipationId(Set<Integer> filmsParticipationId) {
        this.filmsParticipationId = filmsParticipationId;
    }

    @JsonIgnore
    public Set<Film> getFilmsParticipation() {
        return filmsParticipation;
    }

    public void setFilmsParticipation(Set<Film> filmsParticipation) {
        this.filmsParticipation = filmsParticipation;
    }

    public Set<Integer> getFilmsGenresId() {
        return filmsGenresId;
    }

    public void setFilmsGenresId(Set<Integer> filmsGenresId) {
        this.filmsGenresId = filmsGenresId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(name, person.name);
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Person person = (Person) o;
//        return Objects.equals(id, person.id);
//    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }


    // TODO: 19.06.2018 : toString()
    @Override
    public String toString() {
        String s = "Person: \n" +
                "name: " + getName() + "\n" +
                "name ENG: " + getOriginName() + "\n" +
                "photo URL: " + getPhotoUrl() + "\n" +
                "page URL: " + getPageUrl() + "\n" +
                "Birth place: " + getBirthplace() + "\n" +
                "Birth date: " + getDateOfBirth() + "\n" +
                "height: " + getHeight() + "\n" +
                "bio: " + getBio() + "\n" +
                "Career: " + "\n";

        s = s + "Genres: " + "\n";
        for (Genre g : getGenresOfFilms()) {
            s = s + "    " + g.getName() + "\n";
        }

        return s;
    }
    @JsonIgnore
    public StringBuilder getXmlStringBuilder() {
        String xTag = "<xperson>";
        String xTagClose = "</xperson>";
        String idTag = "<id>";
        String idTagClose = "</id>";
        String nameTag = "<name>";
        String nameTagClose = "</name>";
        StringBuilder sb = new StringBuilder("\n");
        sb
                .append("\t").append(xTag).append("\n")
                .append("\t\t").append(idTag).append(getId()).append(idTagClose).append("\n")
                .append("\t\t").append(nameTag).append(getName()).append(nameTagClose).append("\n")
                .append("\t").append(xTagClose);
        return sb;
    }
    @JsonIgnore
    public String getXml() {
        return getXmlStringBuilder().toString();
    }
}
