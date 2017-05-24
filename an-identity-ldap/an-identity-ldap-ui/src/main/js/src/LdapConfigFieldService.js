angular.module("autonubil-intranet-ldap")
.service("LdapConfigFieldService", function(Restangular,$location, $interval) {
	
	return {
		list : function(sourceId, params, success,error) {
			return Restangular.all("autonubil/api/ldapconfig/configs/"+sourceId+"/fields").getList(params).then(success,error);
		},
		save : function(sourceId, field, success, error) {
			field.sourceId = sourceId;
			if(field.id) {
				return config.put().then(success,error);
			} else {
				return Restangular.one("autonubil/api/ldapconfig/configs/"+sourceId+"/fields").customPOST(field).then(success);
			}
		},
		deleteField : function(sourceId,id,success) {
			return Restangular.all("autonubil/api/ldapconfig/configs/"+sourceId+"/fields/"+id).customDELETE().then(success);
		}
	};
	
});
