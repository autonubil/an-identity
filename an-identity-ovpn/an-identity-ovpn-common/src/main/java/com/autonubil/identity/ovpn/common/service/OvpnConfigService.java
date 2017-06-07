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
	public List<Ovpn> listOvpns(String id, String search) {

		List<Ovpn> out = new ArrayList<>();

		Select s = SqlBuilderFactory.select();
		Table source = s.fromTable("vpn");

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
	public Ovpn getOvpn(String id) {
		List<Ovpn> a = listOvpns(id, null);
		if (a.size() > 0) {
			return a.get(0);
		}
		return null;
	}
	
	@Override
	public Ovpn getOvpnByName(String name) {
		List<Ovpn> a = listOvpns(null, name);
		for (Ovpn ovpn : a) {
			if (ovpn.getName().equals(name))
				return ovpn;
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
	public Ovpn saveOvpn(Ovpn ovpnSource) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if (ovpnSource.getId() == null) {
			Insert i = SqlBuilderFactory.insert("vpn");
			ovpnSource.setId(UUID.randomUUID().toString());
			i.addField("id", ovpnSource.getId());
			i.addField("name", ovpnSource.getName().toLowerCase());
			i.addField("description", ovpnSource.getDescription());
			i.addField("client_config_provider", ovpnSource.getClientConfigurationProvider());
			i.addField("server_config_provider", ovpnSource.getServerConfigurationProvider());
			i.addField("client_configuration", ovpnSource.getClientConfiguration().toString());
			i.addField("server_configuration", ovpnSource.getServerConfiguration().toString());
			templ.update(i.toSQL(), i.getParams());
		} else {
			Update u = SqlBuilderFactory.update("vpn");
			u.set("name", ovpnSource.getName());
			u.set("description", ovpnSource.getDescription());
			u.set("client_config_provider", ovpnSource.getClientConfigurationProvider());
			u.set("server_config_provider", ovpnSource.getServerConfigurationProvider());
			try {
				u.set("client_configuration",
						new ObjectMapper().writeValueAsString(ovpnSource.getClientConfiguration()));
				u.set("server_configuration",
						new ObjectMapper().writeValueAsString(ovpnSource.getServerConfiguration()));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			u.where(Operator.AND, u.condition(u.getTable(), "id", Comparator.EQ, ovpnSource.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return getOvpn(ovpnSource.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#deleteSource(java.lang.
	 * String)
	 */
	@Override
	public void deleteOvpn(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("vpn_permission");
			d.where(Operator.AND, d.condition(d.getTable(), "vpn", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("vpn");
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
		Table app = s.fromTable("vpn_permission");

		if (ovpnId != null) {
			s.where(Operator.AND, s.condition(app, "vpn_id", Comparator.EQ, ovpnId));
		}
		if (source != null) {
			s.where(Operator.AND, s.condition(app, "source", Comparator.EQ, source));
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
		Insert i = SqlBuilderFactory.insert("vpn_permission");
		i.addField("vpn_id", permission.getOvpnId());
		i.addField("source", permission.getSourceId());
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
			Delete d = SqlBuilderFactory.delete("vpn_permission");
			d.where(Operator.AND, d.condition(d.getTable(), "vpn_id", Comparator.EQ, appId));
			d.where(Operator.AND, d.condition(d.getTable(), "source", Comparator.EQ, source));
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
		JoinableTable ovpn = s.fromTable("vpn");
		s.select(Aggregation.NONE, ovpn, "id", "id");
		s.select(Aggregation.NONE, ovpn, "name", "name");
		s.select(Aggregation.NONE, ovpn, "description", "description");
		
		s.select(Aggregation.NONE, ovpn, "client_config_provider", "client_config_provider");
		s.select(Aggregation.NONE, ovpn, "server_config_provider", "server_config_provider");
		
		s.select(Aggregation.NONE, ovpn, "client_configuration", "client_configuration");
		s.select(Aggregation.NONE, ovpn, "server_configuration", "server_configuration");
		
		// s.select(Aggregation.NONE, ovpn, "vpn", "vpn");

		if (!StringUtils.isEmpty(search)) {
			s.where(Operator.AND, s.condition(ovpn, "name", Comparator.LIKE, "%" + search.toLowerCase() + "%"));
		}

		JoinedTable vpnPerm = ovpn.joinTable(JoinType.LEFT, "vpn_permission");
		s.select(Aggregation.NONE, vpnPerm, "source", "source");
		s.select(Aggregation.NONE, vpnPerm, "group_id", "group_id");

		vpnPerm.on(s.condition(ovpn, "id", Comparator.EQ, vpnPerm, "vpn_id")
				.and(s.condition(vpnPerm, "group_id", Comparator.IN, groupIds)));

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

	private class OvpnSourceRowMapper implements RowMapper<Ovpn> {

		@Override
		public Ovpn mapRow(ResultSet rs, int rowNum) throws SQLException {
			Ovpn out = new Ovpn();
			out.setId(rs.getString("id"));
			out.setDescription(rs.getString("description"));
			out.setName(rs.getString("name"));

			out.setClientConfigurationProvider(rs.getString("client_config_provider"));
			out.setServerConfigurationProvider(rs.getString("server_config_provider"));
			try {
				out.setClientConfiguration(new ObjectMapper().readTree(rs.getString("client_configuration")));
				out.setServerConfiguration(new ObjectMapper().readTree(rs.getString("server_configuration")));
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
			out.setOvpnId(rs.getString("vpn_id"));
			out.setSourceId(rs.getString("source"));
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

			String group = rs.getString("source") + ":" + rs.getString("group_id");

			if (groups != null && groups.get(group) == null) {
				return;
			}

			found.add(id);

			Ovpn out = new Ovpn();
			out.setId(id);
			out.setName(rs.getString("name"));
			out.setDescription(rs.getString("description"));
			out.setClientConfigurationProvider(rs.getString("client_config_provider"));
			out.setServerConfigurationProvider(rs.getString("server_config_provider"));
			try {
				out.setClientConfiguration(new ObjectMapper().readTree(rs.getString("client_configuration")));
				out.setServerConfiguration(new ObjectMapper().readTree(rs.getString("server_configuration")));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			
			
			getOvpns().add(out);
		}

		public List<Ovpn> getOvpns() {
			return ovpns;
		}

	}

	private List<OvpnClientConfigService> ovpnClientConfigServices;

	@Autowired
	protected void setOvpnClientConfigService(List<OvpnClientConfigService> ovpnClientConfigServices) {
		this.ovpnClientConfigServices = ovpnClientConfigServices;
	}

	@Override
	public List<OvpnClientConfigService> listClientConfigServices() {
		return this.ovpnClientConfigServices;
	}

	@Override
	public List<ConfigProvider> listClientConfigProviders(String search) {
		List<ConfigProvider> result = new ArrayList<>();
		for (OvpnClientConfigService configService : this.listClientConfigServices()) {
			ConfigProvider p = new ConfigProvider();
			p.setId(configService.getId());
			p.setClassName(configService.getClassName());
			p.setDisplayName(configService.getDisplayName());
			p.setDescription(configService.getDescription());
			if ((search == null || search.length() == 0) || (p.getClassName().contains(search)
					|| p.getDescription().contains(search) || p.getDisplayName().contains(search)|| p.getId().equals(search)  )) {
				result.add(p);
			}
		}
		return result;
	}

	private List<OvpnServerConfigService> ovpnServerConfigServices;

	@Autowired
	protected void setOvpnServerConfigService(List<OvpnServerConfigService> ovpnServerConfigServices) {
		this.ovpnServerConfigServices = ovpnServerConfigServices;
	}

	@Override
	public List<OvpnServerConfigService> listServerConfigServices() {
		return this.ovpnServerConfigServices;
	}

	@Override
	public List<ConfigProvider> listServerConfigProviders(String search) {
		List<ConfigProvider> result = new ArrayList<>();
		for (OvpnServerConfigService configService : this.listServerConfigServices()) {

			ConfigProvider p = new ConfigProvider();
			p.setId(configService.getId());
			p.setClassName(configService.getClassName());
			p.setDisplayName(configService.getDisplayName());
			p.setDescription(configService.getDescription());
			if ((search == null || search.length() == 0) || (p.getClassName().contains(search)
					|| p.getDescription().contains(search) || p.getDisplayName().contains(search) || p.getId().equals(search)  )) {
				result.add(p);
			}

		}
		return result;
	}

}
