package com.autonubil.identity.ldap.impl.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapConfig.AUTH;
import com.autonubil.identity.ldap.api.entities.LdapConfig.ENCRYPTION;

public class LdapConfigRowMapper implements RowMapper<LdapConfig> {

	@Override
	public LdapConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		LdapConfig out = new LdapConfig();
		
		out.setId(rs.getString("id"));
		out.setName(rs.getString("name"));
		out.setHost(rs.getString("host"));
		
		out.setAdminBindDn(rs.getString("admin_bind_dn"));
		out.setServerType(rs.getString("server_type"));
		out.setRootDse(rs.getString("root_dse"));
		out.setOtpGroup(rs.getString("otp_group"));
		
		out.setCert(rs.getString("cert"));
		out.setTrustAll(rs.getBoolean("trust_all"));
		out.setUseOtp(rs.getBoolean("use_otp"));

		out.setAuth(AUTH.valueOf(rs.getString("auth")));
		out.setEncryption(ENCRYPTION.valueOf(rs.getString("encryption")));
		
		out.setPort(rs.getInt("port"));
		
		out.setUseAsAuth(rs.getBoolean("use_as_auth"));
		
		return out;
	}

	
}
