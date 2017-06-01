angular.module("autonubil-intranet-ovpn")
.service("OvpnService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/ovpn/vpns").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/ovpn/vpns",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/ovpn/vpns").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/ovpn/vpns",id).remove().then(success);
		},
		save : function(ovpn,success) {
			return ovpn.put().then(success);
		},
		
		
		getSourceList : function(params, success) {
			return Restangular.all("autonubil/api/ovpn/sources").getList(params).then(success);
		},
		getSource : function(id,success) {
			return Restangular.one("autonubil/api/ovpn/sources",id).get().then(success);
		},
		addSource : function(config,success) {
			return Restangular.all("autonubil/api/ovpn/sources").post(config).then(success);
		},
		removeSource : function(id,success) {
			return Restangular.one("autonubil/api/ovpn/vpsourcesns",id).remove().then(success);
		},
		saveSource : function(source,success) {
			return source.put().then(success);
		},
		
		listPermissions : function(id,success) {
			return Restangular.all("autonubil/api/ovpn/vpns/"+id+"/permissions").getList({}).then(success);
		},
		addPermission : function(ovpnId,permission,success) {
			return Restangular.all("autonubil/api/ovpn/vpns/"+ovpnId+"/permissions").customPOST(permission).then(success);
		},
		deletePermission : function(ovpnId,sourceId,groupId,success) {
			return Restangular.all("autonubil/api/ovpn/vpns/"+ovpnId+"/permissions").customDELETE("",{sourceId:sourceId,groupId:groupId}).then(success);
		}
		
		 
	};
	
});
