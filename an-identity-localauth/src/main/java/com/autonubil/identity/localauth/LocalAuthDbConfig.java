package com.autonubil.identity.localauth;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
public class LocalAuthDbConfig {

	@Bean(name="localauth")
	public DataSource localAuthDataSource(@Autowired DataSourceFactory dataSourceFactory) {
		DataSource ds = dataSourceFactory.getDataSource("localauth");
		try {
			ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}
	
}
