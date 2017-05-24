
angular.module("autonubil-intranet").requires.push("autonubil-intranet-less");

angular.module("autonubil-intranet-less", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-less")
.run(function(PluginMenuService, $location) {

	console.log("register menu items for less ... ");
	
	PluginMenuService.addItem("/main/admin", "/less", {
		visible: true,
		title : "Less Config"
	}, {
		controller : "LessConfigController",
		templateUrl : "less/templates/less.html" 
	});

	PluginMenuService.addItem("/main/admin", "/css", {
		visible: true,
		title : "CSS Config"
	}, {
		controller : "CssListController",
		templateUrl : "less/templates/css_configs.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/css/:id", {
		controller : "CssEditController",
		templateUrl : "less/templates/css_config.html" 
	});
	
	
});
