package com.autonubil.identity.ldap.impl.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.LdapConnectionType;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.impl.util.factories.LdapConnectionFactory;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.mail.api.MailServiceFactory;

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
public class LdapConfigServiceImpl implements LdapConfigService {

	private static Log log = LogFactory.getLog(LdapConfigServiceImpl.class);
	
	@Autowired
	@Qualifier("ldapConfig")
	private DataSource dataSource;
	
	@Autowired
	private List<LdapConnectionFactory> connectionFactories = new ArrayList<>(); 
	
	@Autowired
	private LdapCustomFieldConfigService ldapConfigFieldService; 
	
	@Autowired
	private MailServiceFactory mailServiceFactory;
	
	private MailService mailService;
	
	@PostConstruct
	public void init() {
		this.mailService = mailServiceFactory.getDefaultMailService();
	}
	
	
	
	public List<LdapConnectionType> getConnectionTypes() {
		List<LdapConnectionType> out = new ArrayList<>();
		for(LdapConnectionFactory lcf : connectionFactories) {
			out.add(lcf.getType());
		}
		return out;
	}
	

	@Override
	public List<LdapConfig> list(String id, String order, Boolean useAsAuth) {
		Select s = SqlBuilderFactory.select();
		Table ls = s.fromTable("ldap_config");
		if (id != null) {
			s.where(Operator.AND, s.condition(ls, "id", Comparator.EQ, id));
		}
		if (useAsAuth != null) {
			s.where(Operator.AND, s.condition(ls, "use_as_auth", Comparator.EQ, useAsAuth.booleanValue() ? 1 : 0));
		}
		if (order != null) {
			s.order(ls, order, true);
		} else {
			s.order(ls, "name", true);
		}
		log.debug(s.toSQL()+" / "+s.getParams());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.query(s.toSQL(), s.getParams(), new LdapConfigRowMapper());
	}

	public LdapConfig get(String id) {
		List<LdapConfig> cs = list(id, null, null);
		if (cs.size() == 1) {
			return cs.get(0);
		}
		return null;
	}

	public LdapConfig save(LdapConfig config) {
		LdapConfig s = null;
		if (config.getId() != null) {
			s = get(config.getId());
		}
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if (s == null) {
			Insert i = SqlBuilderFactory.insert("ldap_config");
			config.setId(UUID.randomUUID().toString());
			i.addField("id", config.getId());
			i.addField("name", config.getName());
			i.addField("cert", config.getCert());
			i.addField("trust_all", config.isTrustAll());
			i.addField("host", config.getHost());
			i.addField("port", config.getPort());
			i.addField("encryption", config.getEncryption().name());
			i.addField("auth", config.getAuth().name());
			i.addField("admin_bind_dn", config.getAdminBindDn());
			i.addField("root_dse", config.getRootDse());
			i.addField("otp_group", config.getOtpGroup());
			i.addField("server_type", config.getServerType());
			i.addField("use_as_auth", config.isUseAsAuth() ? 1 : 0);
			i.addField("use_otp", config.isUseOtp() ? 1 : 0);
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("ldap_config");
			u.set("name", config.getName());
			u.set("cert", config.getCert());
			u.set("trust_all", config.isTrustAll());
			u.set("host", config.getHost());
			u.set("port", config.getPort());
			u.set("encryption", config.getEncryption().name());
			u.set("auth", config.getAuth().name());
			u.set("admin_bind_dn", config.getAdminBindDn());
			u.set("root_dse", config.getRootDse());
			u.set("otp_group", config.getOtpGroup());
			u.set("server_type", config.getServerType());
			u.set("use_as_auth", config.isUseAsAuth() ? 1 : 0);
			u.set("use_otp", config.isUseOtp() ? 1 : 0);
			u.where(Operator.AND,u.condition(u.getTable(), "id", Comparator.EQ, config.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return get(config.getId());
	}

	public void delete(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("ldap_config_password");
			d.where(Operator.AND,d.condition(d.getTable(), "ldap_config_id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("ldap_config_field");
			d.where(Operator.AND,d.condition(d.getTable(), "ldap_config_id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("ldap_config");
			d.where(Operator.AND,d.condition(d.getTable(), "id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
	}

	public String getPassword(String id) {
		Select s = SqlBuilderFactory.select();
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Table ls = s.fromTable("ldap_config_password");
		s.where(Operator.AND,s.condition(ls, "ldap_config_id", Comparator.EQ, id));
		s.select(Aggregation.NONE, ls, "password", "password");
		return templ.queryForObject(s.toSQL(), s.getParams(), String.class);
	}

	public void setPassword(String id, String password) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		LdapConfig s = get(id);
		if (s == null) {
			throw new RuntimeException("no such object: " + id);
		}
		;
		{
			log.info("delete old password ... ");
			Delete d = SqlBuilderFactory.delete("ldap_config_password");
			d.where(Operator.AND,d.condition(d.getTable(), "ldap_config_id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		;
		{
			log.info("insert new  password ... ");
			Insert i = SqlBuilderFactory.insert("ldap_config_password");
			i.addField("ldap_config_id", id);
			i.addField("password", password);
			templ.update(i.toSQL(), i.getParams());
		}
		;
	}
	
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@Override
	public LdapConnection connect(String id) {
		log.debug("looking for sources: " + id);
		LdapConfig config = get(id);
		if(config==null) {
			return null;
		}
		List<LdapCustomsFieldConfig> customFields = ldapConfigFieldService.list(null, id, null, 0, 1000);
		for(LdapConnectionFactory f : connectionFactories) {
			log.debug(
					"looking for sources: "+
					f.getType().getId()+
					" == "+
					config.getServerType()+
					"?");
			if(f.getType().getId().compareTo(config.getServerType())==0) {
				log.debug("looking for sources: "+f.getType().getId()+" == "+config.getServerType()+": "+f.getClass());
				return f.connect(config, config.getAdminBindDn(), getPassword(id),customFields,mailService);
			}
		}
		return null;
	}
	

}
