package com.autonubil.identity.secrets.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.secrets.entities.SecretsStore;
import com.autonubil.identity.secrets.services.SecretsStoreConfigService;

@RestController
@RequestMapping("/autonubil")
public class SecretsProviderController {

	@Autowired
	SecretsStoreConfigService secretsProviderConfigService;
	
	@RequestMapping(value = "/api/secrets/stores", method = RequestMethod.GET)
	public List<SecretsStore> listSecrets(@RequestParam String search) throws AuthException {
		AuthUtils.checkAdmin();
		return secretsProviderConfigService.listSecretStores(search);
	}

	
}
