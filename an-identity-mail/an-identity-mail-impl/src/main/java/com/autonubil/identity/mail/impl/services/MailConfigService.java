package com.autonubil.identity.mail.impl.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.mail.api.MailServiceFactory;
import com.autonubil.identity.mail.impl.entities.MailConfig;
import com.autonubil.identity.mail.impl.entities.MailConfig.ENCRYPTION;
import com.autonubil.identity.mail.impl.entities.MailServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Aggregation;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;
import de.disk0.db.sqlbuilder.interfaces.Update;

@Service
public class MailConfigService implements MailServiceFactory {

	private static final Log log = LogFactory.getLog(MailConfigService.class);
	
	@Autowired
	@Qualifier("mailServiceDataSource")
	private DataSource dataSource;

	@Autowired
	private MailTemplateService mailTemplateService;

	public List<MailConfig> list(String id) {
		
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("mail_config");
		if(id!=null) {
			s.where(Operator.AND, s.condition(t,"id",Comparator.EQ,id));
		}
		s.order(t, "name", true);

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.query(s.toSQL(), s.getParams(), new RowMapper<MailConfig>() {

			@Override
			public MailConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailConfig msc = new MailConfig();
				msc.setId(rs.getString("id"));
				msc.setName(rs.getString("name"));
				msc.setDescription(rs.getString("description"));
				msc.setHost(rs.getString("host"));
				msc.setPort(rs.getInt("port"));
				msc.setSender(rs.getString("sender"));
				msc.setCert(rs.getString("cert"));
				msc.setEncryption(MailConfig.ENCRYPTION.valueOf(rs.getString("encryption")));
				msc.setAuth(rs.getBoolean("auth"));
				msc.setUsername(rs.getString("username"));
				try {
					msc.setParams(new ObjectMapper().readValue(rs.getString("params"), new TypeReference<Map<String,Object>>() {}));
				} catch (Exception e) {
				}
				return msc;
			}
		});
	}
	
	
	public MailConfig get(String id) {
		List<MailConfig> out = list(id);
		if(out.size()>0) {
			return out.get(0);
		}
		return null;
	}
	
	public MailConfig save(MailConfig c) {
		MailConfig s = get(c.getId());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if(s == null) {
			Insert i = SqlBuilderFactory.insert("mail_config");
			i.addField("id",c.getId());
			i.addField("name",c.getName());
			i.addField("description",c.getDescription());
			i.addField("host",c.getHost());
			i.addField("port",c.getPort());
			i.addField("sender",c.getSender());
			i.addField("cert",c.getCert());
			i.addField("encryption",c.getEncryption().name());
			i.addField("auth",c.isAuth()?1:0);
			i.addField("username",c.getUsername());
			try {
				i.addField("params", new ObjectMapper().writeValueAsString(c.getParams()));
			} catch (Exception e) {
			}
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("mail_config");
			u.set("id",c.getId());
			u.set("name",c.getName());
			u.set("description",c.getDescription());
			u.set("host",c.getHost());
			u.set("port",c.getPort());
			u.set("sender",c.getSender());
			u.set("cert",c.getCert());
			u.set("encryption",c.getEncryption().name());
			u.set("auth",c.isAuth()?1:0);
			u.set("username",c.getUsername());
			try {
				u.set("params", new ObjectMapper().writeValueAsString(c.getParams()));
			} catch (Exception e) {
			}
			u.where(Operator.AND,u.condition(u.getTable(),"id",Comparator.EQ,c.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return get(c.getId());
	}
	
	public void delete(String id) {
		MailConfig s = get(id);
		if(s==null) {
			return;
		}
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("mail_config_password");
			d.where(Operator.AND,d.condition(d.getTable(), "mail_config_id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("mail_config");
			d.where(Operator.AND,d.condition(d.getTable(), "id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
	}

	public String getPassword(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("mail_config_password");
		s.select(Aggregation.NONE, t, "password", "password");
		s.where(s.condition(t, "mail_config_id", Comparator.EQ, id));
		List<String> pws = templ.query(s.toSQL(), s.getParams(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("password");
			}
		});
		if(pws.size()>0) {
			return pws.get(0);
		}
		return null;
	}
	
	public void setPassword(String id, String password) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		MailConfig mc = get(id);
		if(mc == null) return;
		String pw = getPassword(id);
		if(pw==null) {
			Insert i = SqlBuilderFactory.insert("mail_config_password");
			i.addField("password",password);
			i.addField("mail_config_id",id);
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("mail_config_password");
			u.set("password",password);
			u.where(Operator.AND,u.condition(u.getTable(), "mail_config_id", Comparator.EQ, id));
			templ.update(u.toSQL(), u.getParams());
		}
	}
	

	@Override
	public MailService getDefaultMailService() {
		return getMailService("default", "", "");
	}


	@Override
	public MailService getMailService(String id, String name, String description) {
		MailConfig mc = get("default");
		if(mc==null) {
			mc = new MailConfig();
			mc.setAuth(false);
			mc.setHost("127.0.0.1");
			mc.setPort(25);
			mc.setEncryption(ENCRYPTION.NONE);
			mc.setId("default");
			mc.setName(name);
			mc.setDescription(description);
			mc = save(mc);
		} 
		String password = getPassword(id);
		log.info(" ----> password: "+password+" / "+id);
		return new MailServiceImpl(mc, password, mailTemplateService);
	}
	
	@PostConstruct
	public void init() {
		MailConfig mc = get("default");
		if(mc==null) {
			mc = new MailConfig();
			mc.setAuth(false);
			mc.setHost("127.0.0.1");
			mc.setPort(25);
			mc.setEncryption(ENCRYPTION.NONE);
			mc.setId("default");
			mc.setName("Default Mail Service");
			mc = save(mc);
			save(mc);
		}
	}

	

}
