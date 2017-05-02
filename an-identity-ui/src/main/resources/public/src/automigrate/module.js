angular.module("autonubil-intranet").requires.push("autonubil-intranet-automigrate");

angular.module("autonubil-intranet-automigrate", [ "angular-plugin", "restangular", "autonubil-intranet-ldap", "autonubil-intranet-ldapusers" ]);
angular.module("autonubil-intranet-automigrate")
.run(function(PluginMenuService, PluginComponentService, $location, Restangular) {

	console.log("register menu items for automigrate ... ");

	menu = {
			title : "Auto Migration",
			visible: true
	};
	
	
	PluginMenuService.addItem("/main/admin", "/migrate", menu, {
		controller : "AutoMigrationListController",
		templateUrl : "automigrate/templates/migrations.html" 
	});

	PluginMenuService.addRoute("/main/admin/migrate/:id", {
		controller : "AutoMigrationEditController",
		templateUrl : "automigrate/templates/migration.html" 
	});
	
	
	
	console.log("checking for automigrate ... ");
	
	Restangular.all("autonubil/api/automigrate/configs").getList().then(
		function() {
		},
		function(response) {
			if(response.status==404) {
				menu.visible = false;
			}
		}
	);
	
	
	
	

});
