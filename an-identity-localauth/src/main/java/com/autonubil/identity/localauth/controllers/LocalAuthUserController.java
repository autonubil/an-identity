package com.autonubil.identity.localauth.controllers;

import java.io.IOException;
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

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.localauth.entities.LocalAuthUser;
import com.autonubil.identity.localauth.services.LocalAuthUserService;
import com.autonubil.identity.util.totp.TotpUtil;
import com.google.zxing.WriterException;

@RestController
@RequestMapping("/autonubil")
public class LocalAuthUserController {

	@Autowired
	private LocalAuthUserService localAuthUserService;
	
	@RequestMapping(value="/api/localauth/users",method={RequestMethod.GET})
	public List<LocalAuthUser> list(
			@RequestParam(required=false) String search,
			@RequestParam(required=false) String username,
			@RequestParam(required=false,defaultValue="username") String order,
			@RequestParam(required=false,defaultValue="0") int offset, 
			@RequestParam(required=false,defaultValue="25") int limit
		) throws AuthException {
		AuthUtils.checkAdmin();
		return localAuthUserService.list(null,search,username,order,offset,limit);
	}
	
	@RequestMapping(value="/api/localauth/users",method={RequestMethod.POST})
	public LocalAuthUser create(@RequestBody LocalAuthUser lau) throws AuthException {
		AuthUtils.checkAdmin();
		lau.setId(null);
		return localAuthUserService.save(lau);
	}
	
	@RequestMapping(value="/api/localauth/users/{id}",method={RequestMethod.GET})
	public LocalAuthUser get(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return localAuthUserService.get(id);
	}
	
	@RequestMapping(value="/api/localauth/users/{id}",method={RequestMethod.PUT})
	public LocalAuthUser update(@RequestBody LocalAuthUser lau, @PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		lau.setId(id);
		return localAuthUserService.save(lau);
	}
	
	@RequestMapping(value="/api/localauth/users/{id}/otp",method={RequestMethod.POST})
	public void updateOTP(@PathVariable String id, HttpServletResponse response, @RequestParam(required=false,defaultValue="240") int size) throws AuthException, IOException, WriterException {
		AuthUtils.checkAdmin();
		String secretUrl = localAuthUserService.generateSecret(id);
		response.setContentType("image/png");
		TotpUtil.createQRCode(response.getOutputStream(), secretUrl, size, size);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/localauth/users/{id}/password",method={RequestMethod.POST})
	public void resetPassword(@PathVariable String id, @RequestParam(required=false) String token, @RequestParam(required=false) String newPassword) throws AuthException {
		AuthUtils.checkAdmin();
		if(newPassword==null) {
			localAuthUserService.resetPassword(id);
		} else {
			localAuthUserService.setPassword(id, newPassword);
		}
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/localauth/users/{id}",method={RequestMethod.DELETE})
	public void delete(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		localAuthUserService.delete(id);
	}
	
	
}
