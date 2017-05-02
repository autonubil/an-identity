package com.autonubil.identity.ldap.impl.controllers;

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
import com.autonubil.identity.ldap.api.LdapConnectionType;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.impl.services.LdapCustomFieldConfigService;
import com.autonubil.identity.ldap.impl.services.LdapConfigServiceImpl;

@RestController
@RequestMapping("/autonubil")
public class LdapConfigController {

	@Autowired
	private LdapConfigServiceImpl ldapConfigService;

	@Autowired
	private LdapCustomFieldConfigService ldapConfigFieldService;
	
	@RequestMapping(value = "/api/ldapconfig/types", method = { RequestMethod.GET })
	public List<LdapConnectionType> getTypes() throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigService.getConnectionTypes();
	}

	@RequestMapping(value = "/api/ldapconfig/configs", method = { RequestMethod.GET })
	public List<LdapConfig> list() throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigService.list(null, null, null);
	}

	@RequestMapping(value = "/api/ldapconfig/configs", method = { RequestMethod.POST })
	public LdapConfig create(@RequestBody LdapConfig config) throws AuthException {
		AuthUtils.checkAdmin();
		config.setId(null);
		return ldapConfigService.save(config);
	}

	@RequestMapping(value = "/api/ldapconfig/configs/{id}", method = { RequestMethod.GET })
	public LdapConfig get(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigService.get(id);
	}

	@RequestMapping(value="/api/ldapconfig/configs/{id}",method={RequestMethod.POST})
	public TestResult test(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		TestResult out = new TestResult();
		try {
			ldapConfigService.connect(id);
			out.setSuccess(true);
		} catch (Throwable e) {
			while(e.getCause()!=null) {
				e = e.getCause();
			}
			out.setException(e);
		}
		return out;
	}

	@RequestMapping(value = "/api/ldapconfig/configs/{id}", method = { RequestMethod.PUT })
	public LdapConfig update(@PathVariable String id, @RequestBody LdapConfig config) throws AuthException {
		AuthUtils.checkAdmin();
		config.setId(id);
		return ldapConfigService.save(config);
	}

	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/ldapconfig/configs/{id}/password", method = { RequestMethod.PUT })
	public void setPassword(@PathVariable String id, @RequestParam String password) throws AuthException {
		AuthUtils.checkAdmin();
		ldapConfigService.setPassword(id, password);
	}

	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/ldapconfig/configs/{id}", method = { RequestMethod.DELETE })
	public void delete(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		ldapConfigService.delete(id);
	}

	@RequestMapping(value = "/api/ldapconfig/configs/{sourceId}/fields", method = { RequestMethod.GET })
	public List<LdapCustomsFieldConfig> listFields(@PathVariable String sourceId, @RequestParam(required=false) List<String> objectClasses) throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigFieldService.list(null, sourceId, objectClasses, 0, 100);
	}
	
	@RequestMapping(value = "/api/ldapconfig/configs/{sourceId}/fields", method = { RequestMethod.POST })
	public LdapCustomsFieldConfig addField(@PathVariable String sourceId, @RequestBody LdapCustomsFieldConfig field) throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigFieldService.save(field);
	}
	
	@RequestMapping(value = "/api/ldapconfig/configs/{sourceId}/fields/{id}", method = { RequestMethod.POST })
	public LdapCustomsFieldConfig addField(@PathVariable String sourceId, @PathVariable String id, @RequestBody LdapCustomsFieldConfig field) throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigFieldService.save(field);
	}
	
	@RequestMapping(value = "/api/ldapconfig/configs/{sourceId}/fields/{id}", method = { RequestMethod.GET })
	public LdapCustomsFieldConfig getField(@PathVariable String sourceId, @PathVariable String id, @RequestBody LdapCustomsFieldConfig field) throws AuthException {
		AuthUtils.checkAdmin();
		return ldapConfigFieldService.get(sourceId,id);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/api/ldapconfig/configs/{sourceId}/fields/{id}", method = { RequestMethod.DELETE })
	public void deleteField(@PathVariable String sourceId, @PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		ldapConfigFieldService.delete(sourceId,id);
	}
	
	public class TestResult {

		private Throwable exception;
		private boolean success;

		
		
		public Throwable getException() {
			return exception;
		}

		public void setException(Throwable exception) {
			this.exception = exception;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

	}

}
