package com.denspark.config;

import com.maxmind.geoip2.DatabaseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ua_parser.Parser;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class LoginNotificationConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginNotificationConfig.class);

    private static DatabaseReader reader = null;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public Parser uaParser() throws IOException {
        return new Parser();
    }

    @Bean
    public DatabaseReader databaseReader() throws IOException {
        try {
            LOGGER.info("GeoLite database: Trying to load GeoLite2-Country database...");

            Resource resource = resourceLoader.getResource("classpath:maxmind/GeoLite2-City.mmdb");
            InputStream dbAsStream = resource.getInputStream(); // <-- this is the difference

            // Initialize the reader
            reader = new DatabaseReader
                    .Builder(dbAsStream)
//                    .fileMode(Reader.FileMode.MEMORY)
                    .build();

            LOGGER.info("GeoLite database: Database was loaded successfully.");

        } catch (IOException | NullPointerException e) {
            LOGGER.error("Database reader cound not be initialized. ", e);
        }
        return reader;
    }
}