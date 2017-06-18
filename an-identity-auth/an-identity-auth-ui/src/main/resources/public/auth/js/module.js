angular.module("autonubil-intranet").requires.push("autonubil-intranet-auth");

angular.module("autonubil-intranet-auth", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-auth")
.factory('authErrorInterceptor', function($q) {
	return {
		responseError : function(rejection) {
			if(rejection.status == 401) {
				console.log(rejection);
				if(!rejection.data.path.endsWith("/api/authentication/authenticate")) {
					angular.module("autonubil-intranet-auth").update();
				}
				angular.module("autonubil-intranet-auth").goto("/auth/login");
			} else if(rejection.status == 403) {
				angular.module("autonubil-intranet-auth").goto("/auth/errors/accessDenied");
			}
			return $q.reject(rejection);;
		}
	}
});

angular.module("autonubil-intranet-auth")
.config(function($httpProvider) {
	$httpProvider.interceptors.push("authErrorInterceptor");
});
angular.module("autonubil-intranet-auth")
.run(function(PluginMenuService, PluginComponentService, AuthService, $rootScope, $location) {

	console.log("register menu items for auth ... ");

	PluginMenuService.addRoute("/auth/login", {
		controller : "LoginController",
		templateUrl : "auth/templates/login.html" 
	});

	PluginMenuService.addRoute("/auth/errors/accessDenied", {
		controller : function() {},
		templateUrl : "auth/templates/accessDenied.html" 
	});

	angular.module("autonubil-intranet-auth").goto = function(path) {
		console.log("updating auth status");
		if($location.path()!=path) {
			console.log($location.path()+"!="+path);
			$location.path(path);
		}
	};
	
	angular.module("autonubil-intranet-auth").update = function() {
		console.log("updating auth status");
		console.log(AuthService);
		AuthService.updateAuth();
	};
	
	// admin menu
	var adminMenuState = {title:"Admin", visible : true, active: true};
	PluginMenuService.addItem("/main","/admin", adminMenuState, {});

	
	// status component
	var status = {
			visible : true,
			title : "Status",
			templateUrl : "auth/templates/status.html" 
		};
	PluginComponentService.addItem("/topRight",status);
	
	// notifications component
	var notifications = {
			visible : true,
			title : "Notifications",
			templateUrl : "auth/templates/notifications.html" 
		};
		
	PluginComponentService.addItem("/dashboard",notifications);
	
	var updateVisibility = function(status) {
		adminMenuState.visible = status.loggedIn && status.admin;
	}
	
	
	$rootScope.$on("authChanged", function(e,status) {
		updateVisibility(status);
	});

	
	updateVisibility(AuthService.getAuthStatus());
	
	
	
});

angular.module("autonubil-intranet-auth")
.service("AuthService", function(Restangular,$location, $interval, $rootScope) {
	
	var AuthStatus = {
			loggedIn : false,
			admin : false,
			user: {
				name : "anonymous"
			}
	};
	

	var setAuthStatus = function(l,a,n,un,notifications) {
		changed = false;
		if(l!=AuthStatus.loggedIn) {
			AuthStatus.loggedIn = l;
			changed = true;
		}
		if(a!=AuthStatus.admin) {
			AuthStatus.admin = a;
			changed = true;
		}
		if(n!=AuthStatus.user.name) {
			AuthStatus.user.name = n;
			AuthStatus.user.username = un;
			AuthStatus.user.notifications = notifications;
			changed = true;
		}
		console.log ( "Auth changed? "+changed);
		if(changed) {
			$rootScope.$emit("authChanged",AuthStatus);
		}
	} 
	
	
	var updateAuth = function() {
		console.log ( " update Auth ... ");
		Restangular.all("autonubil/api/authentication").customGET("authenticate").then(
				function(e) {
					x = false;
					_.forEach(e.user.groups,function(group){
						if(group.name == "admin") {
							x = true;
						}
					});
					setAuthStatus(true,x,e.user.displayName,e.user.username,e.user.notifications);
				},
				function(e) {
					setAuthStatus(false,false,"anonymous");
				}			
		);
	};
	
	$interval(updateAuth,10000);
	updateAuth();

	return {
		getSources : function(callback) { 
			return Restangular.all("autonubil/api/authentication/sources").getList().then(callback);
		},
		getAuthStatus : function() {
			return AuthStatus;
		},
		
		login : function(credentials,success,error) {
			Restangular.all("autonubil/api/authentication/authenticate").post(credentials).then(
					function(identity) {
						updateAuth();
						success(identity);
					},error);
		}, 
	
		reset : function(reset,success,error) {
			Restangular.all("autonubil/api/authentication/reset").post(reset).then(success,error);
		}, 
		
		logout : function(username,password) {
			Restangular.one("autonubil/api/authentication/authenticate").customDELETE()
			.then(
				function(data) {
					updateAuth();
				},
				function(err) {
					updateAuth();
				}
			);
		},
	
		updateAuth : updateAuth
		
	}
	
});


angular.module("autonubil-intranet-auth")
.controller("LoginController", function($scope,AuthService,$location,$routeParams) {
	
	$scope.status = AuthService.getAuthStatus();

	$scope.config = {
		sourceId : $routeParams.sourceId || "",
		mode : $routeParams.mode || "login",
		username : $routeParams.username || ""
	};

	console.log($scope.config);
	
	$scope.reset = {
			username: $scope.config.username,
			secondFactor: "",
			token: $routeParams.token||"",
			email: $routeParams.email||"",
			oldPassword:"",
			newPassword:""
	};
	
	$scope.credentials = {
			username : $scope.reset.username,
			password : ""
	};
	
	AuthService.getSources(function(sources) {
		if($scope.config.sourceId=="") {
			$scope.config.sourceId = sources[0].sourceId;
		};
		_.forEach(sources,function(e) {
			if(e.sourceId == $scope.config.sourceId) {
				$scope.source = e;
			};
		});
		$scope.sources = sources;
	});
	
	$scope.login = function() {
		$scope.success = undefined;
		$scope.error = undefined;
		$scope.credentials.sourceId = $scope.config.sourceId; 
		AuthService.login($scope.credentials,
				function(user) {
					$scope.status.loggedIn = true;
				},
				function(response) { 
					$scope.error = response;
				}
		);
	};
	
	$scope.resetPassword = function() {
		$scope.success = undefined;
		$scope.error = undefined;
		$scope.reset.sourceId = $scope.config.sourceId; 
		AuthService.reset($scope.reset,
			function() {
				console.log("(controller) reset success!");
				if($scope.config.mode=='reset') {
					$scope.success = "Success! Please check your mail.";
				} else if($scope.config.mode=='resetWithOldPassword') {
					$scope.success = "Success! You can now login with your new password.";
				} else if($scope.config.mode=='resetWithToken') {
					$scope.success = "Success! You can now login with your new password.";
				}
			},
			function(response) { 
				console.log("(controller) reset error!",response);
				$scope.error = response;
			}
		);

	}
	
});


angular.module("autonubil-intranet-auth")
.controller("LoginStatusController", function($scope,AuthService,$rootScope) {
	$scope.status = AuthService.getAuthStatus();
	$rootScope.$on("authChanged", function(e,status) {
		$scope.status = status;
	})
	$scope.logout = function() {
		console.log("logout ... ");
		AuthService.logout();
	}
});


angular.module("autonubil-intranet-auth")
.controller("NotificationController", function($scope,AuthService,$location,$routeParams) {
	
	$scope.status = AuthService.getAuthStatus();
	
});
