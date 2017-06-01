package com.autonubil.identity.apps.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
public class AppsDbConfig {

	@Bean(name="appsDb")
	public DataSource appsDb(@Autowired DataSourceFactory dataSourceFactory) {
		DataSource ds = dataSourceFactory.getDataSource("apps");
		try {
			ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}
	
}
