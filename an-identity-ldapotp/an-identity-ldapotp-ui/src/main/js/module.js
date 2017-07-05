
angular.module("autonubil-intranet").requires.push("autonubil-intranet-otp");

angular.module("autonubil-intranet-otp", [ "angular-plugin", "restangular","autonubil-intranet-ldap", "angularMoment"]);
angular.module("autonubil-intranet-otp")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register component for OTP .... ");
	
	myOtps = {
		visible : true,
		defaultItem: true,
		title : "OTP Tokens",
		id: "otptokens",
		templateUrl : "otp/templates/otps.html" 
	};
	
	PluginComponentService.addItem("/me",myOtps);
	PluginComponentService.addItem("/dashboard",myOtps);
	
	
});
