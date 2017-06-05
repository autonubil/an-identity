angular.module("autonubil-intranet-myovpns")
.service("MyOvpnsService", function(Restangular,$location) {
	
	return {
		getUsers : function(params, success) {
			return Restangular.all("autonubil/api/authentication").customGET("authenticate").then(success);
		},
		getVpns : function(params,success) {
			return Restangular.all("autonubil/api/ovpn/my_vpns").getList(params).then(success);
		},
	};
	
});