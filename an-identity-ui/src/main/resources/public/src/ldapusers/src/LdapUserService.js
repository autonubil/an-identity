angular.module("autonubil-intranet-ldap")
.service("LdapUserService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(connectionId, params, success) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users").getList(params).then(success);
		},
		setUserExpiry : function(connectionId, userId, date, success, error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users/"+userId+"/expiration_user").customPUT({},"",{date:date}).then(success,error);
		},
		setPasswordExpiry : function(connectionId, userId, date, success, error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users/"+userId+"/expiration_password").customPUT({},"",{date:date}).then(
					function() {
						console.log("success");
						success();
					},
					function() {
						console.log("error");
						success();
					}
			);
		},
		get : function(connectionId,userId,success) {
			return Restangular.one("autonubil/api/ldapusers/"+connectionId+"/users",userId).get().then(
					function(user) {
						var a = [];
						for (var key in user.attributes) {
							a.push({ key : key, value : user.attributes[key] })
						}
						user.attributes = a;
						success(user);
					}
			);
		},
		create : function(connectionId,user,success,error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users").post(user).then(success,error);
		},
		save : function(user,success,error) {
			user.attributes = undefined;
			user.put().then(success,error);
		},
		getGroups : function(connectionId,userId,success) {
			return Restangular.one("autonubil/api/ldapusers/"+connectionId+"/users/"+userId,"groups").get().then(success);
		},
		addUserToGroup : function(connectionId,userId,groupId,success,error) {
			
		},
		removeUserFromGroup : function(user,groupId,success,error) {
			Restangular.one("autonubil/api/ldapusers/"+user.sourceId+"/users/"+user.id,"groups").customDELETE("",{groupId: groupId}).then(success,error);
		},
		addUserToGroup : function(user,groupId,success,error) {
			user.post("groups",{},{groupId: groupId}).then(success,error);
		}
		
	};
	
});
