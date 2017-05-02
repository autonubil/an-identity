angular.module("autonubil-intranet-ldap")
.service("LdapGroupService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(connectionId, params, success, error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/groups").getList(params).then(success,error);
		},
		get : function(connectionId,groupId,success) {
			return Restangular.one("autonubil/api/ldapusers/"+connectionId+"/groups",groupId).get().then(
					function(user) {
						var a = [];
						for (var key in user.attributes) {
							a.push({ key : key, value : user.attributes[key] })
						}
						user.attributes = a;
					    console.log(user);
						success(user);
					}
			);
		}
	};
	
});
