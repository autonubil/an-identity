
angular.module("autonubil-intranet").requires.push("autonubil-intranet-localauth");

angular.module("autonubil-intranet-localauth", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-localauth")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for localauth ... ");
	
	PluginMenuService.addItem("/main/admin", "/localusers", {
		visible: true,
		title : "Local Users"
	}, {
		controller : "LocalAuthUserListController",
		templateUrl : "localauth/templates/localusers.html" 
	});

	PluginMenuService.addRoute("/main/admin/localusers/:id", {
		controller : "LocalAuthUserEditController",
		templateUrl : "localauth/templates/localuser.html" 
	});

});
