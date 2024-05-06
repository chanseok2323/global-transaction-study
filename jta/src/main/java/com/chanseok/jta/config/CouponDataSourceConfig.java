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
        entityManagerFactoryRef = "couponEntityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackages = {"com.chanseok.jta.coupon"}
)
public class CouponDataSourceConfig {
    private final XaDataSourceProperties xaDataSourceProperties;

    public CouponDataSourceConfig(XaDataSourceProperties xaDataSourceProperties) {
        this.xaDataSourceProperties = xaDataSourceProperties;
    }

    @Bean("couponEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean couponEntityManagerFactory() {
        return couponEntityManagerFactoryBuilder()
                    .dataSource(couponDataSource())
                    .packages("com.chanseok.jta.coupon")
                    .persistenceUnit("mysql")
                    .properties(jpaProperties())
                    .jta(true)
                .build();
    }

    @Bean("couponEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder couponEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), jpaProperties(), null);
    }

    public Map<String, Object> jpaProperties() {
        return XaDataSourceProperties.Hibernate.jpaProperties(xaDataSourceProperties.getSecond().getHibernate());
    }

    @Bean("couponDataSourceProperties")
    public DataSourceProperties couponDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("couponDataSource")
    public DataSource couponDataSource() {
        MysqlXADataSource dataSource = new MysqlXADataSource();
        dataSource.setUrl(xaDataSourceProperties.getSecond().getUrl());
        dataSource.setUser(xaDataSourceProperties.getSecond().getUsername());
        dataSource.setPassword(xaDataSourceProperties.getSecond().getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName(xaDataSourceProperties.getSecond().getUniqueResourceName());
        xaDataSource.setMaxPoolSize(30);
        return xaDataSource;
    }
}
