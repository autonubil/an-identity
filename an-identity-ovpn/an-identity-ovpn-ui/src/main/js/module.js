
angular.module("autonubil-intranet").requires.push("autonubil-intranet-ovpn");

angular.module("autonubil-intranet-ovpn", [ "angular-plugin", "restangular","autonubil-intranet-auth" ]);
angular.module("autonubil-intranet-ovpn")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	// register ovpn component or menu item here ... 
	
});
