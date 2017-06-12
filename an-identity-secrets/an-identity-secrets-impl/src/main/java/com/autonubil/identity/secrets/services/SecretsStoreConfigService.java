package com.autonubil.identity.secrets.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonubil.identity.secrets.api.SecretsProvider;
import com.autonubil.identity.secrets.entities.SecretsStore;

@Service
public class SecretsStoreConfigService {

	
	@Autowired
	List<SecretsProvider> secretProviders;
	
	public List<SecretsProvider> listSecretProviders(String search) {
		// TODO Auto-generated method stub
		return null;
	}
	
	


	private List<SecretsProvider> secretsProviders;

	@Autowired
	protected void setOvpnServerConfigService(List<SecretsProvider> ovpnServerConfigServices) {
		this.secretsProviders = ovpnServerConfigServices;
	}

	public List<SecretsProvider> listSecretsProviders() {
		return this.secretsProviders;
	}

	public List<SecretsStore> listSecretStores(String search) {
		List<com.autonubil.identity.secrets.entities.SecretsStore> result = new ArrayList<>();
		for (SecretsProvider configService : this.listSecretsProviders()) {

			SecretsStore p = new SecretsStore();
			p.setId(configService.getId());
			p.setName(configService.getName());
			p.setDescription(configService.getDescription());
			if ((search == null || search.length() == 0) || (p.getName().contains(search)
					|| p.getDescription().contains(search) || p.getId().equals(search)  )) {
				result.add(p);
			}

		}
		return result;
	}


}
