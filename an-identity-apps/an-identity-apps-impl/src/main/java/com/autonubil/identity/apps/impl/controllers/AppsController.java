package com.autonubil.identity.apps.impl.controllers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.autonubil.identity.apps.impl.entities.App;
import com.autonubil.identity.apps.impl.entities.AppPermission;
import com.autonubil.identity.apps.impl.service.AppsService;
import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;

@RestController
@RequestMapping("/autonubil")
public class AppsController {

	@Autowired
	private AppsService appsService;
	
	@RequestMapping(value="/api/apps/apps",method=RequestMethod.GET)
	public List<App> list(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return appsService.list(null, search);
	}
	
	@RequestMapping(value="/api/apps/apps",method=RequestMethod.POST)
	public App create(@RequestBody App app) throws AuthException {
		AuthUtils.checkAdmin();
		app.setId(null);
		return appsService.save(app);
	}
	
	@RequestMapping(value="/api/apps/apps/{id}",method=RequestMethod.GET)
	public App get(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return appsService.get(id);
	}
	
	@RequestMapping(value="/api/apps/apps/{id}",method=RequestMethod.PUT)
	public App update(@PathVariable String id, @RequestBody App app) throws AuthException {
		AuthUtils.checkAdmin();
		app.setId(id);
		return appsService.save(app);
	}
	
	@RequestMapping(value="/api/apps/apps/{id}/permissions",method=RequestMethod.GET)
	public List<AppPermission> listPermissions(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return appsService.listPermissions(id, null, null);
	}

	@RequestMapping(value="/api/apps/apps/{id}/icon",method=RequestMethod.GET)
	public void getIcon(@PathVariable String id, HttpServletResponse response) throws Exception {
		byte[] b = appsService.getIcon(id);
		response.getOutputStream().write(b);
	}
	
	@RequestMapping(value="/api/apps/apps/{id}/icon",method=RequestMethod.POST)
	public void setIcon(@PathVariable String id, MultipartFile file) throws Exception {
		AuthUtils.checkAdmin();
		InputStream is = null;
		try {
			is = file.getInputStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			IOUtils.copy(is, os);
			appsService.setIcon(id,os.toByteArray());
		} catch (Exception e) {
			throw e;
		} finally {
			try {is.close();} catch (Exception e) {};
			
		}
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/apps/apps/{id}/permissions",method=RequestMethod.POST)
	public void addPermission(@PathVariable String id, @RequestBody AppPermission permission) throws AuthException {
		AuthUtils.checkAdmin();
		permission.setAppId(id);
		appsService.addPermission(permission);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/apps/apps/{id}/permissions",method=RequestMethod.DELETE)
	public void removePermission(@PathVariable String id, @RequestParam String sourceId, @RequestParam String groupId) throws AuthException {
		AuthUtils.checkAdmin();
		appsService.removePermission(id,sourceId,groupId);
	}
	
	@RequestMapping(value="/api/apps/my_apps",method=RequestMethod.GET)
	public List<App> myApps(@RequestParam(required=false) String search) throws AuthException {
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
		return appsService.listAppsForGroups(groups,search);
	}
	
	
	@RequestMapping(value="/api/apps/my_apps/{sourceId}",method=RequestMethod.GET)
	public List<App> myApps(@RequestParam(required=false) String search, @PathVariable String sourceId) throws AuthException {
		Identity i = IdentityHolder.get();
		if(i!=null) {
			if(i.getUser()!=null) {
				if(i.getUser().getSourceId().compareTo(sourceId)==0) {
					return appsService.listAppsForGroups(i.getUser().getGroups(),search);
				}
			}
			for(User u : i.getLinked()) {
				if(u.getSourceId().compareTo(sourceId)==0) {
					return appsService.listAppsForGroups(i.getUser().getGroups(),search);
				}
			}
		}
		return new ArrayList<>();
	}
	
	
}
