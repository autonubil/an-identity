
//angular.module("autonubil-intranet",["templates","ngRoute","angular-plugin","autonubil-intranet-auth","autonubil-intranet-localauth","autonubil-intranet-mail","autonubil-intranet-ldap"]);
angular.module("autonubil-intranet",["templates","ngRoute","angular-plugin"]);

angular.module("autonubil-intranet")
 .run(function(PluginMenuService) {

	PluginMenuService.addItem("","/welcome",{title:"Welcome",visible:true}, {
		controller: function() {},
		templateUrl: "app/templates/welcome.html"
	});
	
	PluginMenuService.setDefault("/welcome");
	
});
