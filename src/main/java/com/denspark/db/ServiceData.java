package com.denspark.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceData {

    @JsonProperty
    private int maxId;
    @JsonProperty
    private long moviesCount;

    public ServiceData(int maxId, long moviesCount) {
        this.maxId = maxId;
        this.moviesCount = moviesCount;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public long getMoviesCount() {
        return moviesCount;
    }

    public void setMoviesCount(long moviesCount) {
        this.moviesCount = moviesCount;
    }
}
