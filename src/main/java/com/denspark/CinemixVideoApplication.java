package com.denspark;


import com.denspark.config.*;
import com.denspark.springdrop.AbstractSpringApplication;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.util.Map;


public class CinemixVideoApplication extends AbstractSpringApplication<CinemixServerConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(CinemixVideoApplication.class);

    public CinemixVideoApplication(Class<?>... annotatedClasses) {
        super(annotatedClasses);
    }

    @Override
    public void onInitialize(Bootstrap bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<CinemixServerConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(CinemixServerConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new SundialBundle<CinemixServerConfiguration>() {
            @Override
            public SundialConfiguration getSundialConfiguration(CinemixServerConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });
        logger.info("SundialBundle added");
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new AssetsBundle("/static/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/static/fonts", "/fonts", null, "fonts"));
        bootstrap.addBundle(new AssetsBundle("/static/static/img", "/static/img", null, "images"));
        bootstrap.addBundle(new AssetsBundle("/static/js", "/js", null, "js"));
        logger.info("AssetsBundle added");
        bootstrap.addBundle(new ViewBundle<CinemixServerConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(CinemixServerConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
        logger.info("ViewBundle added");
    }

    @Override public void configure(Configuration configuration, Environment environment, ConfigurableWebApplicationContext context) {
        environment.getApplicationContext().setAttribute("configuration", configuration);
        environment.getApplicationContext().setAttribute("context", context);

    }

    public static void main(final String[] args) throws Exception {
        new CinemixVideoApplication(
                AppConfig.class,
                LoginNotificationConfig.class,
                CinemixServerSpringConfiguration.class,
                MyRequestContextListener.class,
                CinemixSpringSecurityConfig.class).run(args);
    }


}
