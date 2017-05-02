package com.autonubil.identity.localauth.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.autonubil.identity.localauth.entities.LocalAuthUser;

public class LocalAuthUserResultSetExtractor implements ResultSetExtractor<List<LocalAuthUser>> {

	private int offset;
	private int limit;
	private List<LocalAuthUser> result = new ArrayList<>(); 
			
	public LocalAuthUserResultSetExtractor(Integer offset, Integer limit) {
		this.offset = offset==null?0:offset;
		this.limit = limit==null?-1:limit; 
	}
	
	@Override
	public List<LocalAuthUser> extractData(ResultSet rs) throws SQLException, DataAccessException {
		int count = -1;
		while(rs.next()) {
			count++;
			if(count<offset) continue;
			LocalAuthUser lau = new LocalAuthUser();
			lau.setId(rs.getString("id"));
			lau.setUsername(rs.getString("username"));
			lau.setEmail(rs.getString("email"));
			lau.setUseOtp(rs.getBoolean("use_otp"));
			result.add(lau);
			if(limit>0 && result.size()==limit) {
				break;
			}
		}
		return result;
	}

}
