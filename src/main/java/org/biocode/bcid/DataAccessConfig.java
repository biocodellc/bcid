package org.biocode.bcid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author rjewing
 */
@Configuration
@ComponentScan(basePackages = {"org.biocode.bcid.repositories"})
@PropertySource("classpath:bcid-database.properties")
@EnableTransactionManagement
public class DataAccessConfig {
    @Autowired
    private Environment env;


    @Bean
    public DataSourceTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcTemplate() {
        return new NamedParameterJdbcTemplate(txManager().getDataSource());
    }

    @Bean
    public EzidQueueSql ezidQueueSql() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("ezid-queue-sql.yml"));
        return new EzidQueueSql(yaml.getObject());
    }

    @Bean
    public ClientSql clientSql() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("client-repository-sql.yml"));
        return new ClientSql(yaml.getObject());
    }

    private DataSource dataSource() {
        DriverManagerDataSource d = new DriverManagerDataSource(env.getProperty("bcidUrl"), env.getProperty("bcidUser"),
                env.getProperty("bcidPassword"));

        d.setDriverClassName("org.postgresql.Driver");

        return d;
    }
}
