package com.ssafy.campinity.demo.batch.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@PropertySource({"classpath:application.yml"})
@EnableJpaRepositories(
        basePackages = "com.ssafy.campinity.core.repository",
        entityManagerFactoryRef = "campinityEntityManager",
        transactionManagerRef = "campinityTransactionManager"
)
public class CampinityDataSourceConfig {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Bean
    public LocalContainerEntityManagerFactoryBean campinityEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(campinityDataSource());
        em.setPackagesToScan("com.ssafy.campinity.core.entity");
        em.setPersistenceUnitName("campinityEntityManager");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.ddl-auto", ddl);
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");
        properties.put("hibernate.physical_naming_strategy",
                "com.ssafy.campinity.core.utils.SnakeCasePhysicalNamingStrategy");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @ConfigurationProperties(prefix="spring.campinity-db.datasource")
    public DataSource campinityDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager campinityTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(campinityEntityManager().getObject());
        return transactionManager;
    }
}