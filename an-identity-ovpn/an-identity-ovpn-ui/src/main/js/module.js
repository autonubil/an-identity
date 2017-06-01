
angular.module("autonubil-intranet").requires.push("autonubil-intranet-ovpn");

angular.module("autonubil-intranet-ovpn", [ "angular-plugin", "restangular","autonubil-intranet-auth", "autonubil-intranet-me" ]);
angular.module("autonubil-intranet-ovpn")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	PluginMenuService.addItem("/main/admin", "/vpn_sources", {
		title : "OpenVPN Sources",
		visible:true
	}, {
		controller : "SourceListController",
		templateUrl : "ovpn/templates/sources.html" 
	});
	
	PluginMenuService.addItem("/main/admin", "/vpn_permission", {
		title : "OpenVPN VPNs",
		visible:true
	}, {
		controller : "OvpnListController",
		templateUrl : "ovpn/templates/ovpns.html" 
	});
	
	
	PluginMenuService.addRoute("/main/admin/apps/:id", {
		controller : "OvpnEditController",
		templateUrl : "ovpn/templates/ovpn.html" 
	});
 
	
	
	myApps = {
			visible : true,
			status : "My VPNs",
			templateUrl : "ovpn/templates/my_vpns.html"
		};
		
		
		PluginComponentService.addItem("/me",myApps); 
		PluginComponentService.addItem("/dashboard",myApps); 
	
});
