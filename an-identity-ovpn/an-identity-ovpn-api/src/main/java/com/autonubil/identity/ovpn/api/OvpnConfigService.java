package com.autonubil.identity.ovpn.api;

import java.util.List;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;

public interface OvpnConfigService {
	List<Ovpn> listOvpns(String id, String search);
	List<ConfigProvider> listClientConfigProviders(String search);
	List<ConfigProvider> listSessionConfigProviders(String search);
	List<OvpnClientConfigService> listClientConfigServices();
	List<OvpnSessionConfigService> listSessionConfigServices();

	Ovpn getOvpn(String id);
	Ovpn getOvpnByName(String name);
	Ovpn saveOvpn(Ovpn ovpnSource);
	void deleteOvpn(String id);


	List<OvpnPermission> listPermissions(String ovpnId, String source, String groupId);
	void addPermission(OvpnPermission permission);
	void removePermission(String appId, String source, String groupId);
	List<Ovpn> listOvpnsForGroups(List<Group> groups, String search);
}
