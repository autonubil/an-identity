package com.autonubil.identity.ovpn.api;

import java.util.List;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;
import com.autonubil.identity.ovpn.api.entities.OvpnSource;

public interface OvpnConfigService {

	List<ConfigProvider> listClientConfigProviders(String id, String search);
	List<ConfigProvider> listServerConfigProviders(String id, String search);
	
	List<OvpnSource> listSources(String id, String search);

	OvpnSource getSource(String id);

	OvpnSource saveSource(OvpnSource ovpnSource);

	void deleteSource(String id);

	List<OvpnPermission> listPermissions(String ovpnId, String source, String groupId);

	void addPermission(OvpnPermission permission);

	void removePermission(String appId, String source, String groupId);

	List<Ovpn> listOvpnsForGroups(List<Group> groups, String search);
	
	List<OvpnClientConfigService> listClientConfigServices();
	List<OvpnServerConfigService> listServerConfigServices();
}
