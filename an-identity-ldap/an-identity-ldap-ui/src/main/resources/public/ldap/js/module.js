angular.module("autonubil-intranet").requires.push("autonubil-intranet-ldap");

angular.module("autonubil-intranet-ldap", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-ldap")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for ldap ... ");
	
	PluginMenuService.addItem("/main/admin", "/ldap", {
		visible: true,
		title : "LDAP Servers"
	}, {
		controller : "LdapConfigListController",
		templateUrl : "ldap/templates/ldap_configs.html" 
	});

	PluginMenuService.addRoute("/main/admin/ldap/:id", {
		controller : "LdapConfigEditController",
		templateUrl : "ldap/templates/ldap_config.html" 
	});

});

angular.module("autonubil-intranet-ldap")
.controller("LdapConfigEditController", function($scope,AuthService,LdapConfigService,LdapConfigFieldService,$routeParams) {

	$scope.changed = false;
	
	$scope.enablePassword = false;
	$scope.password = "";
	$scope.status = { error: false, success : false};

	$scope.update = function() {
		LdapConfigFieldService.list(
				$routeParams.id,
				{},
				function(fields){
					$scope.fields = fields;
				},
				function(response){
				}
		);
		LdapConfigService.get($routeParams.id,function(ldapConfig){
			$scope.ldapConfig = ldapConfig;
		});
		
		LdapConfigService.getTypes(function(types) {
			$scope.types = types;
		});
		
	};


	$scope.save = _.debounce(function() {
		LdapConfigService.save($scope.ldapConfig,function(ldapConfig) {
			$scope.status = { error: false, success : false};
			$scope.ldapConfig = ldapConfig;
			$scope.changed = false;
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.test = function() {
		$scope.status = { error: false, success : false};
		LdapConfigService.test(
			$routeParams.id,
			function(result) {
				$scope.status = result;
				$scope.status.error = !$scope.status.success; 
				console.log($scope.status);
			});
	};
	
	$scope.setPassword = function() {
		console.log("setting password");
		LdapConfigService.setPassword($routeParams.id,$scope.password, function() {
			console.log("set password reset");
			$scope.password = "";
			$scope.enablePassword = false;
		});
	};
	
	
	$scope.addField = function() {
		$scope.fieldError = undefined;
		LdapConfigFieldService.save(
				$routeParams.id,
				$scope.field,
				function(field) {
					$scope.update();
				},
				function(response) {
					$scope.fieldError = response;
				}
		);
	}
	
	$scope.deleteField = function(sourceId, id) {
		LdapConfigFieldService.deleteField(sourceId,id,$scope.update);
	}

	
	$scope.update();
	
})
angular.module("autonubil-intranet-ldap")
.service("LdapConfigFieldService", function(Restangular,$location, $interval) {
	
	return {
		list : function(sourceId, params, success,error) {
			return Restangular.all("autonubil/api/ldapconfig/configs/"+sourceId+"/fields").getList(params).then(success,error);
		},
		save : function(sourceId, field, success, error) {
			field.sourceId = sourceId;
			if(field.id) {
				return config.put().then(success,error);
			} else {
				return Restangular.one("autonubil/api/ldapconfig/configs/"+sourceId+"/fields").customPOST(field).then(success);
			}
		},
		deleteField : function(sourceId,id,success) {
			return Restangular.all("autonubil/api/ldapconfig/configs/"+sourceId+"/fields/"+id).customDELETE().then(success);
		}
	};
	
});

angular.module("autonubil-intranet-ldap")
.controller("LdapConfigListController", function($scope,AuthService,LdapConfigService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		LdapConfigService.getList($scope.search, function(ldapConfigs){
			$scope.ldapConfigs = ldapConfigs;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.remove = function(id) {
		LdapConfigService.remove(id, function() {
			$scope.update();
		});
		
	}

	$scope.add = function() {
		LdapConfigService.save({"name":$scope.search.search},function(lc) {
			$location.path($location.path()+"/"+lc.id);
		});
	};
	
	$scope.update();
	
	
})
angular.module("autonubil-intranet-ldap")
.service("LdapConfigService", function(Restangular,$location, $interval) {
	
	return {
		getTypes : function(success) {
			return Restangular.all("autonubil/api/ldapconfig/types").getList().then(success);
		},
		getList : function(params, success) {
			return Restangular.all("autonubil/api/ldapconfig/configs").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/ldapconfig/configs",id).get().then(success);
		},
		add : function(user,success) {
			return Restangular.all("autonubil/api/ldapconfig/configs").post(user).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/ldapconfig/configs",id).remove().then(success);
		},
		save : function(config,success) {
			if(config.id) {
				return config.put().then(success);
			} else {
				return Restangular.one("autonubil/api/ldapconfig/configs").customPOST(config).then(success);
			}
		},
		test : function(configId,success,error) {
			return Restangular.one("autonubil/api/ldapconfig/configs/"+configId).customPOST({}).then(success,error);
		},
		setPassword : function(id,password,success) {
			return Restangular.all("autonubil/api/ldapconfig/configs/"+id+"/password").customPUT("","",{"password":password}).then(success);
		}
	};
	
});
