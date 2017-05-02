package com.autonubil.identity.db.mysql;


import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;

import com.autonubil.identity.db.common.DataSourceFactory;

public class MysqlDataSourceFactory implements DataSourceFactory {

	private static Log log = LogFactory.getLog(MysqlDataSourceFactory.class);
	private String baseUrl, username, password;

	public MysqlDataSourceFactory(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	@Override
	public DataSource getDataSource(String schema) {
		BasicDataSource bds = new  BasicDataSource();
		String url = baseUrl+"/"+schema;
		log.info("creating datasource for: "+url+", username: "+username);
		bds.setUrl(url);
		bds.setUsername(username);
		bds.setPassword(password);
		bds.setMaxTotal(100);
		bds.setDriverClassName("com.mysql.jdbc.Driver");

		Flyway flyway = new Flyway();
		flyway.setDataSource(bds);
		flyway.setLocations("db/migration_"+schema);
		flyway.migrate();
		
		return bds;
	}

}
