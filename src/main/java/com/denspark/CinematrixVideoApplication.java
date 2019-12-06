package com.denspark;


import com.codahale.metrics.MetricRegistry;
import com.denspark.config.CinematrixServerConfiguration;
import com.denspark.config.CinematrixServerSpringConfiguration;
import com.denspark.resources.*;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.sparkbrains.dropwizard.ParameterNameProvider;
import com.sparkbrains.dropwizard.SpringBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.SessionFactoryHealthCheck;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.validation.BaseValidator;
import io.dropwizard.views.ViewBundle;
import org.hibernate.SessionFactory;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;


public class CinematrixVideoApplication extends Application<CinematrixServerConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(CinematrixVideoApplication.class);
    private Boolean isStarted = false;
    private Environment environment;

    private static final MetricRegistry metrics = new MetricRegistry();

    private SpringBundle<CinematrixServerConfiguration> springBundle;

    public static void main(final String[] args) throws Exception {
        new CinematrixVideoApplication().run(args);
    }

    @Override
    public String getName() {
        return "CinematrixVideo";
    }

    public SpringBundle<CinematrixServerConfiguration> getSpringBundle(Class<?>... annotatedClasses) {
        if (annotatedClasses != null) {
            springBundle = new SpringBundle<CinematrixServerConfiguration>(getName(), annotatedClasses) {
                @Override
                public ManagedDataSource getDataSource(CinematrixServerConfiguration configuration) {
                    return configuration.getDataSourceFactory().build(metrics, "dataSource");
                }
            };
        } else if (getClass().isAnnotationPresent(org.springframework.context.annotation.Configuration.class)) {
            springBundle = new SpringBundle<CinematrixServerConfiguration>(getName(), getClass()) {
                @Override
                public ManagedDataSource getDataSource(CinematrixServerConfiguration configuration) {
                    return configuration.getDataSourceFactory().build(metrics, "dataSource");
                }
            };
        } else {
            springBundle = new SpringBundle<CinematrixServerConfiguration>(getName()) {
                @Override
                public ManagedDataSource getDataSource(CinematrixServerConfiguration configuration) {
                    return configuration.getDataSourceFactory().build(metrics, "dataSource");
                }
            };
        }
        return springBundle;
    }

    @Override
    public void initialize(final Bootstrap<CinematrixServerConfiguration> bootstrap) {
        initObjectMapper(bootstrap);
        initBeanValidation(bootstrap);

        bootstrap.addBundle(new MigrationsBundle<CinematrixServerConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(CinematrixServerConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new SundialBundle<CinematrixServerConfiguration>() {

            @Override
            public SundialConfiguration getSundialConfiguration(CinematrixServerConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });

        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new AssetsBundle("/static/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/static/fonts", "/fonts", null, "fonts"));
        bootstrap.addBundle(new AssetsBundle("/static/static/img", "/static/img", null, "images"));
        bootstrap.addBundle(new AssetsBundle("/static/js", "/js", null, "js"));

        bootstrap.addBundle(new ViewBundle<CinematrixServerConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(CinematrixServerConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });

        bootstrap.addBundle(getSpringBundle(CinematrixServerSpringConfiguration.class));
    }

    @Override
    public void run(final CinematrixServerConfiguration configuration,
                    final Environment environment) {

        this.environment = environment;


        ApplicationContext context = springBundle.getContext();

        registerResources(environment, context, configuration);
        registerHealthChecks(environment, configuration, context);

        environment.lifecycle().addServerLifecycleListener(
                server -> {
                    logger.info("ServerStarted");
                    isStarted = true;
                }
        );

    }

    private void registerResources(Environment environment, ApplicationContext context, CinematrixServerConfiguration configuration) {

        FilmixApiResource filmixApiResource = new FilmixApiResource(context, configuration);
        environment.jersey().register(filmixApiResource);
        environment.jersey().register(new UserResource(context, configuration));
        environment.jersey().register(new RegistrationController(context, configuration));
        environment.jersey().register(new CinemixResource(context));
        environment.jersey().register(new RegistrationResource());


        environment.getApplicationContext().setAttribute("configuration", configuration);
        environment.getApplicationContext().setAttribute("context", context);

    }

    private void registerHealthChecks(Environment environment, CinematrixServerConfiguration configuration, ApplicationContext context) {
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);
        SessionFactoryHealthCheck sessionFactoryHealthCheck = new SessionFactoryHealthCheck(sessionFactory,
                configuration.getDataSourceFactory().getValidationQuery());
        environment.healthChecks().register("sessionFactoryHealthCheck", sessionFactoryHealthCheck);
    }

    public void stopServer() throws Exception {
        if (isStarted) {
            environment.getApplicationContext().getServer().stop();
            logger.info("Server stopped");
            isStarted = false;
        } else {
            logger.info("Server not started");
        }
    }

    private void initBeanValidation(final Bootstrap<CinematrixServerConfiguration> bootstrap) {
        bootstrap.setValidatorFactory(BaseValidator.newConfiguration()
                .parameterNameProvider(new ParameterNameProvider())
                .buildValidatorFactory());

    }

    private void initObjectMapper(final Bootstrap<CinematrixServerConfiguration> bootstrap) {
        bootstrap.setObjectMapper(Jackson.newObjectMapper()
                .enable(INDENT_OUTPUT)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .setDateFormat(new ISO8601DateFormat())
                .setSerializationInclusion(NON_ABSENT));
    }

}
