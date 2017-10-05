package com.autonubil.identity.ldap.impl.services;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Condition;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;
import de.disk0.db.sqlbuilder.interfaces.Update;

@Service
public class LdapCustomFieldConfigService {

	private static Log log = LogFactory.getLog(LdapCustomFieldConfigService.class);
	
	@Qualifier("ldapConfig")
	@Autowired
	private DataSource dataSource;

	public LdapCustomFieldConfigService() {
	}
	
	public List<LdapCustomsFieldConfig> list(String id, String sourceId, List<String> objectClasses, int offset, int max) {
		Select s = SqlBuilderFactory.select();
		Table ls = s.fromTable("ldap_config_field");
		if (id != null) {
			s.where(Operator.AND, s.condition(ls, "id", Comparator.EQ, id));
		}
		if(objectClasses!=null && objectClasses.size()>0) {
			Condition c = null;
			for(String oc : objectClasses) {
				Condition cs = s.condition(ls, "object_class", Comparator.EQ, oc);
				c = c==null?cs:c.or(cs);
			}
			s.where(Operator.AND, c);
		}
		log.debug(s.toSQL()+" / "+s.getParams());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.query(s.toSQL(), s.getParams(), new LdapCustomFieldConfigRowMapper());
	}
	
	
	public LdapCustomsFieldConfig get(String sourceId, String id) {
		if(id==null) return null;
		List<LdapCustomsFieldConfig> ls = list(id, sourceId, null, 0, 100);
		if(ls.size()>0) {
			return ls.get(0);
		}
		return null;
	}
	
	public LdapCustomsFieldConfig save(LdapCustomsFieldConfig f) {
		LdapCustomsFieldConfig l = get(f.getSourceId(), f.getId());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if(l==null) {
			Insert i = SqlBuilderFactory.insert("ldap_config_field");
			f.setId(UUID.randomUUID().toString());
			i.addField("id", f.getId());
			i.addField("ldap_config_id", f.getSourceId());
			i.addField("object_class", f.getObjectClass());
			i.addField("attribute_name", f.getAttributeName());
			i.addField("attribute_type", f.getAttributeType());
			i.addField("display_name", f.getDisplayName());
			i.addField("multi", f.isMulti());
			i.addField("admin_editable", f.isAdminEditable());
			i.addField("user_editable", f.isUserEditable());
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("ldap_config_field");
			f.setId(UUID.randomUUID().toString());
			u.set("id", f.getId());
			u.set("ldap_config_id", f.getSourceId());
			u.set("object_class", f.getObjectClass());
			u.set("attribute_name", f.getAttributeName());
			u.set("attribute_type", f.getAttributeType());
			u.set("display_name", f.getDisplayName());
			u.set("multi", f.isMulti());
			u.set("admin_editable", f.isAdminEditable());
			u.set("user_editable", f.isUserEditable());
			u.where(Operator.AND,u.condition(u.getTable(), "id", Comparator.EQ, f.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return get(f.getSourceId(), f.getId());
	}

	public void delete(String sourceId, String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("ldap_config_field");
			d.where(Operator.AND,d.condition(d.getTable(), "id", Comparator.EQ, id));
			d.where(Operator.AND,d.condition(d.getTable(), "ldap_config_id", Comparator.EQ, sourceId));
			templ.update(d.toSQL(), d.getParams());
		}
	}


	
	
	
	
}
