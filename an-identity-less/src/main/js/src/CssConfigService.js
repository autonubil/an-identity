angular.module("autonubil-intranet-less")
.service("CssConfigService", function(Restangular,$location, $interval) {
	return {
		list : function(success) {
			return Restangular.all("autonubil/api/less/css").getList().then(success);
		},
		create : function(config,success) {
			return Restangular.all("autonubil/api/less/css").post(config).then(
				function(config) {
					console.log("config: ",config);
					success(config);
				});
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/less/css",id).get().then(success);
		},
		put : function(config,success) {
			Restangular.one("autonubil/api/less/css",config.id).customPUT(
				{
					id : config.id,
					name : config.name,
					order : config.order,
					rel : config.rel,
					type : config.type,
					href : config.href
				}
			).then(success);
		},
		remove : function(config,success) {
			Restangular.one("autonubil/api/less/css",config.id).remove().then(success);
		}
	};
});
