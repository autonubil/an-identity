angular.module("autonubil-intranet-mail")
.service("MailConfigService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/mail/configs").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/mail/configs",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/mail/configs").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/mail/configs",id).remove().then(success);
		},
		save : function(config,success,error) {
			return config.put().then(success,error);
		},
		setPassword : function(id,password,success,error) { 
			Restangular.all("autonubil/api/mail/configs/"+id+"/password").customPUT("","",{"password":password}).then(success,error);
		}
	};
	
});
