angular.module("autonubil-intranet-automigrate")
.service("AutoMigrationService", function(Restangular,$location, $interval) {
	
	return {
		list : function(params, success,error) {
			return Restangular.all("autonubil/api/automigrate/configs").getList(params).then(success,error);
		},
		get : function(id, success,error) {
			return Restangular.one("autonubil/api/automigrate/configs",id).get().then(success,error);
		},
		create : function(config, success,error) {
			return Restangular.all("autonubil/api/automigrate/configs").post(config).then(success,error);
		},
		save : function(config, success,error) {
			return config.put().then(success,error);
		},
		remove : function(id, success,error) {
			return Restangular.one("autonubil/api/automigrate/configs",id).remove().then(success,error);
		}
	}
	
})