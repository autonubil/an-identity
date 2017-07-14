package com.autonubil.identity.openid.impl.controllers;

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

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.openid.impl.entities.OAuthApp;
import com.autonubil.identity.openid.impl.services.OAuth2ServiceImpl;

@RestController
@RequestMapping("/autonubil")
public class OpenIdConfigController {

	@Autowired
	private OAuth2ServiceImpl oauthService;


	
	@RequestMapping(value = "/api/openid/applications", method = RequestMethod.GET)
	public List<OAuthApp> listOAuthApps(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return oauthService.listApplications(search);
	}
	

	@RequestMapping(value = "/api/openid/application/{id}", method = RequestMethod.GET)
	public OAuthApp getOAuthApp(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return oauthService.getApplication(id);
	}
	
	@RequestMapping(value = "/api/openid/application", method = RequestMethod.POST)
	public OAuthApp createOAuthApp(@RequestBody OAuthApp source) throws AuthException {
		AuthUtils.checkAdmin();
		return oauthService.saveApplication(source);
	}
	
	@RequestMapping(value = "/api/openid/application/{id}", method = RequestMethod.PUT)
	public OAuthApp updateOAuthApp(@PathVariable String id, @RequestBody OAuthApp source) throws AuthException {
		AuthUtils.checkAdmin();
		return oauthService.updateApplication(source);
	}
	
	

	@RequestMapping(value = "/api/openid/application/{id}/permissions", method = RequestMethod.GET)
	public List<com.autonubil.identity.openid.impl.entities.OAuthPermission> listPermissions(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return oauthService.listPermissions(id, null, null);
	}

	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/openid/application/{id}/permissions", method = RequestMethod.POST)
	public void addPermission(@PathVariable String id, @RequestBody com.autonubil.identity.openid.impl.entities.OAuthPermission permission) throws AuthException {
		AuthUtils.checkAdmin();
		permission.setClientId(id);
		oauthService.addPermission(permission);
	}

	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/openid/application/{id}/permissions", method = RequestMethod.DELETE)
	public void removePermission(@PathVariable String id, @RequestParam String sourceId, @RequestParam String groupId)
			throws AuthException {
		AuthUtils.checkAdmin();
		oauthService.removePermission(id, sourceId, groupId);
	}

	
}
