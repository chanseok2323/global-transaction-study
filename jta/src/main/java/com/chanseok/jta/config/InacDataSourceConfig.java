package com.chanseok.jta.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(XaDataSourceProperties.class)
@EnableJpaRepositories(
        entityManagerFactoryRef = "inacEntityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackages = {"com.chanseok.jta.inac"}
)
public class InacDataSourceConfig {
    private final XaDataSourceProperties xaDataSourceProperties;

    public InacDataSourceConfig(XaDataSourceProperties xaDataSourceProperties) {
        this.xaDataSourceProperties = xaDataSourceProperties;
    }

    @Bean("inacEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean inacEntityManagerFactory() {
        return inacEntityManagerFactoryBuilder()
                    .dataSource(inacDataSource())
                    .packages("com.chanseok.jta.inac")
                    .persistenceUnit("mysql")
                    .properties(jpaProperties())
                    .jta(true)
                .build();
    }

    @Bean("inacEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder inacEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), jpaProperties(), null);
    }

    public Map<String, Object> jpaProperties() {
        return XaDataSourceProperties.Hibernate.jpaProperties(xaDataSourceProperties.getFirst().getHibernate());
    }

    @Bean("inacDataSourceProperties")
    public DataSourceProperties inacDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("inacDataSource")
    public DataSource inacDataSource() {
        MysqlXADataSource dataSource = new MysqlXADataSource();
        dataSource.setUrl(xaDataSourceProperties.getFirst().getUrl());
        dataSource.setUser(xaDataSourceProperties.getFirst().getUsername());
        dataSource.setPassword(xaDataSourceProperties.getFirst().getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName(xaDataSourceProperties.getFirst().getUniqueResourceName());
        xaDataSource.setMaxPoolSize(30);
        return xaDataSource;
    }
}
