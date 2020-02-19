package com.denspark.config;

import com.denspark.config.cinema.CinematrixCinemaUserConfig;
import com.denspark.springdrop.ExtraDwConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.dropwizard.sundial.SundialConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public class CinemixServerConfiguration extends ExtraDwConfiguration {
    @NotEmpty
    private String config_PATH;

    @NotEmpty
    private String xlink_WORKING_FOLDER;

    @NotEmpty
    private String smtp_host;

    @NotEmpty
    private String smtp_port;

    @NotEmpty
    private String mail_username;

    @NotEmpty
    private String mail_password;

    @NotEmpty
    private String mail_email_from;

    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    @NotNull
    private Map<String, String> hibernateProperties = Collections.emptyMap();


    @Valid
    @NotNull
    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @Valid
    @NotNull
    public CinematrixCinemaUserConfig cinemixConfig = new CinematrixCinemaUserConfig();


    @JsonProperty
    public String getConfig_PATH() {
        return config_PATH;
    }


    @JsonProperty
    public String getXlink_WORKING_FOLDER() {
        return xlink_WORKING_FOLDER;
    }

    @JsonProperty
    public String getSmtp_host() {
        return smtp_host;
    }

    @JsonProperty
    public String getSmtp_port() {
        return smtp_port;
    }

    @JsonProperty
    public String getMail_username() {
        return mail_username;
    }

    @JsonProperty
    public String getMail_password() {
        return mail_password;
    }

    @JsonProperty
    public String getMail_email_from() {
        return mail_email_from;
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
