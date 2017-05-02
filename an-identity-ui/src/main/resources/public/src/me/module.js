
angular.module("autonubil-intranet").requires.push("autonubil-intranet-me");

angular.module("autonubil-intranet-me", [ "angular-plugin", "restangular","autonubil-intranet-auth" ]);
angular.module("autonubil-intranet-me")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for mail ... ");
	
	PluginMenuService.addItem("/main", "/me", {
		visible: true,
		title : "Me"
	}, {
		templateUrl : "me/templates/me.html" 
	});
	
	
	myInfo = {
		visible : true,
		title : "My Accounts",
		templateUrl : "me/templates/my_info.html" 
	};
	
	
	PluginComponentService.addItem("/me",myInfo);
	
	myApps = {
		visible : true,
		status : "My Apps",
		templateUrl : "me/templates/my_apps.html"
	};
	
	
	PluginComponentService.addItem("/me",myApps); 
	PluginComponentService.addItem("/dashboard",myApps); 
	
});
