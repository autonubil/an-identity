package com.autonubil.identity.mail.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
public class MailServiceDbConfig {

	@Bean(name="mail")
	public DataSource mailServiceDataSource(@Autowired DataSourceFactory dataSourceFactory) {
		DataSource ds = dataSourceFactory.getDataSource("mail");
		try {
			ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}
	
}
