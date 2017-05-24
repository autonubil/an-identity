
angular.module("autonubil-intranet").requires.push("autonubil-intranet-apps");

angular.module("autonubil-intranet-apps", [ "angular-plugin", "restangular", "autonubil-intranet-ldap", "autonubil-intranet-ldapusers", "lr.upload" ]);
angular.module("autonubil-intranet-apps")
.run(function(PluginMenuService, PluginComponentService, $location) {

	PluginMenuService.addItem("/main/admin", "/apps", {
		title : "App Links",
		visible:true
	}, {
		controller : "AppListController",
		templateUrl : "apps/templates/apps.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/apps/:id", {
		controller : "AppEditController",
		templateUrl : "apps/templates/app.html" 
	});

});
