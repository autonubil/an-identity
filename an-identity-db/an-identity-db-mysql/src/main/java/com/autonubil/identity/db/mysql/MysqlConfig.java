package com.autonubil.identity.db.mysql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
@PropertySource(value={"mysql.properties"})
public class MysqlConfig {

	@Bean
	public DataSourceFactory mysqlDataSourceFactory(
			@Value("${mysql.jdbc.baseurl}") String baseUrl,
			@Value("${mysql.jdbc.username}") String username,
			@Value("${mysql.jdbc.password}") String password
		) {
		return new MysqlDataSourceFactory(baseUrl,username,password); 
	}
	
}
