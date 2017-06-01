package com.autonubil.identity.ovpn.common.service;

import java.io.IOException;
import java.security.InvalidParameterException;
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

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnServerConfigService;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;
import com.autonubil.identity.ovpn.api.entities.OvpnSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class OvpnConfigService implements com.autonubil.identity.ovpn.api.OvpnConfigService {

	private static Log log = LogFactory.getLog(OvpnConfigService.class);

	@Autowired
	@Qualifier("ovpnDb")
	private DataSource dataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#listSources(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<OvpnSource> listSources(String id, String search) {

		List<OvpnSource> out = new ArrayList<>();

		Select s = SqlBuilderFactory.select();
		Table source = s.fromTable("ovpn_source");

		if (id != null) {
			s.where(Operator.AND, s.condition(source, "id", Comparator.EQ, id));
		}

		if (!StringUtils.isEmpty(search)) {
			s.where(Operator.AND, s.condition(source, "name", Comparator.LIKE, "%" + search.toLowerCase() + "%"));
		}

		s.order(source, "name", true);

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		out = templ.query(s.toSQL(), s.getParams(), new OvpnSourceRowMapper());
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.autonubil.identity.ovpn.common.service.test#getSource(java.lang.
	 * String)
	 */
	@Override
	public OvpnSource getSource(String id) {
		List<OvpnSource> a = listSources(id, null);
		if (a.size() > 0) {
			return a.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#saveSource(com.autonubil.
	 * identity.ovpn.api.entities.OvpnSource)
	 */
	@Override
	public OvpnSource saveSource(OvpnSource ovpnSource) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if (ovpnSource.getId() == null) {
			Insert i = SqlBuilderFactory.insert("ovpn_source");
			ovpnSource.setId(UUID.randomUUID().toString());
			i.addField("id", ovpnSource.getId());
			i.addField("name", ovpnSource.getName().toLowerCase());
			i.addField("description", ovpnSource.getDescription());
			i.addField("client_config_provider", ovpnSource.getClientConfigurationProvider());
			i.addField("server_config_provider", ovpnSource.getServerConfigurationProvider());
			
			try {
				i.addField("configuration", new ObjectMapper().writeValueAsString(ovpnSource.getConfiguration()));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("ovpn_source");
			u.set("name", ovpnSource.getName());
			u.set("description", ovpnSource.getDescription());
			u.set("name_lower", ovpnSource.getName().toLowerCase());
			u.set("client_config_provider", ovpnSource.getClientConfigurationProvider());
			u.set("server_config_provider", ovpnSource.getServerConfigurationProvider());
			try {
				u.set("configuration", new ObjectMapper().writeValueAsString(ovpnSource.getConfiguration()));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			u.where(Operator.AND, u.condition(u.getTable(), "id", Comparator.EQ, ovpnSource.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return getSource(ovpnSource.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#deleteSource(java.lang.
	 * String)
	 */
	@Override
	public void deleteSource(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("ovpn_permission");
			d.where(Operator.AND, d.condition(d.getTable(), "ovpn_source", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("ovpn_source");
			d.where(d.condition(d.getTable(), "id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#listPermissions(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<OvpnPermission> listPermissions(String ovpnId, String source, String groupId) {
		List<OvpnPermission> out = new ArrayList<>();
		Select s = SqlBuilderFactory.select();
		Table app = s.fromTable("app_permission");

		if (ovpnId != null) {
			s.where(Operator.AND, s.condition(app, "ovpn_id", Comparator.EQ, ovpnId));
		}
		if (source != null) {
			s.where(Operator.AND, s.condition(app, "ovpn_source", Comparator.EQ, source));
		}
		if (groupId != null) {
			s.where(Operator.AND, s.condition(app, "group_id", Comparator.EQ, groupId));
		}

		s.order(app, "name", true);

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		out = templ.query(s.toSQL(), s.getParams(), new OvpnPermissionRowMapper());
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.autonubil.identity.ovpn.common.service.test#addPermission(com.
	 * autonubil.identity.ovpn.api.entities.OvpnPermission)
	 */
	@Override
	public void addPermission(OvpnPermission permission) {
		List<OvpnPermission> ap = listPermissions(permission.getOvpnId(), permission.getSourceId(),
				permission.getGroupId());
		if (permission.getOvpnId() == null || permission.getSourceId() == null || permission.getGroupId() == null) {
			throw new InvalidParameterException("ovpn, source and group must not be null");
		}
		if (ap.size() > 0) {
			removePermission(permission.getOvpnId(), permission.getSourceId(), permission.getGroupId());
		}
		Insert i = SqlBuilderFactory.insert("app_permission");
		i.addField("ovpn_id", permission.getOvpnId());
		i.addField("ovpn_source", permission.getSourceId());
		i.addField("group_id", permission.getGroupId());
		i.addField("name", permission.getName());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		templ.update(i.toSQL(), i.getParams());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#removePermission(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void removePermission(String appId, String source, String groupId) {
		List<OvpnPermission> ap = listPermissions(appId, source, groupId);

		if (ap.size() > 0) {
			Delete d = SqlBuilderFactory.delete("app_permission");
			d.where(Operator.AND, d.condition(d.getTable(), "ovpn_id", Comparator.EQ, appId));
			d.where(Operator.AND, d.condition(d.getTable(), "ovpn_source", Comparator.EQ, source));
			d.where(Operator.AND, d.condition(d.getTable(), "group_id", Comparator.EQ, groupId));
			NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
			templ.update(d.toSQL(), d.getParams());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#listOvpnsForGroups(java.
	 * util.List, java.lang.String)
	 */
	@Override
	public List<Ovpn> listOvpnsForGroups(List<Group> groups, String search) {

		if (groups == null || groups.size() == 0) {
			return new ArrayList<>();
		}

		List<String> groupIds = new ArrayList<>();
		for (Group g : groups) {
			groupIds.add(g.getId());
		}

		Select s = SqlBuilderFactory.select();
		JoinableTable ovpn = s.fromTable("ovpn");
		s.select(Aggregation.NONE, ovpn, "id", "id");
		s.select(Aggregation.NONE, ovpn, "name", "name");
		// s.select(Aggregation.NONE, ovpn, "ovpn_source", "ovpn_source");

		if (!StringUtils.isEmpty(search)) {
			s.where(Operator.AND, s.condition(ovpn, "name", Comparator.LIKE, "%" + search.toLowerCase() + "%"));
		}

		JoinedTable appPerm = ovpn.joinTable(JoinType.LEFT, "ovpn_permission");
		s.select(Aggregation.NONE, appPerm, "ovpn_source", "ovpn_source");
		s.select(Aggregation.NONE, appPerm, "group_id", "group_id");

		appPerm.on(s.condition(ovpn, "id", Comparator.EQ, appPerm, "ovpn_id")
				.and(s.condition(appPerm, "group_id", Comparator.IN, groupIds)));

		s.order(ovpn, "name", true);

		log.warn("listing groups: (" + s.toSQL() + " / " + s.getParams() + ")");

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);

		OvpnPermissionCallbackHandler ch = new OvpnPermissionCallbackHandler(groups);
		try {
			templ.query(s.toSQL(), s.getParams(), ch);
		} catch (Exception e) {
			log.warn("error in statement (" + s.toSQL() + " / " + s.getParams() + ") --- ", e);
			throw new RuntimeException("error in statement (" + s.toSQL() + " / " + s.getParams() + ") --- ", e);
		}
		return ch.getOvpns();
	}

	private class OvpnSourceRowMapper implements RowMapper<OvpnSource> {

		@Override
		public OvpnSource mapRow(ResultSet rs, int rowNum) throws SQLException {
			OvpnSource out = new OvpnSource();
			out.setId(rs.getString("id"));
			out.setDescription(rs.getString("description"));
			out.setName(rs.getString("name"));

			out.setClientConfigurationProvider(rs.getString("client_config_provider"));
			out.setServerConfigurationProvider(rs.getString("server_config_provider"));
			try {
				out.setConfiguration(new ObjectMapper().readTree(rs.getString("configuration")));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return out;
		}
	}

	private class OvpnPermissionRowMapper implements RowMapper<OvpnPermission> {

		@Override
		public OvpnPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
			OvpnPermission out = new OvpnPermission();
			out.setOvpnId(rs.getString("ovpn_id"));
			out.setSourceId(rs.getString("ovpn_source"));
			out.setGroupId(rs.getString("group_id"));
			out.setName(rs.getString("name"));
			return out;
		}

	}

	private class OvpnPermissionCallbackHandler implements RowCallbackHandler {

		private List<Ovpn> ovpns = new ArrayList<>();
		private Map<String, Group> groups;
		private List<String> found = new ArrayList<>();

		public OvpnPermissionCallbackHandler(List<Group> groups) {
			this.groups = new HashMap<>();
			for (Group g : groups) {
				this.groups.put(g.getSourceId() + ":" + g.getId(), g);
			}
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException {

			String id = rs.getString("id");
			if (found.contains(id)) {
				return;
			}

			String group = rs.getString("ovpn_source") + ":" + rs.getString("group_id");

			if (groups != null && groups.get(group) == null) {
				return;
			}

			found.add(id);

			Ovpn out = new Ovpn();
			out.setId(id);
			out.setName(rs.getString("name"));
			out.setDescription(rs.getString("description"));
			getOvpns().add(out);
		}

		public List<Ovpn> getOvpns() {
			return ovpns;
		}

	}
	
    private List<OvpnClientConfigService> ovpnClientConfigServices;

    @Autowired
    protected void setOvpnClientConfigService(List<OvpnClientConfigService> ovpnClientConfigServices){
        this.ovpnClientConfigServices = ovpnClientConfigServices;
    }

	@Override
	public List<OvpnClientConfigService> listClientConfigServices() {
		return this.ovpnClientConfigServices;
	}

	@Override
	public List<ConfigProvider> listClientConfigProviders(String id, String search) {
		List<ConfigProvider> result = new ArrayList<>();
		for (OvpnClientConfigService configService : this.listClientConfigServices()) {
			ConfigProvider p = new ConfigProvider();
			p.setName(configService.getClass().getCanonicalName());
			p.setDisplayName(configService.getName());
			p.setDescription(configService.getDescription());
			result.add(p);
		}
		return result;
	}

	private List<OvpnServerConfigService> ovpnServerConfigServices;

    @Autowired
    protected void setOvpnServerConfigService(List<OvpnServerConfigService> ovpnClientConfigServices){
        this.ovpnServerConfigServices = ovpnServerConfigServices;
    }
    
	@Override
	public List<OvpnServerConfigService> listServerConfigServices() {
		return this.ovpnServerConfigServices;
	}
    
	@Override
	public List<ConfigProvider> listServerConfigProviders(String id, String search) {
		List<ConfigProvider> result = new ArrayList<>();
		for (OvpnServerConfigService configService : this.listServerConfigServices()) {
			/*
			ConfigProvider p = new ConfigProvider();
			p.setName(configService.getClass().getCanonicalName());
			p.setDisplayName(configService.getName());
			p.setDescription(configService.getDescription());
			result.add(p);
			*/
		}
		return result;
	}

}
