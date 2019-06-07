package com.denspark;


import com.denspark.core.video_parser.video_models.cinema.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan({
        "com.denspark.db.abstract_dao",
        "com.denspark.db.filmix_dao",
        "com.denspark.db.service"
})
public class CinematrixVideoSpringConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CinematrixVideoConfiguration configuration;

    @Bean
    public LocalSessionFactoryBean sessionFactory(){
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(packageFor(Film.class));
//        sessionFactory.setPackagesToScan(packageFor(Genre.class));
//        sessionFactory.setPackagesToScan(packageFor(Role.class));
//        sessionFactory.setPackagesToScan(packageFor(Person.class));
//
//        sessionFactory.setPackagesToScan(packageFor(SourceAUrl.class));
//        sessionFactory.setPackagesToScan(packageFor(ActorA.class));
//        sessionFactory.setPackagesToScan(packageFor(GenreA.class));
//        sessionFactory.setPackagesToScan(packageFor(SourceA.class));
//        sessionFactory.setPackagesToScan(packageFor(VideoA.class));

        sessionFactory.setPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl());
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }


    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory){
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", MySQL5Dialect.class.getName());
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", "true");
        properties.setProperty("hibernate.use_identifier_rollback", "true");
//        properties.setProperty("hibernate.current_session_context_class", "thread");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        return properties;

    }

    private String packageFor(Class<?> clazz){
        return clazz.getPackage().getName();
    }
}
