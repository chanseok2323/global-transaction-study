package com.chanseok.jta.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.xa")
public class XaDataSourceProperties {
    private XaDataSource first;
    private XaDataSource second;

    public XaDataSource getFirst() {
        return first;
    }

    public void setFirst(XaDataSource first) {
        this.first = first;
    }

    public XaDataSource getSecond() {
        return second;
    }

    public void setSecond(XaDataSource second) {
        this.second = second;
    }

    public static class XaDataSource {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private String uniqueResourceName;
        private Hibernate hibernate;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUniqueResourceName() {
            return uniqueResourceName;
        }

        public void setUniqueResourceName(String uniqueResourceName) {
            this.uniqueResourceName = uniqueResourceName;
        }

        public Hibernate getHibernate() {
            return hibernate;
        }

        public void setHibernate(Hibernate hibernate) {
            this.hibernate = hibernate;
        }
    }

    public static class Hibernate {
        private String ddlAuto;
        private String dialect;
        private Naming naming;

        public static Map<String, Object> jpaProperties(Hibernate hibernateProperties) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.transactionType", "JTA");

            if(hibernateProperties.getDdlAuto() != null) {
                properties.put("hibernate.hbm2ddl.auto", hibernateProperties.getDdlAuto());
            }
            if(hibernateProperties.getDialect() != null) {
                properties.put("hibernate.dialect", hibernateProperties.getDialect());
            }

            Naming hibernateNaming = hibernateProperties.getNaming();
            if(hibernateNaming != null) {
                if (hibernateNaming.getImplicitStrategy() != null) {
                    properties.put("hibernate.implicit_naming_strategy",  hibernateNaming.getImplicitStrategy());
                }
                if (hibernateNaming.getPhysicalStrategy() != null) {
                    properties.put("hibernate.physical_naming_strategy", hibernateNaming.getPhysicalStrategy());
                }
            }

            return properties;
        }

        public String getDdlAuto() {
            return ddlAuto;
        }

        public void setDdlAuto(String ddlAuto) {
            this.ddlAuto = ddlAuto;
        }

        public String getDialect() {
            return dialect;
        }

        public void setDialect(String dialect) {
            this.dialect = dialect;
        }

        public Naming getNaming() {
            return naming;
        }

        public void setNaming(Naming naming) {
            this.naming = naming;
        }
    }

    public static class Naming {
        private String physicalStrategy;
        private String implicitStrategy;

        public String getPhysicalStrategy() {
            return physicalStrategy;
        }

        public void setPhysicalStrategy(String physicalStrategy) {
            this.physicalStrategy = physicalStrategy;
        }

        public String getImplicitStrategy() {
            return implicitStrategy;
        }

        public void setImplicitStrategy(String implicitStrategy) {
            this.implicitStrategy = implicitStrategy;
        }
    }
}