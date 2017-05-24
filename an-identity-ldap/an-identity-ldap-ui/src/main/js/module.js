angular.module("autonubil-intranet").requires.push("autonubil-intranet-ldap");

angular.module("autonubil-intranet-ldap", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-ldap")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for ldap ... ");
	
	PluginMenuService.addItem("/main/admin", "/ldap", {
		visible: true,
		title : "LDAP Servers"
	}, {
		controller : "LdapConfigListController",
		templateUrl : "ldap/templates/ldap_configs.html" 
	});

	PluginMenuService.addRoute("/main/admin/ldap/:id", {
		controller : "LdapConfigEditController",
		templateUrl : "ldap/templates/ldap_config.html" 
	});

});
