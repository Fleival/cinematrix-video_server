package com.denspark;

import com.denspark.config.CinematrixVideoConfiguration;
import com.denspark.config.CinematrixVideoSpringConfiguration;
import com.denspark.mod.spring.context.SpringContextBuilder;
import com.denspark.resources.FilmixApiResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.SessionFactoryHealthCheck;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class CinematrixVideoApplication extends Application<CinematrixVideoConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(CinematrixVideoApplication.class);
    Boolean isStarted = false;
    Environment environment;

    public static void main(final String[] args) throws Exception {
        new CinematrixVideoApplication().run(args);
    }

    @Override
    public String getName() {
        return "CinematrixVideo";
    }

    @Override
    public void initialize(final Bootstrap<CinematrixVideoConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<CinematrixVideoConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(CinematrixVideoConfiguration cinematrixVideoConfiguration) {
                return cinematrixVideoConfiguration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new SundialBundle<CinematrixVideoConfiguration>() {

            @Override
            public SundialConfiguration getSundialConfiguration(CinematrixVideoConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });
    }

    @Override
    public void run(final CinematrixVideoConfiguration configuration,
                    final Environment environment) {
        this.environment = environment;
        DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
        ManagedDataSource dataSource = dataSourceFactory.build(environment.metrics(), "dataSource");

        ApplicationContext context = new SpringContextBuilder()
                .addParentContextBean("dataSource", dataSource)
                .addParentContextBean("configuration", configuration)
                .addAnnotationConfiguration(CinematrixVideoSpringConfiguration.class)
                .build();

        registerResources(environment, context, configuration);
        registerHealthChecks(environment, configuration, context);

        environment.lifecycle().addServerLifecycleListener(
                server -> {
                    logger.info("ServerStarted");
                    isStarted = true;

                }
        );
    }

    private void registerResources(Environment environment, ApplicationContext context, CinematrixVideoConfiguration configuration) {

        FilmixApiResource filmixApiResource = new FilmixApiResource(context, configuration);
        environment.jersey().register(filmixApiResource);
        environment.getApplicationContext().setAttribute("configuration", configuration);
        environment.getApplicationContext().setAttribute("context", context);

    }

    private void registerHealthChecks(Environment environment, CinematrixVideoConfiguration configuration, ApplicationContext context) {
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
        }else {
            logger.info("Server not started");
        }
    }
}
