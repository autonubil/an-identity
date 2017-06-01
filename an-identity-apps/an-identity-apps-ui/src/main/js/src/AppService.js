angular.module("autonubil-intranet-apps")
.service("AppService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/apps/apps").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/apps/apps",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/apps/apps").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/apps/apps",id).remove().then(success);
		},
		save : function(app,success) {
			return app.put().then(success);
		},
		listPermissions : function(id,success) {
			return Restangular.all("autonubil/api/apps/apps/"+id+"/permissions").getList({}).then(success);
		},
		addPermission : function(appId,permission,success) {
			return Restangular.all("autonubil/api/apps/apps/"+appId+"/permissions").customPOST(permission).then(success);
		},
		deletePermission : function(appId,sourceId,groupId,success) {
			return Restangular.all("autonubil/api/apps/apps/"+appId+"/permissions").customDELETE("",{sourceId:sourceId,groupId:groupId}).then(success);
		}
	};
	
});