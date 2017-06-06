angular.module("autonubil-intranet").requires.push("autonubil-intranet-ovpn");
angular.module("autonubil-intranet-ovpn", [ "angular-plugin", "restangular","autonubil-intranet-auth"]);
angular.module("autonubil-intranet-ovpn")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	PluginMenuService.addItem("/main/admin", "/ovpn/vpns", {
		title : "OpenVPN",
		visible:true
	}, {
		controller : "VpnListController",
		templateUrl : "ovpn/templates/vpns.html" 
	});
	
	
	PluginMenuService.addRoute("/main/admin/ovpn/vpns/:id", {
		controller : "VpnEditController",
		templateUrl : "ovpn/templates/vpn.html" 
	});
	
	

	

});


angular.module("autonubil-intranet").requires.push("autonubil-intranet-myovpns");
angular.module("autonubil-intranet-myovpns", [ "angular-plugin", "restangular","autonubil-intranet-auth", "autonubil-intranet-me" ]);

angular.module("autonubil-intranet-myovpns")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {
 
	
	myVpns= {
			visible : true,
			status : "My VPNs",
			templateUrl : "ovpn/templates/my_vpns.html"
	};
	
	
	PluginComponentService.addItem("/me",myVpns); 
	PluginComponentService.addItem("/dashboard",myVpns); 

});

angular.module("autonubil-intranet-myovpns")
.controller("MyOvpnsController", function($scope, MeService, MyOvpnsService, AuthService, $location) {
	
	$scope.search =  "";

	$scope.updateVpns = _.debounce(function() {
		MyOvpnsService.getVpns({search:$scope.search},function(vpns){
			$scope.vpns = vpns;
		});
	},250);
	
	$scope.updateVpns();
	
});

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
		
		getServerConfigProviderList : function(search,success) {
			return Restangular.all("autonubil/api/ovpn/server_config_providers").getList({search: search}).then(success);
		},
		getClientConfigProviderList : function(search,success) {
			return Restangular.all("autonubil/api/ovpn/client_config_providers").getList({search: search}).then(success);
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
.controller("VpnEditController", function($scope,AuthService,OvpnService,LdapConfigService,LdapGroupService,$routeParams) {

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
		OvpnService.get($routeParams.id,function(source){
			$scope.source = source;
		});
		
		OvpnService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});

		
		OvpnService.getClientConfigProviderList("", function(clientConfigProviders){
			console.log("clientConfigProviders: "+clientConfigProviders.length);
			$scope.clientConfigProviders = clientConfigProviders;
		});
		
		OvpnService.getServerConfigProviderList("", function(serverConfigProviders){
			console.log("serverConfigProviders: "+serverConfigProviders.length);
			$scope.serverConfigProviders = serverConfigProviders;
		});
	};
	

	$scope.save = _.debounce(function() {
		OvpnService.save($scope.source,function(source) {
			$scope.source = source;
			$scope.changed = false;
			// $scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save(true);
	};
	
	
	$scope.update();
	
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
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		OvpnService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OvpnService.addPermission(
				$routeParams.id,
				{
					vpnId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
})
.directive('jsonText', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {            
          function into(input) {
            return JSON.parse(input);
          }
          function out(data) {
            return JSON.stringify(data);
          }
          ngModel.$parsers.push(into);
          ngModel.$formatters.push(out);

        }
    };
});

angular.module("autonubil-intranet-ovpn")
.controller("VpnListController", function($scope,AuthService,OvpnService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getList($scope.search, function(vpns){
			$scope.vpns= vpns;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN source ... ");
		var x = { name : $scope.search.search, description: $scope.search.search, serverConfiguration: "{}", serverConfigurationProvider: "default", clientConfiguration: "{}", clientConfigurationProvider:"default" };
		OvpnService.add(
				x,
				function(ovpn){
					console.log("OpenVPN source saved... ",ovpn);
					$location.path("/main/admin/ovpn/vpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})