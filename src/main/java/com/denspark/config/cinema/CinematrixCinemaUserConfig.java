package com.denspark.config.cinema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CinematrixCinemaUserConfig {

    @JsonProperty("films_genres_exclude")
    private List<String> filmGenreExclude;

    @JsonProperty("tv_series_genres_exclude")
    private List<String> tvSeriesGenreExclude;

    @JsonProperty("tv_series_genre")
    private List<String> tvSeriesGenre;


    public List<String> getFilmGenreExclude() {
        return filmGenreExclude;
    }

    public void setFilmGenreExclude(List<String> filmGenreExclude) {
        this.filmGenreExclude = filmGenreExclude;
    }

    public List<String> getTvSeriesGenreExclude() {
        return tvSeriesGenreExclude;
    }

    public void setTvSeriesGenreExclude(List<String> tvSeriesGenreExclude) {
        this.tvSeriesGenreExclude = tvSeriesGenreExclude;
    }

    public List<String> getTvSeriesGenre() {
        return tvSeriesGenre;
    }

    public void setTvSeriesGenre(List<String> tvSeriesGenre) {
        this.tvSeriesGenre = tvSeriesGenre;
    }
}
