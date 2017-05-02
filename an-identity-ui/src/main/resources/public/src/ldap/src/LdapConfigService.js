angular.module("autonubil-intranet-ldap")
.service("LdapConfigService", function(Restangular,$location, $interval) {
	
	return {
		getTypes : function(success) {
			return Restangular.all("autonubil/api/ldapconfig/types").getList().then(success);
		},
		getList : function(params, success) {
			return Restangular.all("autonubil/api/ldapconfig/configs").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/ldapconfig/configs",id).get().then(success);
		},
		add : function(user,success) {
			return Restangular.all("autonubil/api/ldapconfig/configs").post(user).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/ldapconfig/configs",id).remove().then(success);
		},
		save : function(config,success) {
			if(config.id) {
				return config.put().then(success);
			} else {
				return Restangular.one("autonubil/api/ldapconfig/configs").customPOST(config).then(success);
			}
		},
		test : function(configId,success,error) {
			return Restangular.one("autonubil/api/ldapconfig/configs/"+configId).customPOST({}).then(success,error);
		},
		setPassword : function(id,password,success) {
			return Restangular.all("autonubil/api/mail/configs/"+id+"/password").customPUT("","",{"password":password}).then(success);
		}
	};
	
});
