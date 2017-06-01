package com.autonubil.identity.ovpn.common;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
public class OvpnDbConfig {
	
	@Bean(name="ovpnDb")
	public DataSource ovpnDb(@Autowired DataSourceFactory dataSourceFactory) {
		DataSource ds = dataSourceFactory.getDataSource("ovpn");
		try {
			ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}
}
