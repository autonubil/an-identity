package com.autonubil.identity.audit.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
public class AuditLoggerDbConfig {

	@Bean(name="audit_log")
	@Primary
	public DataSource auditLoggerDataSource(@Autowired DataSourceFactory dataSourceFactory) {
		return dataSourceFactory.getDataSource("audit_log");
	}
	
}
