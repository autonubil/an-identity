angular.module("autonubil-intranet").requires.push("autonubil-intranet-ovpn");
angular.module("autonubil-intranet-ovpn", [ "angular-plugin", "restangular","autonubil-intranet-auth"]);
angular.module("autonubil-intranet-ovpn")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	PluginMenuService.addItem("/main/admin", "/ovpn/vpns", {
		title: "OpenVPN",
		visible: true
	}, {
		controller : "VpnListController",
		templateUrl : "ovpn/templates/vpns.html" 
	});
	
	
	PluginMenuService.addRoute("/main/admin/ovpn/vpns/:id", {
		controller: "VpnEditController",
		templateUrl: "ovpn/templates/vpn.html" 
	});

});


angular.module("autonubil-intranet").requires.push("autonubil-intranet-myovpns");
angular.module("autonubil-intranet-myovpns", [ "angular-plugin", "restangular","autonubil-intranet-auth", "autonubil-intranet-me" ]);

angular.module("autonubil-intranet-myovpns")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {
	myVpns= {
			visible: true,
			defaultItem: true,
			id: "vpns",
			status: "My VPNs",
			title: "VPNs",
			
			templateUrl : "ovpn/templates/my_vpns.html"
	};
	
	PluginComponentService.addItem("/me",myVpns); 
	PluginComponentService.addItem("/dashboard",myVpns); 

});
