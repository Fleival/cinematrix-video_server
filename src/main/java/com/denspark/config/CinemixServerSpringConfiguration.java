package com.denspark.config;


import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.model.user.User;
import com.denspark.security.MyUserDetailsService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan({
        "com.denspark.resources",
        "com.denspark.security",
        "com.denspark.health",
        "com.denspark.db",
        "com.denspark.db.service",
        "com.denspark.client.registration",
        "com.denspark.config.startup_event"
})

@EnableJpaRepositories(
        basePackages = "com.denspark.db.cinemix.persistance",
        entityManagerFactoryRef = "jpaEntityManagerFactory",
        transactionManagerRef = "transactionEmManager"
)
public class CinemixServerSpringConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DataSource dataSource;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CinemixServerConfiguration serverConfiguration;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(
                packageFor(User.class),
                packageFor(Film.class)
        );

        sessionFactory.setPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl());
        sessionFactory.setHibernateProperties(hibernateDWProperties());
        return sessionFactory;
    }

    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(
                packageFor(User.class),
                packageFor(Film.class)
        );

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateDWProperties());
        return em;
    }

    @Bean(name = "transactionSfManager")
    @Autowired
    public HibernateTransactionManager transactionSfManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean(name = "transactionEmManager")
    @Autowired
    public PlatformTransactionManager transactionEmManager(EntityManagerFactory jpaEntityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(jpaEntityManagerFactory);
        tm.setPersistenceUnitName("transactionEmManager");
        return tm;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    @Scope("singleton")
    public Model provideModel() {
        return new ExtendedModelMap();
    }

    private Properties hibernateDWProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", MySQL5Dialect.class.getName());

        properties.putAll(serverConfiguration.getHibernateProperties());

        return properties;

    }

    /////////////OTHER SPRING CONFIGURATION/////////////

//    @Autowired
//    private MessageSource messageSource;

    @Bean
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new MyUserDetailsService();
    }

    private String packageFor(Class<?> clazz) {
        return clazz.getPackage().getName();
    }
}
