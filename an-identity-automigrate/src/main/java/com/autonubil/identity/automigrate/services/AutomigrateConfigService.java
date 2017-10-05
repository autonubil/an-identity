package com.autonubil.identity.automigrate.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.automigrate.entities.AutomigrateConfig;
import com.autonubil.identity.automigrate.entities.AutomigrateGroupMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;

@Service
public class AutomigrateConfigService {

	private static Log log = LogFactory.getLog(AutomigrateConfigService.class);
	
	@Qualifier("automigrateDataSource")
	@Autowired
	private DataSource dataSource;

	public List<AutomigrateConfig> list(String id, String fromId, String toId, int offset, int max) {
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("migrate");
		if(id!=null) {
			log.info(" ---> id "+id);
			s.where(s.condition(t,"id",Comparator.EQ,id));
		}
		if(fromId!=null) {
			log.info(" ---> fromId "+fromId);
			s.where(Operator.AND,s.condition(t,"from_ldap_id",Comparator.EQ,fromId));
		}
		if(toId!=null) {
			log.info(" ---> toId "+toId);
			s.where(Operator.AND,s.condition(t,"to_ldap_id",Comparator.EQ,toId));
		}
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		AutomigrateConfigRowCallbackHandler h = new AutomigrateConfigRowCallbackHandler(offset, max);
		templ.query(s.toSQL(),  s.getParams(), h);
		log.info(" ---> SQL "+s.toSQL());
		log.info(" ---> PRM "+s.getParams());
		return h.getConfigs();
	}

	public AutomigrateConfig get(String id) {
		List<AutomigrateConfig> l = list(id, null,null, 0, 1);
		if(l.size()>0) {
			return l.get(0);
		}
		return null;
	}

	public AutomigrateConfig save(AutomigrateConfig config) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if(config.getId()!=null) {
			delete(config.getId());
		} else {
			config.setId(UUID.randomUUID().toString());
		}
		
		try {
			log.info(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(config));
		} catch (Exception e) {
		}
		
		{
			Insert insert = SqlBuilderFactory.insert("migrate");
			insert.addField("id",config.getId());
			insert.addField("from_ldap_id",config.getFromLdap());
			insert.addField("to_ldap_id",config.getToLdap());
			templ.update(insert.toSQL(),insert.getParams());
		}
		for(AutomigrateGroupMapping m : config.getGroupMappings()) {
			if(StringUtils.isEmpty(m.getFromGroup()) && StringUtils.isEmpty(m.getToGroup())) {
				continue;
			}
			Insert insert = SqlBuilderFactory.insert("migrate_group");
			insert.addField("id", UUID.randomUUID().toString());
			insert.addField("migrate_id",config.getId());
			insert.addField("from_group",m.getFromGroup());
			insert.addField("to_group",m.getToGroup());
			templ.update(insert.toSQL(),insert.getParams());
		}
		return get(config.getId());
	}

	public void delete(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("migrate_group");
			d.where(d.condition(d.getTable(), "migrate_id", Comparator.EQ, id));
			templ.update(d.toSQL(),d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("migrate");
			d.where(d.condition(d.getTable(), "id", Comparator.EQ, id));
			templ.update(d.toSQL(),d.getParams());
		}
	}
	
	public class AutomigrateConfigRowCallbackHandler implements RowCallbackHandler {

		private List<AutomigrateConfig> configs = new ArrayList<>();
		
		private int offset, max;
		private int count = 0;
		private NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		public AutomigrateConfigRowCallbackHandler(int offset, int max) {
			this.offset = offset;
			this.max = max;
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			if(count>=offset && configs.size() < max) {
				AutomigrateConfig c = new AutomigrateConfig();
				c.setId(rs.getString("id"));
				c.setFromLdap(rs.getString("from_ldap_id"));
				c.setToLdap(rs.getString("to_ldap_id"));
				count++;
				Select s = SqlBuilderFactory.select();
				Table t = s.fromTable("migrate_group");
				s.where(s.condition(t,"migrate_id", Comparator.EQ, c.getId()));
				c.setGroupMappings(
					templ.query(
							s.toSQL(), 
							s.getParams(), 
							new RowMapper<AutomigrateGroupMapping>() {
								@Override
								public AutomigrateGroupMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
									return new AutomigrateGroupMapping(rs.getString("from_group"), rs.getString("to_group"));
								}
							}
					)
				);
				configs.add(c);
			}
		}
		
		public List<AutomigrateConfig> getConfigs() {
			return configs;
		}
	}
	
	public void log(String migrationId, String userId, boolean success, String message) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Insert insert = SqlBuilderFactory.insert("migrate_log");
		insert.addField("id", UUID.randomUUID().toString());
		insert.addField("migrate_id",migrationId);
		insert.addField("from_group","");
		insert.addField("user_id",userId);
		insert.addField("success",success);
		insert.addField("message",message);
		templ.update(insert.toSQL(),insert.getParams());
	}
	
	
	
	
}
