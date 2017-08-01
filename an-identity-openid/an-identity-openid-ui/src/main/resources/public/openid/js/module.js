angular.module("autonubil-intranet").requires.push("autonubil-intranet-openid");
angular.module("autonubil-intranet-openid", [ "angular-plugin", "restangular","autonubil-intranet-auth", "angularSpinner", "checklist-model"]);


angular.module("autonubil-intranet-openid")
.run(function(PluginMenuService) {
	console.log("register menu items for openid... ");
	
	PluginMenuService.addItem("/main/admin", "/oauth/applications", {
		title : "OAuth",
		visible:true
	}, {
		controller : "ApplicationListController",
		templateUrl : "openid/templates/applications.html" 
	});
	
	
	PluginMenuService.addRoute("/main/admin/oauth/application/:id", {
		controller : "ApplicationEditController",
		templateUrl : "openid/templates/application.html" 
	});

	
	PluginMenuService.addRoute("/oauth/authorize", {
		controller : "OpenIdConnectController",
		templateUrl : "openid/templates/authorize.html" 
	});
	
});



angular.module("autonubil-intranet-openid")
 .controller("ApplicationEditController", function($scope, AuthService, OpenidConnectService, LdapConfigService, LdapGroupService, AppService, $routeParams, Restangular ) {
 
	 
	$scope.changed = false;
	
	$scope.newPermission = {
			application : {},
			group : {},
			name : "",
	};

	
 
	$scope.update = function() { 
		OpenidConnectService.get($routeParams.id,function(application){
			$scope.application = application;
		});

		
		if (!$scope.meta) {
			OpenidConnectService.getMeta(function(meta){
				$scope.meta = meta;
			});
				
		}
		
		if (!$scope.permissions) {
			OpenidConnectService.listPermissions($routeParams.id,function(permissions){
				console.log("permssions: "+permissions.length);
				$scope.permissions = permissions;
			});
		}

		if (!$scope.apps) {
			AppService.getList(	$scope.search = { search : "", offset : 0, limit : 5 },  function(apps){
				console.log("apps: "+apps.length);
				$scope.apps =   apps;
			});
		}
		
	};
	
	$scope.testOAuthApp = function(){
		console.log ( "OAuth2:  Getting auth from approval... ");
		params = {
				client_id: $scope.application.id,
				response_type: "code",
				nonce: "test.nonce",
//				state: "an-identity-test",
				scope: $scope.application.scopes.join(' ')
		}; 
		params['_t'] = Date.now();
		
		Restangular.one('oauth').customGET("approve", params).then( function(e) {
			
			// remember the code
			$scope.code = e.code;
			
			//if ($scope.status.loggedIn){
			newUrl = $scope.application.callbackUrl;
			if (newUrl.indexOf('?') > -1) {
				newUrl += "&";
			} else {
				newUrl += "?";
			}
			
			newUrl += "code=" + e.code +"&state="+e.state + "&nonce="+e.nonce;
			if (e.authenticated) {
				console.log("OAuth2: redirecting to "+ newUrl);
				window.location.href = newUrl;
			} 
			// }
		});
	};
	
	
	$scope.save = _.debounce(function() {
		OpenidConnectService.save($scope.application,function(application) {
			$scope.application = application;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save(true);
	};
	
	AuthService.updateAuth();
	
	$scope.update();
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.application.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.application.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	
	$scope.removePermission = function(application,groupId) {
		console.log("remove permission",application,groupId);
		OpenidConnectService.deletePermission($routeParams.id,application,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OpenidConnectService.addPermission(
				$routeParams.id,
				{
					vpnId: $routeParams.id,
					applicationId : $scope.newPermission.application.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.application.name+": "+$scope.newPermission.group.displayName
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
            return JSON.stringify(data,null, 2);
          }
          ngModel.$parsers.push(into);
          ngModel.$formatters.push(out);

        }
    };
})

angular.module("autonubil-intranet-openid")
 .controller("ApplicationListController", function($scope, AuthService, OpenidConnectService, $location) {

	 
	
	 
	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OpenidConnectService.getList($scope.search, function(applications){
			$scope.applications = applications;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OAuth application ... ");
		
		OpenidConnectService.add(
				{ name : $scope.search.search, clienntSigningAlg : "RS256"},
				function(application){
					console.log("OAuth2 application saved... ",application);
					$location.path("/main/admin/oauth/application/"+application.id);
				}
		);
	}
	
	AuthService.updateAuth();
	
	$scope.update();
	
})
angular.module("autonubil-intranet-openid")
.controller("OpenIdConnectController", function($scope, $rootScope, AuthService, Restangular, $routeParams) {
	
	$scope.status = AuthService.getAuthStatus();
	
	$scope.redirect_uri = $routeParams.redirect_uri; 
	$scope.app= {
			id: null,
			prefix: "The ",
			name: "Application"
	}
	$scope.scopes = [];
	
	
	$scope.code = $routeParams.code; 
	
	var update = function() {
	
		console.log ( "OAuth2:  Getting auth from approval... ");
		params = {
				client_id: $routeParams.client_id,
				response_type: $routeParams.response_type	
		};
		
		if ($routeParams.nonce) {
			params['nonce'] = $routeParams.nonce;
		}
		if ($routeParams.state) {
			params['state'] = $routeParams.state;
		}
		if ($routeParams.scope) {
			params['scope'] = $routeParams.scope;
		}
		if ($scope.code) {
			params['code'] = $scope.code
		}
		params['_t'] = Date.now();
		
		Restangular.one('oauth').customGET("approve", params).then( function(e) {
			
			if (e.error) {
				$scope.error = {
						data: {
							message: e.error
						}
				};
				return;
			}
			
			
			
			
			if (e.scopes) {
				$scope.scopes = e.scopes;
			} else {
				$scope.scopes = [];
			}
			
			if (e.linkedApplication) {
				$scope.app = e.linkedApplication;
			} else {
				$scope.app = {
						id: null,
					prefix: "The ",
					name: "Application"
				}
			}
			
			// remember the code
			$scope.code = e.code;
			
			if ($scope.status.loggedIn && $routeParams.redirect_uri){
				var newUrl = $routeParams.redirect_uri;
				if (newUrl.indexOf('?') > -1) {
					newUrl += "&";
				} else {
					newUrl += "?a=b" ;
				}
				
				params = ""
				// newUrl += "code=" + e.code +"&state="+e.state;
				if (e.code)
					params  +=  "&code="+e.code;
				if (e.state)
					params+=  "&state="+e.state;
				if (e.nonce)
					params  +=  "&nonce="+e.nonce;
				
				if (newUrl.indexOf('?') > -1) {
					newUrl += "&"+params;
				} else {
					newUrl += "?"+params ;
				}
				
				$scope.redirect_uri = newUrl;
				if (e.authenticated) {
					// console.log("OAuth2: redirecting to "+ newUrl);
					window.location.href = newUrl;
				} 
			}
		});
		
		
	};
	
	$rootScope.$on("authChanged", function(e,status) {
		update();
		if (!$scope.status.loggedIn){
			console.log ( "OAuth2:  User needs to login first... ");
		}
	});
	update();
	
})
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
