package com.autonubil.identity.ldap.impl.services;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.autonubil.identity.util.TokenGenerator;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Aggregation;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;

@Service
public class ResetTokenService {

	@Autowired
	@Qualifier("ldapConfig")
	private DataSource dataSource;
	
	public String createToken(String source, String userId, String email, Date expires) {
		String nt = TokenGenerator.getToken(24);
		invalidateTokens(source, userId);
		Insert i = SqlBuilderFactory.insert("reset_token");
		i.addField("source", source);
		i.addField("user_id", userId);
		i.addField("token", nt);
		i.addField("email", email.toUpperCase());
		i.addField("token_expires", expires);
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		templ.update(i.toSQL(), i.getParams());
		return nt;
		
	}

	public boolean checkToken(String source, String userId, String token, String email) {
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("reset_token");
		s.select(Aggregation.COUNT,t,"token","tokens");
		s.where(Operator.AND,s.condition(t,"source",Comparator.EQ,source));
		s.where(Operator.AND,s.condition(t,"user_id",Comparator.EQ,userId));
		s.where(Operator.AND,s.condition(t,"token",Comparator.EQ,token));
		s.where(Operator.AND,s.condition(t,"token_expires",Comparator.GT, new Date()));
		s.where(Operator.AND,s.condition(t,"email",Comparator.EQ,email.toUpperCase()));
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Integer i = templ.queryForObject(s.toSQL(), s.getParams(), Integer.class); 
		i = i==null?0:i;
		return i>0;
	}
	
	public void invalidateTokens(String source, String userId) {
		Delete s = SqlBuilderFactory.delete("reset_token");
		s.where(Operator.AND,s.condition(s.getTable(),"source",Comparator.EQ,source));
		s.where(Operator.AND,s.condition(s.getTable(),"user_id",Comparator.EQ,userId));
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		templ.update(s.toSQL(), s.getParams()); 
	}
	
	
	@Scheduled(fixedRate=60000)
	public void removeExpired() {
		Delete d = SqlBuilderFactory.delete("reset_token");
		d.where(d.condition(d.getTable(), "token_expires", Comparator.LTE, new Date()));
		
	}
	
}
