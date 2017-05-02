package com.autonubil.identity.db.derby;

import java.io.File;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.flywaydb.core.Flyway;

import com.autonubil.identity.db.common.DataSourceFactory;

public class DerbyDataSourceFactory implements DataSourceFactory {

	private static Log log = LogFactory.getLog(DerbyDataSourceFactory.class);
	
	private String baseDir;
	
	public DerbyDataSourceFactory(String baseDir) {
		this.baseDir = baseDir;
	}
	
	@Override
	public DataSource getDataSource(String schema) {
		
		log.info("initializing DERBY db with baseDir: "+baseDir+" and schema: "+schema);
		
		File f = new File(baseDir);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		EmbeddedConnectionPoolDataSource ds = new EmbeddedConnectionPoolDataSource();
		
		log.info("initializing DERBY db with path: "+(f.getAbsolutePath()+"/intranet"));
		ds.setDatabaseName(f.getAbsolutePath()+"/"+schema);
		ds.setUser(schema);
		ds.setPassword(schema);
		ds.setConnectionAttributes("create=true");

		Flyway flyway = new Flyway();
		flyway.setDataSource(ds);
		flyway.setLocations("db/migration_"+schema);
		flyway.migrate();
		
		return ds;
	}

}
