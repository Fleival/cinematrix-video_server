package com.denspark.core.video_parser.video_models.cinema;



import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "genres")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Genre.findAll",
                        query = "SELECT g FROM Genre g"
                ),
                @NamedQuery(
                        name = "com.denspark.core.video_parser.video_models.cinema.Genre.findByName",
                        query = "SELECT g FROM Genre g"
                                + " where g.name like :name "
                )
        }
)
public class Genre implements FilmixCommonEntity, CinemaEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "genre_name")
    private String name;

    public Genre() {
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(String name) {
        this.name = name;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return getId().equals(genre.getId()) &&
                getName().equals(genre.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Id= " + this.getId() + " name= " + this.getName();
    }
}
