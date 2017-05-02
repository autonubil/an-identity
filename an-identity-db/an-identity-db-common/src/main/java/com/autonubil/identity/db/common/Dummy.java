package com.autonubil.identity.db.common;

import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.jooq.SQLDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class Dummy {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SQLDialect dialect;
	
	@PostConstruct
	public void init() throws SQLException {
		JdbcTemplate t = new JdbcTemplate(dataSource);
		t.execute("insert into admin_users (id,username) values ('"+UUID.randomUUID().toString()+"','hund')");
	}
	
	
}
