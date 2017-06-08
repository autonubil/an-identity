angular.module("templates", []);

angular.module("autonubil-intranet",["templates","ngRoute"]);

angular.module("autonubil-intranet")
 .run(function(PluginMenuService) {

	PluginMenuService.addItem("","/welcome",{title:"Welcome",visible:true}, {
		controller: function() {},
		templateUrl: "welcome.html"
	});
	
	PluginMenuService.setDefault("/auth/login");
	
});
