package com.autonubil.identity.ovpn.common.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;
import com.autonubil.identity.ovpn.api.entities.OvpnSource;

@RestController
@RequestMapping("/autonubil")
public class OvpnConfigController {
	
	@Autowired
	private OvpnConfigService ovpnConfigService;

	
	@RequestMapping(value="/api/ovpn/client_config_providers",method=RequestMethod.GET)
	public List<ConfigProvider> listClientConfigProviders(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listClientConfigProviders(null, search);
	}
	@RequestMapping(value="/api/ovpn/server_config_providers",method=RequestMethod.GET)
	public List<ConfigProvider> listServerConfigProviders(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listServerConfigProviders(null, search);
	}
	
	@RequestMapping(value="/api/ovpn/sources",method=RequestMethod.GET)
	public List<OvpnSource> listSources(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listSources(null, search);
	}
	
	@RequestMapping(value="/api/ovpn/sources",method=RequestMethod.POST)
	public OvpnSource createSource(@RequestBody OvpnSource source) throws AuthException {
		AuthUtils.checkAdmin();
		source.setId(null);
		return ovpnConfigService.saveSource(source);
	}
	
	@RequestMapping(value="/api/ovpn/sources/{id}",method=RequestMethod.GET)
	public OvpnSource getSource(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.getSource(id);
	}
	
	@RequestMapping(value="/api/ovpn/sources/{id}",method=RequestMethod.PUT)
	public OvpnSource updateSource(@PathVariable String id, @RequestBody OvpnSource source) throws AuthException {
		AuthUtils.checkAdmin();
		source.setId(id);
		return ovpnConfigService.saveSource(source);
	}
	
	@RequestMapping(value="/api/ovpn/vpns/{id}/permissions",method=RequestMethod.GET)
	public List<OvpnPermission> listPermissions(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listPermissions(id, null, null);
	}
 
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/ovpn/vpns/{id}/permissions",method=RequestMethod.POST)
	public void addPermission(@PathVariable String id, @RequestBody OvpnPermission permission) throws AuthException {
		AuthUtils.checkAdmin();
		permission.setOvpnId(id);
		ovpnConfigService.addPermission(permission);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/ovpn/vpns/{id}/permissions",method=RequestMethod.DELETE)
	public void removePermission(@PathVariable String id, @RequestParam String sourceId, @RequestParam String groupId) throws AuthException {
		AuthUtils.checkAdmin();
		ovpnConfigService.removePermission(id,sourceId,groupId);
	}
	
	@RequestMapping(value="/api/ovpn/my_vpns",method=RequestMethod.GET)
	public List<Ovpn> myVpns(@RequestParam(required=false) String search) throws AuthException {
		Identity i = IdentityHolder.get();
		List<Group> groups = new ArrayList<>();
		if(i!=null) {
			if(i.getUser()!=null) {
				
				groups.addAll(i.getUser().getGroups());
			}
			for(User u : i.getLinked()) {
				groups.addAll(u.getGroups());
			}
		}
		return ovpnConfigService.listOvpnsForGroups(groups,search);
	}
	
	
	@RequestMapping(value="/api/ovpn/my_vpns/{sourceId}",method=RequestMethod.GET)
	public List<Ovpn> myVpns(@RequestParam(required=false) String search, @PathVariable String sourceId) throws AuthException {
		Identity i = IdentityHolder.get();
		if(i!=null) {
			if(i.getUser()!=null) {
				if(i.getUser().getSourceId().compareTo(sourceId)==0) {
					return ovpnConfigService.listOvpnsForGroups(i.getUser().getGroups(),search);
				}
			}
			for(User u : i.getLinked()) {
				if(u.getSourceId().compareTo(sourceId)==0) {
					return ovpnConfigService.listOvpnsForGroups(i.getUser().getGroups(),search);
				}
			}
		}
		return new ArrayList<>();
	}
	


}
