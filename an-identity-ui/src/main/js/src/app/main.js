angular.module("templates", []);

angular.module("autonubil-intranet",["templates","ngRoute","angular-plugin"]);

angular.module("autonubil-intranet")
 .run(function(PluginMenuService) {

	PluginMenuService.addItem("","/welcome",{title:"Welcome",visible:true}, {
		controller: function() {},
		templateUrl: "app/templates/welcome.html"
	});
	
	PluginMenuService.setDefault("/welcome");
	
});
