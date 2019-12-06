package com.denspark.config;

import com.denspark.config.cinema.CinematrixCinemaUserConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.knowm.dropwizard.sundial.SundialConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public class CinematrixServerConfiguration extends Configuration {
    @NotEmpty
    private String config_PATH;
    @NotEmpty
    private String xlink_WORKING_FOLDER;


    private DataSourceFactory dataSourceFactory;

    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    @NotNull
    private Map<String, String> hibernateProperties = Collections.emptyMap();

    @Valid
    @NotNull
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @Valid
    @NotNull
    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @Valid
    @NotNull
    public CinematrixCinemaUserConfig cinemixConfig = new CinematrixCinemaUserConfig();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @JsonProperty
    public String getConfig_PATH() {
        return config_PATH;
    }


    @JsonProperty
    public String getXlink_WORKING_FOLDER() {
        return xlink_WORKING_FOLDER;
    }

    @JsonProperty("sundial")
    public SundialConfiguration getSundialConfiguration() {
        return sundialConfiguration;
    }

    @JsonProperty("viewRendererConfiguration")
    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }


    @JsonProperty("hibernateProperties")
    public Map<String, String> getHibernateProperties() {
        return hibernateProperties;
    }

    @JsonProperty("cinemixConfig")
    public CinematrixCinemaUserConfig getCinematrixCinemaUserConfig() {
        return cinemixConfig;
    }
}
