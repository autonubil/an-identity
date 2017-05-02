package com.autonubil.identity.db.common;

import javax.sql.DataSource;

public interface DataSourceFactory {
	
	
	public DataSource getDataSource(String schema);

}
