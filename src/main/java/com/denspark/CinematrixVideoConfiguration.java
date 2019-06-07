package com.denspark;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CinematrixVideoConfiguration extends Configuration {
    @NotEmpty
    private String config_PATH;
    @NotEmpty
    private String xlink_WORKING_FOLDER;



    private DataSourceFactory dataSourceFactory;

    @Valid
    @NotNull
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @JsonProperty
    public String getConfig_PATH() {
        return config_PATH;
    }

    @JsonProperty
    public void setConfig_PATH(String config_PATH) {
        this.config_PATH = config_PATH;
    }

    @JsonProperty
    public String getXlink_WORKING_FOLDER() {
        return xlink_WORKING_FOLDER;
    }

    @JsonProperty
    public void setXlink_WORKING_FOLDER(String xlink_WORKING_FOLDER) {
        this.xlink_WORKING_FOLDER = xlink_WORKING_FOLDER;
    }
}
