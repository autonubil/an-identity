package com.autonubil.identity.ldapotp.impl.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.ldap.api.UnsupportedOperation;
import com.autonubil.identity.ldapotp.api.OtpToken;
import com.autonubil.identity.ldapotp.impl.services.OtpTokenService;

@RestController
@RequestMapping("/autonubil")
public class OtpTokenController {
	
	@Autowired
	private OtpTokenService otpTokenService;

	@RequestMapping(value = "/api/ldapotp/mytokens", method = { RequestMethod.GET })
	public List<OtpToken> list() throws AuthException, UnsupportedOperation {
		AuthUtils.checkLoggedIn();
		return otpTokenService.list(IdentityHolder.get().getUser().getSourceId(), IdentityHolder.get().getUser().getId());
	}

	@RequestMapping(value = "/api/ldapotp/mytokens", method = { RequestMethod.POST })
	public OtpToken create(@RequestBody OtpToken token) throws AuthException, UnsupportedOperation {
		AuthUtils.checkLoggedIn();
		return otpTokenService.create(IdentityHolder.get().getUser().getSourceId(), IdentityHolder.get().getUser().getId(),token);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/ldapotp/mytokens/{tokenId}", method = { RequestMethod.DELETE })
	public void delete(@PathVariable String tokenId) throws AuthException, UnsupportedOperation {
		AuthUtils.checkLoggedIn();
		otpTokenService.delete(IdentityHolder.get().getUser().getSourceId(), IdentityHolder.get().getUser().getId(),tokenId);
	}
	
	@RequestMapping(value = "/api/ldapotp/tokens/{connectionId}/{userId}", method = { RequestMethod.GET })
	public List<OtpToken> list(@PathVariable String connectionId, @PathVariable String userId) throws AuthException, UnsupportedOperation {
		AuthUtils.checkAdmin();
		return otpTokenService.list(connectionId,userId);
	}
	
	@RequestMapping(value = "/api/ldapotp/tokens/{connectionId}/{userId}", method = { RequestMethod.POST })
	public OtpToken create(@PathVariable String connectionId, @PathVariable String userId, @RequestBody OtpToken token) throws AuthException, UnsupportedOperation {
		AuthUtils.checkAdmin();
		return otpTokenService.create(connectionId,userId,token);
	}

	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/ldapotp/tokens/{connectionId}/{userId}/{tokenId}", method = { RequestMethod.DELETE })
	public void delete(@PathVariable String connectionId, @PathVariable String userId, @PathVariable String tokenId) throws AuthException, UnsupportedOperation {
		AuthUtils.checkAdmin();
		otpTokenService.delete(connectionId,userId,tokenId);
	}
	
}
