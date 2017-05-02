package com.autonubil.identity.apps.impl.service;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.autonubil.identity.apps.impl.entities.App;
import com.autonubil.identity.apps.impl.entities.AppPermission;
import com.autonubil.identity.auth.api.entities.Group;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Aggregation;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.JoinType;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.JoinableTable;
import de.disk0.db.sqlbuilder.interfaces.JoinedTable;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;
import de.disk0.db.sqlbuilder.interfaces.Update;

@Service
public class AppsService {
	
	private static Log log = LogFactory.getLog(AppsService.class);

	@Autowired
	@Qualifier("appsDb")
	private DataSource dataSource;

	public List<App> list(String id, String search) {
		
		List<App> out = new ArrayList<>();
		
		Select s = SqlBuilderFactory.select();
		Table app =  s.fromTable("app");

		if(id!=null) {
			s.where(Operator.AND, s.condition(app,"id",Comparator.EQ,id));
		}
		
		if(!StringUtils.isEmpty(search)) {
			s.where(Operator.AND, s.condition(app,"name_lower",Comparator.LIKE, "%"+search.toLowerCase()+"%"));
		}
		
		s.order(app, "name", true);
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		out = templ.query(s.toSQL(), s.getParams(), new AppRowMapper());
		return out;
	}
	
	public App get(String id) {
		List<App> a = list(id, null);
		if(a.size()>0) {
			return a.get(0);
		}
		return null;
	}
	
