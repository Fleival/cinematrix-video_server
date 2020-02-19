package com.denspark.model.user;

import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.utils.text_utils.Base32;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "user_account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(unique = true, nullable = false, name = "ID")
    private Long id;

    private String firstName;

    private String lastName;

    private String gender;

    private String email;

    private String city;

    private String country;

    @Column(length = 60)
    @JsonIgnore
    private String password;
    @JsonIgnore
    private boolean enabled;
    @JsonIgnore
    private boolean isUsing2FA;
    @JsonIgnore
    private String secret;

    //

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @JsonIgnore
    private Collection<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_favorite_film",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "ID")}
    )
    private Set<Film> favoriteMovies;

    @Transient
    @JsonInclude
    private Set<Integer> favoriteMoviesId;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_favorite_tv_series",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "ID")}
    )
    private Set<Film> favoriteTvSeries;

    @Transient
    @JsonInclude
    private Set<Integer> favoriteTvSeriesId;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String username) {
        this.email = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    public Set<Film> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(Set<Film> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Set<Integer> getFavoriteMoviesId() {
        return favoriteMoviesId;
    }

    public void setFavoriteMoviesId(Set<Integer> favoriteMoviesId) {
        this.favoriteMoviesId = favoriteMoviesId;
    }

    @JsonIgnore
    public Set<Film> getFavoriteTvSeries() {
        return favoriteTvSeries;
    }

    public void setFavoriteTvSeries(Set<Film> favoriteTvSeries) {
        this.favoriteTvSeries = favoriteTvSeries;
    }

    public Set<Integer> getFavoriteTvSeriesId() {
        return favoriteTvSeriesId;
    }

    public void setFavoriteTvSeriesId(Set<Integer> favoriteTvSeriesId) {
        this.favoriteTvSeriesId = favoriteTvSeriesId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUsing2FA() {
        return isUsing2FA;
    }

    public void setUsing2FA(boolean isUsing2FA) {
        this.isUsing2FA = isUsing2FA;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder
                .append("User [id=").append(id)
                .append(", firstName=").append(firstName)
                .append(", lastName=").append(lastName)
                .append(", gender=").append(gender)
                .append(", email=").append(email)
                .append(", city=").append(city)
                .append(", country=").append(country)
                .append(", password=").append(password)
                .append(", enabled=").append(enabled)
                .append(", isUsing2FA=").append(isUsing2FA)
                .append(", secret=").append(secret)
                .append(", roles=").append(roles).append("]");
        return builder.toString();
    }

}
