package com.denspark.health;

import com.denspark.config.CinemixServerConfiguration;
import io.dropwizard.hibernate.SessionFactoryHealthCheck;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CinemixHealthCheck extends SessionFactoryHealthCheck {


    @Autowired
    public CinemixHealthCheck(final SessionFactory sessionFactory, final CinemixServerConfiguration serverConfiguration) {

        super(sessionFactory, serverConfiguration.getDataSourceFactory().getValidationQuery());
    }
}