package com.autonubil.identity.db.pgsql;


import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;

import com.autonubil.identity.db.common.DataSourceFactory;

public class PgSqlDataSourceFactory implements DataSourceFactory {

	private static Log log = LogFactory.getLog(PgSqlDataSourceFactory.class);
	private String baseUrl, username, password;
	

	public PgSqlDataSourceFactory(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	@Override
	public DataSource getDataSource(String schema) {
		BasicDataSource bds = new  BasicDataSource();
		String url = baseUrl+"?currentSchema="+schema;
		log.info("creating datasource for: "+url+", username: "+username);
		bds.setUrl(url);
		if(username!=null && username.length()>0) {
			bds.setUsername(username);
		}
		if(password!=null && password.length()>0) {
			bds.setPassword(password);
		}
		bds.setMaxTotal(100);
		bds.setDriverClassName("org.postgresql.Driver");
		
		log.info("doing migration for: "+schema);
		Flyway flyway = new Flyway();
		flyway.setDataSource(bds);
		flyway.setSchemas(schema);
		// flyway.setTable("flyway_"+schema);
		flyway.setLocations("db/migration_"+schema);
		flyway.setBaselineOnMigrate(true);
		// flyway.setCleanDisabled(true);
		flyway.migrate();
		
		return bds;
	}

}
