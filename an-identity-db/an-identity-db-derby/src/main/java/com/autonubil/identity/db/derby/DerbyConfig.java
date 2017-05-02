package com.autonubil.identity.db.derby;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.autonubil.identity.db.common.DataSourceFactory;

@Configuration
@PropertySource(value = { "derby.properties" })
public class DerbyConfig {

	public static final Log log = LogFactory.getLog(DerbyConfig.class);

	@Bean
	public DataSourceFactory dataSourceFactory(@Value("${database.derby.datadir}") String baseDir) {
		return new DerbyDataSourceFactory(baseDir);
	}

}
