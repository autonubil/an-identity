package com.autonubil.identity.ldap.impl.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;

public class LdapCustomFieldConfigRowMapper implements RowMapper<LdapCustomsFieldConfig> {

	@Override
	public LdapCustomsFieldConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		LdapCustomsFieldConfig out = new LdapCustomsFieldConfig();
		out.setId(rs.getString("id"));
		out.setSourceId(rs.getString("ldap_config_id"));
		out.setObjectClass(rs.getString("object_class"));
		out.setAttributeName(rs.getString("attribute_name"));
		out.setAttributeType(rs.getString("attribute_type"));
		out.setDisplayName(rs.getString("display_name"));
		out.setMulti(rs.getBoolean("multi"));
		out.setAdminEditable(rs.getBoolean("admin_editable"));
		out.setUserEditable(rs.getBoolean("user_editable"));
		out.setMulti(rs.getBoolean("multi"));
		return out;
	}

}
