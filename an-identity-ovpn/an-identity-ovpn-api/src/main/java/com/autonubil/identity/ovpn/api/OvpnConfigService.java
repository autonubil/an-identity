package com.autonubil.identity.ovpn.api;

import java.util.List;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;
import com.autonubil.identity.ovpn.api.entities.OvpnSession;
import com.autonubil.identity.ovpn.api.entities.OvpnSessionConfigRequest;

public interface OvpnConfigService {
	
	int SESSION_EXPIRY = 60*30;
	int SESSION_RETENTION = 60*12*30;
	
	List<Ovpn> listOvpns(String id, String search);
	List<ConfigProvider> listClientConfigProviders(String search);
	List<ConfigProvider> listSessionConfigProviders(String search);
	List<ConfigProvider> listServerConfigProviders(String search);
	List<OvpnClientConfigService> listClientConfigServices();
	List<OvpnServerConfigService> listServerConfigServices();
	List<OvpnSessionConfigService> listSessionConfigServices();

	Ovpn getOvpn(String id);
	Ovpn getOvpnByName(String name);
	Ovpn saveOvpn(Ovpn ovpnSource);
	void deleteOvpn(String id);


	List<OvpnPermission> listPermissions(String ovpnId, String source, String groupId);
	void addPermission(OvpnPermission permission);
	void removePermission(String appId, String source, String groupId);
	List<Ovpn> listOvpnsForGroups(List<Group> groups, String search);
	
	OvpnSession getSession(String id, String sourceId, String userName);
	List<OvpnSession> getUserSessions(String sourceId, String userName);
	void updateSession(OvpnSession session);
	void saveSession(OvpnSession session);
	void deleteSession(OvpnSession session);
	void terminateSession(OvpnSession session);
	void purge();
	
	String calcSessionId(String ovpnId, OvpnSessionConfigRequest configRequest);
}
