angular.module("autonubil-intranet-me")
.service("MeService", function(Restangular,$location) {
	
	return {
		getUsers : function(params, success) {
			return Restangular.all("autonubil/api/authentication").customGET("authenticate").then(success);
		},
		getApps : function(params,success) {
			return Restangular.all("autonubil/api/apps/my_apps").getList(params).then(success);
		},
	};
	
	
});
