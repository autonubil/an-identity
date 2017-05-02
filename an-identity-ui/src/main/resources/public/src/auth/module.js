angular.module("autonubil-intranet").requires.push("autonubil-intranet-auth");

angular.module("autonubil-intranet-auth", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-auth")
.factory('authErrorInterceptor', function($q) {
	return {
		responseError : function(rejection) {
			console.log("error in ajax: "+rejection.status); 
			if(rejection.status == 401) {
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
.run(function(PluginMenuService, PluginComponentService, $location) {

	PluginMenuService.addRoute("/auth/login", {
		controller : "LoginController",
		templateUrl : "auth/templates/login.html" 
	});

	PluginMenuService.addRoute("/auth/errors/accessDenied", {
		controller : function() {},
		templateUrl : "auth/templates/accessDenied.html" 
	});

	// admin menu
	angular.module("autonubil-intranet-auth").adminMenuState = {title:"Admin", visible : true, active: true};
	PluginMenuService.addItem("/main","/admin",angular.module("autonubil-intranet-auth").adminMenuState, {});

	angular.module("autonubil-intranet-auth").goto = function(path) {
		if($location.path()!=path) {
			console.log($location.path()+"!="+path);
			$location.path(path);
		}
	};
	
	var status = {
		visible : true,
		title : "Status",
		templateUrl : "auth/templates/status.html" 
	};
	
	var notifications = {
		visible : true,
		title : "Notifications",
		templateUrl : "auth/templates/notifications.html" 
	};
	
	
	console.log("add: ",status);
	
	PluginComponentService.addItem("/topRight",status);
	
	PluginComponentService.addItem("/dashboard",notifications);
	
});
