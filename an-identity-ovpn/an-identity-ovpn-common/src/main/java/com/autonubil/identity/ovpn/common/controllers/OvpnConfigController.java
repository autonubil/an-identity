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

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.exceptions.NotAuthorizedException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.auth.impl.services.AuthService;
import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.api.OvpnServerConfigService;
import com.autonubil.identity.ovpn.api.entities.ConfigProvider;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPermission;
import com.autonubil.identity.ovpn.api.entities.OvpnServerConfigRequest;

@RestController
@RequestMapping("/autonubil")
public class OvpnConfigController {
	
	@Autowired
	private OvpnConfigService ovpnConfigService;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private AuditLogger auditLogger;

	
	@RequestMapping(value="/api/ovpn/client-config-providers",method=RequestMethod.GET)
	public List<ConfigProvider> listClientConfigProviders(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return ovpnConfigService.listClientConfigProviders(search);
	}
	@RequestMapping(value="/api/ovpn/server-config-providers",method=RequestMethod.GET)
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
	
	@RequestMapping(value="/api/ovpn/myvpns",method=RequestMethod.GET)
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
	
	
	@RequestMapping(value="/api/ovpn/myvpns/{sourceId}",method=RequestMethod.GET)
	public List<Ovpn> myVpns(@RequestParam(required=false) String search, @PathVariable String sourceId) throws AuthException {
		Identity i = IdentityHolder.get();
		if(i!=null) {
			if(i.getUser()!=null) {
				if(i.getUser().getSourceId().compareTo(sourceId)==0) {
					return ovpnConfigService.listOvpnsForGroups(i.getUser().getGroups(),search);
				}
			}

		}
		return new ArrayList<>();
	}
	
	


	
	@RequestMapping(value="/api/ovpn/vpns/{id}/server-config",method=RequestMethod.POST)
	public void getServerConfig(@PathVariable String id, HttpServletResponse response, @RequestBody OvpnServerConfigRequest configRequest) throws AuthException {
		Ovpn ovpn =  null;
		if (id.length() == 36) {		
			ovpn = this.ovpnConfigService.getOvpn(id) ;
		}
		if (ovpn == null) {
			// try by name
			ovpn =  this.ovpnConfigService.getOvpnByName(id) ;
		}
		if (ovpn == null){
			response.setStatus(404);
			return;
		}
		
		if ( (configRequest.getSourceId() == null) || (configRequest.getSourceId().length() != 36) ) {
			List<String> sources = new ArrayList<>();
			List<OvpnPermission> permissions =  this.ovpnConfigService.listPermissions(ovpn.getId(), null, null);
			for (OvpnPermission ovpnPermission : permissions) {
				if (!sources.contains(ovpnPermission.getSourceId())) {
					sources.add(ovpnPermission.getSourceId());
				}
			}
			if (sources.size() == 1) {
				configRequest.setSourceId(sources.get(0));
			} else {
				response.setStatus(400);
				return;
			}
		}
		
		Identity i = authService.authenticate(configRequest, false);
		
		if(i!=null) {
			
			List<Ovpn> userVpns = ovpnConfigService.listOvpnsForGroups(i.getUser().getGroups(),ovpn.getName());
			boolean allowed = false;
			for (Ovpn ovpn2 : userVpns) {
				if (ovpn.getId().equals( ovpn2.getId()) ) {
					allowed = true;
					break;
				}
			}
			
			if (!allowed) {
				response.setStatus(401);
				auditLogger.log("OPENVPN", "LOGIN_FAILED", "", "", i.getUser().getSourceName()+":"+i.getUser().getDisplayName(), "Access to vpn "+ ovpn.getName()+ " denied");
				return;
			}
			
			
			auditLogger.log("OPENVPN", "LOGIN_SUCCESS", "", "", i.getUser().getSourceName()+":"+i.getUser().getDisplayName(), "Login to vpn "+ ovpn.getName()+ " succeeded");
		} else {
			auditLogger.log("OPENVPN", "LOGIN_FAILED", "", "", "[unknown]", "Login failed");
			response.setStatus(403);
			return;
		}
		
		OvpnServerConfigService configService = null;
		try {
			List<ConfigProvider> serverConfigProviders = ovpnConfigService.listServerConfigProviders(ovpn.getServerConfigurationProvider());
			for (ConfigProvider serverProvider : serverConfigProviders) {
				if (serverProvider.getId().equals(ovpn.getServerConfigurationProvider())) {
					Class<?> clazz = Class.forName(serverProvider.getClassName());
					configService = (OvpnServerConfigService) clazz.newInstance();
					break;
				}
			}
			if (configService != null) {
				configService.setConfigruation(ovpn.getServerConfiguration());
				configService.setIfConfigInfo(configRequest.getLocal(),  configRequest.getLocalNetmask(), configRequest.getRemote(), configRequest.getRemoteNetmask());
			
				String clientConfig = configService.getServerConfiguration(ovpn,i);
				response.getOutputStream().write(clientConfig.getBytes("UTF-8"));
			    response.flushBuffer();
			}
		} catch (Exception e) {
			throw new RuntimeException("Client Configuration implementation class not found", e);
		}
		
	}
	
	@RequestMapping(value="/api/ovpn/vpns/{id}/client-config",method=RequestMethod.GET)
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
		response.setHeader("Content-Disposition", String.format("attachment; filename=%1$s@%2$s.ovpn",i.getUser().getUsername(),  resultVpn.getName() ));
		response.setHeader("Cache-Control" ,"no-store, no-cache, must-revalidate");

		OvpnClientConfigService configService = null;
		try {
			List<ConfigProvider> clientConfigProviders = ovpnConfigService.listClientConfigProviders(resultVpn.getClientConfigurationProvider());
			for (ConfigProvider configProvider : clientConfigProviders) {
				if (configProvider.getId().equals(resultVpn.getClientConfigurationProvider())) {
					Class<?> clazz = Class.forName(configProvider.getClassName());
					configService = (OvpnClientConfigService) clazz.newInstance();
					break;
				}
			}
			if (configService == null) {
				throw new IllegalArgumentException("Config Provider not found");
			}
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
