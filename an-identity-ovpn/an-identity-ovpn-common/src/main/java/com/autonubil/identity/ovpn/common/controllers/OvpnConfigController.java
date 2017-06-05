package com.autonubil.identity.ovpn.common.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.exceptions.NotAuthorizedException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;

@RestController
@RequestMapping("/autonubil")
public class OvpnConfigController {
	
	@Autowired
	private OvpnConfigService ovpnConfigService;

	
	@RequestMapping(value="/api/ovpn/client_config_providers",method=RequestMethod.GET)
	public List<ConfigProvider> listClientConfigProviders(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listClientConfigProviders(search);
	}
	@RequestMapping(value="/api/ovpn/server_config_providers",method=RequestMethod.GET)
	public List<ConfigProvider> listServerConfigProviders(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listServerConfigProviders(search);
	}
	
	@RequestMapping(value="/api/ovpn/vpns",method=RequestMethod.GET)
	public List<Ovpn> listVpns(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listOvpns(null, search);
	}
	
	@RequestMapping(value="/api/ovpn/vpns",method=RequestMethod.POST)
	public Ovpn createVpn(@RequestBody Ovpn source) throws AuthException {
		AuthUtils.checkAdmin();
		source.setId(null);
		return ovpnConfigService.saveOvpn(source);
	}
	
	@RequestMapping(value="/api/ovpn/vpns/{id}",method=RequestMethod.GET)
	public Ovpn getVpn(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.getOvpn(id);
	}
	
	@RequestMapping(value="/api/ovpn/vpns/{id}",method=RequestMethod.PUT)
	public Ovpn updateVpns(@PathVariable String id, @RequestBody Ovpn source) throws AuthException {
		AuthUtils.checkAdmin();
		source.setId(id);
		return ovpnConfigService.saveOvpn(source);
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
	
	@RequestMapping(value="/api/ovpn/vpns/{id}/client_config",method=RequestMethod.GET)
	public void getClientConfig(@PathVariable String id, HttpServletResponse response) throws AuthException {
		if(!AuthUtils.isLoggedIn()) {
			throw new NotAuthenticatedException();
		}

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
		Ovpn resultVpn = null; 
		List<Ovpn> myOvpns = ovpnConfigService.listOvpnsForGroups(groups,null);
		for(Ovpn vpn : myOvpns){
			if (vpn.getId().equals(id)) {
				resultVpn = vpn;
				break;
			}
		}
		
		if (resultVpn == null) {
			throw new NotAuthorizedException();
		}
		
		response.setContentType("application/x-openvpn-profile");
		response.setHeader("Content-Disposition", String.format("attachment; filename=%1$s-%2$s.ovpn",i.getUser().getUsername(),  resultVpn.getName() ));

		OvpnClientConfigService configService;
		try {
			Class<?> clazz = Class.forName(resultVpn.getClientConfigurationProvider());
			configService = (OvpnClientConfigService) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Client Configuration implementation class not found", e);
		}
		try {
			configService.setConfigruation(resultVpn.getClientConfiguration());
			String clientConfig = configService.getClientConfiguration(resultVpn, i);
			response.getOutputStream().write(clientConfig.getBytes("UTF-8"));
		    response.flushBuffer();
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate client configuration", e);
		}
	}
 
	
}
