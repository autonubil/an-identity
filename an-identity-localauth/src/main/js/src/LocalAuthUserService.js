angular.module("autonubil-intranet-localauth")
.service("LocalAuthUserService", function(Restangular,$location, $interval) {
	
	return {
		resetPassword : function(id, success, error) {
			return Restangular.one("autonubil/api/mail/configs/"+id+"/password").customPOST({}).then(success,error);
		},
		resetOTP : function(id, success) {
			return Restangular.one("autonubil/api/localauth/users/"+id+"/otp").customPUT({}).then(success);
		},
		getList : function(params, success) {
			return Restangular.all("autonubil/api/localauth/users").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/localauth/users",id).get().then(success);
		},
		add : function(user,success) {
			return Restangular.all("autonubil/api/localauth/users").post(user).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/localauth/users",id).remove().then(success);
		},
		save : function(user,success) {
			return user.put().then(success);
		}
	};
	
});