	public App save(App a) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if (a.getId() == null) {
			Insert i = SqlBuilderFactory.insert("app");
			a.setId(UUID.randomUUID().toString());
			i.addField("id", a.getId());
			i.addField("name", a.getName());
			i.addField("description", a.getDescription());
			i.addField("name_lower", a.getName().toLowerCase());
			i.addField("url", a.getUrl());
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("app");
			u.set("name", a.getName());
			u.set("description", a.getDescription());
			u.set("name_lower", a.getName().toLowerCase());
			u.set("url", a.getUrl());
			u.where(Operator.AND, u.condition(u.getTable(), "id", Comparator.EQ, a.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return get(a.getId());
	}
	
	public void delete(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("app");
			d.where(Operator.AND, d.condition(d.getTable(), "app_id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("app");
			d.where(d.condition(d.getTable(), "id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
	}
	
	public List<AppPermission> listPermissions(String appId, String source, String groupId) {
		List<AppPermission> out = new ArrayList<>();
		Select s = SqlBuilderFactory.select();
		Table app =  s.fromTable("app_permission");

		if(appId!=null) {
			s.where(Operator.AND,s.condition(app,"app_id",Comparator.EQ,appId));
		}
		if(source!=null) {
			s.where(Operator.AND,s.condition(app,"source",Comparator.EQ,source));
		}
		if(groupId!=null) {
			s.where(Operator.AND,s.condition(app,"group_id",Comparator.EQ,groupId));
		}
		
		s.order(app, "name", true);
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		out = templ.query(s.toSQL(), s.getParams(), new AppPermissionRowMapper());
		return out;
	}
	
	public void addPermission(AppPermission permission) {
		List<AppPermission> ap = listPermissions(permission.getAppId(), permission.getSourceId(), permission.getGroupId());
		if(
				permission.getAppId()==null ||
				permission.getSourceId()==null ||
				permission.getGroupId()==null
			) {
			throw new InvalidParameterException("app, source and group must not be null");
		}
		if(ap.size()>0) {
			removePermission(permission.getAppId(), permission.getSourceId(), permission.getGroupId());
		}
		Insert i = SqlBuilderFactory.insert("app_permission");
		i.addField("app_id",permission.getAppId());
		i.addField("source",permission.getSourceId());
		i.addField("group_id",permission.getGroupId());
		i.addField("name",permission.getName());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		templ.update(i.toSQL(), i.getParams());

	}
	
	public void removePermission(String appId, String source, String groupId) {
		List<AppPermission> ap = listPermissions(appId, source, groupId);
		
		if(ap.size()>0) {
			Delete d = SqlBuilderFactory.delete("app_permission");
			d.where(Operator.AND,d.condition(d.getTable(), "app_id", Comparator.EQ, appId));
			d.where(Operator.AND,d.condition(d.getTable(), "source", Comparator.EQ, source));
			d.where(Operator.AND,d.condition(d.getTable(), "group_id", Comparator.EQ, groupId));
			NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
			templ.update(d.toSQL(), d.getParams());
		}
	}
	
	public List<App> listAppsForGroups(List<Group> groups, String search) {
		
		if(groups == null || groups.size()==0) {
			return new ArrayList<>();
		}
		
		List<String> groupIds = new ArrayList<>();
		for(Group g : groups) {
			groupIds.add(g.getId());
		}
		
		Select s = SqlBuilderFactory.select();
		JoinableTable app = s.fromTable("app");
		s.select(Aggregation.NONE, app, "id", "id");
		s.select(Aggregation.NONE, app, "name", "name");
		s.select(Aggregation.NONE, app, "url", "url");
		
		if(!StringUtils.isEmpty(search)) {
			s.where(Operator.AND, s.condition(app,"name_lower",Comparator.LIKE, "%"+search.toLowerCase()+"%"));
		}
		
		JoinedTable appPerm = app.joinTable(JoinType.LEFT, "app_permission");
		s.select(Aggregation.NONE, appPerm, "source", "source");
		s.select(Aggregation.NONE, appPerm, "group_id", "group_id");

		appPerm.on(s.condition(app,"id",Comparator.EQ,appPerm,"app_id").and(s.condition(appPerm,"group_id",Comparator.IN,groupIds)));
	
		s.order(app, "name", true);

		log.warn("listing groups: ("+s.toSQL()+" / "+s.getParams()+")");
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		AppPermissionCallbackHandler ch = new AppPermissionCallbackHandler(groups);
		try {
			templ.query(s.toSQL(),s.getParams(),ch);
		} catch (Exception e) {
			log.warn("error in statement ("+s.toSQL()+" / "+s.getParams()+") --- ",e);
			throw new RuntimeException("error in statement ("+s.toSQL()+" / "+s.getParams()+") --- ", e);
		}
		return ch.getApps();
	}

	private class AppRowMapper implements RowMapper<App> {

		@Override
		public App mapRow(ResultSet rs, int rowNum) throws SQLException {
			App out = new App();
			out.setId(rs.getString("id"));
			out.setDescription(rs.getString("description"));
			out.setName(rs.getString("name"));
			out.setUrl(rs.getString("url"));
			return out;
		}
	}
	
	private class AppPermissionRowMapper implements RowMapper<AppPermission> {

		@Override
		public AppPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
			AppPermission out = new AppPermission();
			out.setAppId(rs.getString("app_id"));
			out.setSourceId(rs.getString("source"));
			out.setGroupId(rs.getString("group_id"));
			out.setName(rs.getString("name"));
			return out;
		}
		
	}

	private class AppPermissionCallbackHandler  implements RowCallbackHandler {

		private List<App> apps = new ArrayList<>();
		private Map<String,Group> groups;
		private List<String> found = new ArrayList<>();
		
		public AppPermissionCallbackHandler(List<Group> groups) {
			this.groups = new HashMap<>();
			for(Group g : groups) {
				this.groups.put(g.getSourceId()+":"+g.getId(), g);
			}
		}
		
		
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			
			String id = rs.getString("id"); 
			if(found.contains(id)) {
				return;
			}
			
			String group = rs.getString("source")+":"+rs.getString("group_id"); 
			
			if(groups!=null && groups.get(group)==null) {
				return;
			}
			
			found.add(id);

			App out = new App();
			out.setId(id);
			out.setName(rs.getString("name"));
			out.setUrl(rs.getString("url"));
			getApps().add(out);
		}


		public List<App> getApps() {
			return apps;
		}

	}

	public byte[] getIcon(String id) {
		Connection c = null;
		PreparedStatement ps = null;
		try {

			c = dataSource.getConnection();
			ps = c.prepareStatement("SELECT icon FROM app_icon WHERE app_id = ?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getBytes("icon");
			}
			return new byte[] {
				0x47,0x49,0x46,0x38,0x39,0x61,0x01,0x00,0x01,0x00,(byte)0x80,0x00,0x00,(byte)0xff,(byte)0xff,(byte)0xff,
				0x00,0x00,0x00,0x21,(byte)0xf9,0x04,0x00,0x00,0x00,0x00,0x00,0x2c,0x00,0x00,0x00,0x00,
				0x01,0x00,0x01,0x00,0x00,0x02,0x02,0x44,0x01,0x00,0x3b
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try { ps.close(); } catch (Exception e2) {};
			try { c.close(); } catch (Exception e2) {};
		}
	}

	public void setIcon(String id, byte[] byteArray) {
		Connection c = null;
		PreparedStatement ps = null;
		try {

			c = dataSource.getConnection();
			ps = c.prepareStatement("DELETE FROM app_icon WHERE app_id = ?");
			ps.setString(1, id);
			ps.executeUpdate();
			ps.close();

			ps = c.prepareStatement("INSERT INTO app_icon (app_id,icon) VALUES (?,?)");
			ps.setString(1, id);
			ps.setBytes(2, byteArray);
			ps.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try { ps.close(); } catch (Exception e2) {};
			try { c.close(); } catch (Exception e2) {};
		}
	}
	
}
