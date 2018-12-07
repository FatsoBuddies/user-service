package com.service.user.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = { "com.service.user" }, entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef= "transactionManager")
@EnableTransactionManagement
public class PersistanceConfig {

	/*
	 * Configure dataSource for application.
	 * @return
	 */
	@Bean
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/quickFood");
		dataSource.setUsername("root");
		dataSource.setPassword("Poc@12345");
		return dataSource;
	}

	@Bean(name="entityManagerFactory")
	public EntityManagerFactory entityManagerFactory() throws IOException {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
		entityManagerFactory.setDataSource(getDataSource());
		entityManagerFactory.setPackagesToScan(new String[] {"com.service.user.entity"});
		entityManagerFactory.setJpaProperties(getAdditionalJPAProperties());
		entityManagerFactory.afterPropertiesSet();
		return entityManagerFactory.getObject();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() throws IOException {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory());
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties getAdditionalJPAProperties() throws IOException {
		Resource resource = new ClassPathResource("jpa.properties");
		Properties jpaProperties = PropertiesLoaderUtils.loadProperties(resource);
		return jpaProperties;
	}

}
