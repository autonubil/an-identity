
angular.module("autonubil-intranet").requires.push("autonubil-intranet-me");

angular.module("autonubil-intranet-me", [ "angular-plugin", "restangular","autonubil-intranet-auth" ]);
angular.module("autonubil-intranet-me")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	console.log("register menu items for mail ... ");
	
	var meMenuItem = {
			visible: false,
			title : "Me"
		};
	
	PluginMenuService.addItem("/main", "/me", meMenuItem, { templateUrl : "me/templates/me.html" });
	
	$rootScope.$on("authChanged",function(e,status) {
		meMenuItem.visible = status.loggedIn;
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
