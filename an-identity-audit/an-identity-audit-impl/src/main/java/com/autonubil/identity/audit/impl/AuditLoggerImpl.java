package com.autonubil.identity.audit.impl;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.audit.api.AuditLoggerHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.interfaces.Insert;

@Service
class AuditLoggerImpl implements AuditLogger {
	
	@Autowired
	@Qualifier("auditLoggerDataSource")
	private DataSource dataSource;

	@PostConstruct
	public void init() {
		AuditLoggerHelper.setInstance(this);
	}
	
	@Override
	public void log(String component, String type, String sessionId, String remote, String identity, String action) {
		Insert i = SqlBuilderFactory.insert("audit_log");
		i.addField("id", UUID.randomUUID().toString());
		i.addField("date", System.currentTimeMillis());
		i.addField("component", component);
		i.addField("type", type);
		i.addField("remote", remote);
		i.addField("session_id", sessionId);
		i.addField("user", identity==null?"":identity);
		i.addField("action", action);
		NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(dataSource);
		t.update(i.toSQL(), i.getParams());
	}

	
}
