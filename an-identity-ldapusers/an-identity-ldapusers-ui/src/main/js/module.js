angular.module("autonubil-intranet").requires.push("autonubil-intranet-ldapusers");

angular.module("autonubil-intranet-ldapusers", [ "angular-plugin", "restangular", "autonubil-intranet-ldap", "mp.datePicker" ]);
angular.module("autonubil-intranet-ldapusers")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for ldapusers ... ");

	PluginMenuService.addItem("/main/admin", "/ldapusers_users", {
		visible: true,
		title : "LDAP Users"
	}, {
		controller : "LdapUserListController",
		templateUrl : "ldapusers/templates/ldap_users.html" 
	});

	PluginMenuService.addItem("/main/admin", "/ldapusers_groups", {
		visible: true,
		title : "LDAP Groups"
	}, {
		controller : "LdapGroupListController",
		templateUrl : "ldapusers/templates/ldap_groups.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/ldapusers/edit_user/:connectionId/:userId", {
		controller : "LdapUserEditController",
		templateUrl : "ldapusers/templates/ldap_user.html" 
	});

	PluginMenuService.addRoute("/main/admin/ldapusers/edit_group/:connectionId/:groupId", {
		controller : "LdapGroupEditController",
		templateUrl : "ldapusers/templates/ldap_group.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/ldapusers/add_user/:connectionId", {
		controller : "LdapUserAddController",
		templateUrl : "ldapusers/templates/ldap_user_add.html" 
	});
	
	
});
