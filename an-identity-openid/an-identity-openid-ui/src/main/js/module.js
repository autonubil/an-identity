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


