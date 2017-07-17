angular.module("autonubil-intranet-openid")
.service("OpenidConnectService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/openid/applications").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/openid/application",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/openid/application").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/openid/application",id).remove().then(success);
		},
		save : function(application, success) {
			return application.put().then(success);
		},
		
		getMeta : function(success) {
			return Restangular.all(".well-known/openid-configuration").customGET().then(success);
		},
		
		openApplication : function(success) {
			return Restangular.all(".well-known/openid-configuration").customGET().then(success);
		},
		
		
		listPermissions : function(id,success) {
			return Restangular.all("autonubil/api/openid/application/"+id+"/permissions").getList({}).then(success);
		},
		addPermission : function(ovpnId,permission,success) {
			return Restangular.all("autonubil/api/openid/application/"+ovpnId+"/permissions").customPOST(permission).then(success);
		},
		deletePermission : function(ovpnId,sourceId,groupId,success) {
			return Restangular.all("autonubil/api/openid/application/"+ovpnId+"/permissions").customDELETE("",{sourceId:sourceId,groupId:groupId}).then(success);
		}
		
		
		 
	};
	
});
