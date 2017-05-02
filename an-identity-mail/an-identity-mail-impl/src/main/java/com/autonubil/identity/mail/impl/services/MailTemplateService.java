package com.autonubil.identity.mail.impl.services;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.mail.impl.entities.Mail;
import com.autonubil.identity.mail.impl.entities.MailTemplate;
import com.autonubil.identity.mail.impl.services.templating.Renderer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;
import de.disk0.db.sqlbuilder.interfaces.Update;

@Service
public class MailTemplateService {
	
	private static Log log = LogFactory.getLog(MailTemplateService.class);
	
	@Autowired
	@Qualifier("mailServiceDataSource")
	private DataSource dataSource;
	
	@Autowired
	private MailConfigService mailConfigService;

	public List<MailTemplate> list(String id, String module, String name, String locale, String search, int offset, int max) {
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("mail_template");
		if(id!=null) {
			s.where(Operator.AND, s.condition(t,"id",Comparator.EQ,id));
		}
		if(module!=null && module.length() > 0) {
			s.where(Operator.AND, s.condition(t,"module",Comparator.EQ,module));
		}
		if(name!=null && name.length() > 0) {
			s.where(Operator.AND, s.condition(t,"name",Comparator.EQ,name));
		}
		if(locale!=null && locale.length() > 0) {
			s.where(Operator.AND, s.condition(t,"locale",Comparator.EQ,locale));
		}
		if(search!=null) {
			s.where(Operator.AND, s.condition(t,"name",Comparator.LIKE,search+"%"));
		}
		
		s.order(t, "module", true);
		s.order(t, "name", true);
		s.order(t, "locale", true);
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.query(s.toSQL(), s.getParams(), new RowMapper<MailTemplate>() {

			@Override
			public MailTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailTemplate mt = new MailTemplate();
				mt.setId(rs.getString("id"));
				mt.setModule(rs.getString("module"));
				mt.setName(rs.getString("name"));
				mt.setLocale(rs.getString("locale"));
				mt.setSubject(rs.getString("subject"));
				mt.setText(rs.getString("text"));
				mt.setHtml(rs.getString("html"));
				mt.setModel(rs.getString("model"));
				return mt;
			}
		});
	}
	
	public MailTemplate get(String id) {
		List<MailTemplate> mts = list(id, null, null, null, null, 0, 1);
		if(mts.size()>0) {
			return mts.get(0);
		}
		return null;
	}
	
	public MailTemplate save(MailTemplate mt) {
		List<MailTemplate> mts = list(null, mt.getModule(), mt.getName(), mt.getLocale(), null, 0, 1);
		if(mts.size()>0) {
			mt.setId(mts.get(0).getId());
		}
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if(mt.getId()==null) {
			Insert i = SqlBuilderFactory.insert("mail_template");
			mt.setId(UUID.randomUUID().toString());
			i.addField("id",mt.getId());
			i.addField("module",mt.getModule());
			i.addField("name",mt.getName());
			i.addField("locale",mt.getLocale());
			i.addField("subject",mt.getSubject());
			i.addField("html",mt.getHtml());
			i.addField("text",mt.getText());
			i.addField("model",mt.getModel());
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("mail_template");
			u.set("module",mt.getModule());
			u.set("name",mt.getName());
			u.set("locale",mt.getLocale());
			u.set("subject",mt.getSubject());
			u.set("html",mt.getHtml());
			u.set("text",mt.getText());
			u.set("model",mt.getModel());
			u.where(Operator.AND,u.condition(u.getTable(),"id",Comparator.EQ,mt.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return get(mt.getId());
	}
	
	public void delete(String id) {
		MailTemplate mt = get(id);
		if(mt==null) {
			return;
		}
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Delete d = SqlBuilderFactory.delete("mail_template");
		d.where(Operator.AND,d.condition(d.getTable(), "id", Comparator.EQ, id));
		templ.update(d.toSQL(), d.getParams());
	}

	public List<String> getModules() {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.queryForList("SELECT DISTINCT(module) m FROM mail_template ORDER BY m ", new HashMap<>(), String.class); 
	}
	
	public List<String> getLanguages() {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.queryForList("SELECT DISTINCT(locale) m FROM mail_template ORDER BY m ", new HashMap<>(), String.class); 
	}

	public Mail renderExample(String id, String mailConfigId) throws JsonParseException, JsonMappingException, IOException {
		MailTemplate mt = get(id);
		
		Map<String,Object> x = new HashMap<>();
		x = new ObjectMapper().readValue(mt.getModel(), new TypeReference<Map<String,Object>>() {});
		x.putAll(mailConfigService.get(mailConfigId).getParams());
		
		return Renderer.render(mt, x);
	}
	
	

}
