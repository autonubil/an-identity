
angular.module("autonubil-intranet").requires.push("autonubil-intranet-ovpn");

angular.module("autonubil-intranet-ovpn", [ "angular-plugin", "restangular","autonubil-intranet-auth", "autonubil-intranet-me" ]);
angular.module("autonubil-intranet-ovpn")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	PluginMenuService.addItem("/main/admin", "/vpn_sources", {
		title : "OpenVPN Sources",
		visible:true
	}, {
		controller : "SourceListController",
		templateUrl : "ovpn/templates/sources.html" 
	});
	
	PluginMenuService.addItem("/main/admin", "/vpn_permission", {
		title : "OpenVPN VPNs",
		visible:true
	}, {
		controller : "OvpnListController",
		templateUrl : "ovpn/templates/ovpns.html" 
	});
	
	
	PluginMenuService.addRoute("/main/admin/apps/:id", {
		controller : "OvpnEditController",
		templateUrl : "ovpn/templates/ovpn.html" 
	});
 
	
	
	myApps = {
			visible : true,
			status : "My VPNs",
			templateUrl : "ovpn/templates/my_vpns.html"
		};
		
		
		PluginComponentService.addItem("/me",myApps); 
		PluginComponentService.addItem("/dashboard",myApps); 
	
});

angular.module("autonubil-intranet-ovpn")
.controller("MyVpnsController", function($scope,MeService,AuthService,$location) {
	
	$scope.search =  "";

	$scope.updateApps = _.debounce(function() {
		OvpnService.getVpns({search:$scope.search},function(vpns){
			$scope.vpns = vpns;
		});
	},250);
	
	$scope.updateVpns();
	
});

angular.module("autonubil-intranet-ovpn")
.controller("OvpnEditController", function($scope,AuthService,OvpnService,LdapConfigService,LdapGroupService,$routeParams) {

	$scope.changed = false;
	
	$scope.newPermission = {
			source : {},
			group : {},
			name : "",
	};

	LdapConfigService.getList({},function(configs){
		$scope.configs=configs;
	});

	
	$scope.update = function() {
		OvpnService.get($routeParams.id,function(app){
			$scope.app = app;
		});
		OvpnService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});
	};
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.source.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.source.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	

	$scope.save = _.debounce(function() {
		OvpnService.save($scope.app,function(app) {
			$scope.app = app;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		OvpnService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OvpnService.addPermission(
				$routeParams.id,
				{
					appId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
	$scope.uploadSuccess = function() {
		s = $("#icon").attr("src")+"?"+Math.random();
		console.log("image uploaded ... new image: "+s);
		$("#icon").attr("src", s);
	}
	
	$scope.update();
	
})
angular.module("autonubil-intranet-ovpn")
.controller("OvpnListController", function($scope,AuthService,OvpnService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getList($scope.search, function(ovpns){
			$scope.ovpns = ovpns;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN ... ");
		var x = { name : $scope.search.search, description: $scope.search.search  };
		OvpnService.add(
				x,
				function(ovpn){
					console.log("OpenVPN saved... ",ovpn);
					$location.path("/main/admin/ovpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})
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

angular.module("autonubil-intranet-ovpn")
.controller("SourceEditController", function($scope,AuthService,OvpnService,LdapConfigService,LdapGroupService,$routeParams) {

	$scope.changed = false;
	
	$scope.newPermission = {
			source : {},
			group : {},
			name : "",
	};

	LdapConfigService.getList({},function(configs){
		$scope.configs=configs;
	});

	
	$scope.update = function() {
		OvpnService.get($routeParams.id,function(app){
			$scope.app = app;
		});
		OvpnService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});
	};
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.source.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.source.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	

	$scope.save = _.debounce(function() {
		OvpnService.save($scope.app,function(app) {
			$scope.app = app;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		OvpnService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OvpnService.addPermission(
				$routeParams.id,
				{
					appId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
	$scope.uploadSuccess = function() {
		s = $("#icon").attr("src")+"?"+Math.random();
		console.log("image uploaded ... new image: "+s);
		$("#icon").attr("src", s);
	}
	
	$scope.update();
	
})
angular.module("autonubil-intranet-ovpn")
.controller("SourceListController", function($scope,AuthService,OvpnService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getSourceList($scope.search, function(sources){
			$scope.sources = sources;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN source ... ");
		var x = { name : $scope.search.search, description: $scope.search.search, configuration: "{}", serverConfigurationProvider: "default", clientConfigurationProvider:"default" };
		OvpnService.addSource(
				x,
				function(ovpn){
					console.log("OpenVPN source saved... ",ovpn);
					$location.path("/main/admin/ovpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})