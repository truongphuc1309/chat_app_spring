package com.truongphuc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(value = "classpath:application.properties")
@ComponentScan(basePackages = { "com.truongphuc" })
@EnableJpaRepositories(basePackages = { "com.truongphuc.repository" })
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        System.out.println("\n============================= CONNECTED TO MYSQL==============================");
        entityManagerFactoryBean.setPackagesToScan("com.truongphuc.entity");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactoryBean.setJpaProperties(additionalProperties());

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public DataSource dataSource() {

        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        try {
            properties.load(inputStream);

            String driver = properties.getProperty("db.driver");
            String url = properties.getProperty("db.url");
            String userName = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            dataSource.setDriverClassName(driver);
            dataSource.setUrl(url);
            dataSource.setUsername(userName);
            dataSource.setPassword(password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataSource;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        // properties.setProperty("hibernate.hbm2ddl.auto", "none");
        return properties;
    }

}
