package com.autonubil.identity.openid;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
public class OpenIdDbConfig {
	public class OvpnDbConfig {
		
		@Bean(name="openidDb")
		public DataSource ovpnDb(@Autowired DataSourceFactory dataSourceFactory) {
			DataSource ds = dataSourceFactory.getDataSource("openid");
			try {
				ds.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return ds;
		}
	}

}
