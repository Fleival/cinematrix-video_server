package com.denspark.core.video_parser.video_models.cinema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "positive_r")
    private Integer posRating;
    @Column(name = "negative_r")
    private Integer negRating;
    public Rating(){}

    public Rating(Integer id, Integer posRating, Integer negRating) {
        this.id = id;
        this.posRating = posRating;
        this.negRating = negRating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosRating() {
        return posRating;
    }

    public void setPosRating(Integer posRating) {
        this.posRating = posRating;
    }

    public Integer getNegRating() {
        return negRating;
    }

    public void setNegRating(Integer negRating) {
        this.negRating = negRating;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return getId().equals(rating.getId()) &&
                Objects.equals(getPosRating(), rating.getPosRating()) &&
                Objects.equals(getNegRating(), rating.getNegRating());
    }

    @Override public int hashCode() {
        return Objects.hash(getId(), getPosRating(), getNegRating());
    }
}
