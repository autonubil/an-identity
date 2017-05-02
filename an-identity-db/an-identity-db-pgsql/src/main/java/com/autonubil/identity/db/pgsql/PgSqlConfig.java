package com.autonubil.identity.db.pgsql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
@PropertySource(value={"pgsql.properties"})
public class PgSqlConfig {

	@Bean
	public DataSourceFactory mysqlDataSourceFactory(
			@Value("${pgsql.jdbc.baseurl}") String baseUrl,
			@Value("${pgsql.jdbc.username}") String username,
			@Value("${pgsql.jdbc.password}") String password
		) {
		return new PgSqlDataSourceFactory(baseUrl,username,password); 
	}
	
}
