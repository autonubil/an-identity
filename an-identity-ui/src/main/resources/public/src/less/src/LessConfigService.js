angular.module("autonubil-intranet-less")
.service("LessConfigService", function(Restangular,$location, $interval) {
	return {
		get : function(success) {
			return Restangular.one("autonubil/api/less/bootstrap").get().then(success);
		},
		put : function(config,success) {
			config.put().then(success);
		}
	};
});
