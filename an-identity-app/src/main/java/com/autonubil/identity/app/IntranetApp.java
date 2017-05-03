package com.autonubil.identity.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@PropertySource(value = { "an.identity.properties" })
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class, org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration.class, org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class})
@EnableConfigurationProperties
@ComponentScan(basePackages={"com","de", "net", "org.mitre"})
public class IntranetApp {

	
	public static void main(String[] args) {
		SpringApplication.run(IntranetApp.class, args);
	}
	
}
