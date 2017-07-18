angular.module("autonubil-intranet").requires.push("autonubil-intranet-auth");

angular.module("autonubil-intranet-auth", [ "angular-plugin", "restangular", "angularSpinner"] );

angular.module("autonubil-intranet-auth")
 .factory('authErrorInterceptor', function($q,  $location) {
	return {
		responseError : function(rejection) {
			if(rejection.status == 401) {
				if( (rejection.data.path == undefined) || (  (rejection.data.path != undefined) && !rejection.data.path.endsWith("/api/authentication/authenticate")) ) {
					console.log(rejection);
					angular.module("autonubil-intranet-auth").update();
				}
				if ($location.path() == "/auth/login") {
					// already on login page
					return;
				}
				
				if ($location.path() == "/welcome" || $location.path() == "/auth/dashboard")
					angular.module("autonubil-intranet-auth").goto("/auth/login");
				else
					$location.url("/auth/login?return_url="+ encodeURIComponent( $location.absUrl() )  );
			} else if(rejection.status == 403) {
				angular.module("autonubil-intranet-auth").goto("/auth/errors/accessDenied");
			}  
			return $q.reject(rejection);
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
	
	PluginMenuService.addItem("","/auth/dashboard",{title:"Welcome",visible:false}, {
		controller : "DashboardController",
		templateUrl: "auth/templates/dashboard.html"
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
	var adminMenuState = {title:"Admin", visible : false, active: true};
	PluginMenuService.addItem("/main","/admin", adminMenuState, {});

	
	// status component
	var status = { visible : true, title : "Status", templateUrl : "auth/templates/status.html" };
	PluginComponentService.addItem("/topRight",status);
	
	// notifications component
	var notifications = {
			visible : true,
			defaultItem: true,
			title : "Notifications",
			id: "notifications",
			templateUrl : "auth/templates/notifications.html" 
		};
		
	PluginComponentService.addItem("/dashboard",notifications);
	
	var updateVisibility = function(status) {
		adminMenuState.visible = status.loggedIn && status.admin;
	}
	
	
	$rootScope.$on("authChanged", function(e,status) {
		updateVisibility(status);
		if (!status.loggedIn && ($location.path() != '/auth/login') )
			$location.path('/auth/login');
		
	});

	
	updateVisibility(AuthService.getAuthStatus());
	
});
