package com.denspark.config;


import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.model.user.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan({
        "com.denspark.db.abstract_dao",
        "com.denspark.db.filmix_dao",
        "com.denspark.db.cinematrix_dao",
        "com.denspark.db.service",
        "com.denspark.client.registration"
})
@EnableJpaRepositories(basePackages = "com.denspark.db.cinemix.persistance")
public class CinematrixServerSpringConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DataSource dataSource;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CinematrixServerConfiguration serverConfiguration;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(
                packageFor(Film.class),
                packageFor(User.class)
        );

        sessionFactory.setPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl());
        sessionFactory.setHibernateProperties(hibernateDWProperties());
        return sessionFactory;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(
                packageFor(Film.class),
                packageFor(User.class)
        );

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateDWProperties());
        return em;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateDWProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", MySQL5Dialect.class.getName());

        properties.putAll(serverConfiguration.getHibernateProperties());

        return properties;

    }

    private String packageFor(Class<?> clazz) {
        return clazz.getPackage().getName();
    }
}
