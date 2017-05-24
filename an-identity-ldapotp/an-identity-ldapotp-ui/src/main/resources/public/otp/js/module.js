
angular.module("autonubil-intranet").requires.push("autonubil-intranet-otp");

angular.module("autonubil-intranet-otp", [ "angular-plugin", "restangular","autonubil-intranet-ldap"]);
angular.module("autonubil-intranet-otp")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register component for OTP .... ");
	
	myOtps = {
		visible : true,
		title : "OTP Tokens",
		templateUrl : "otp/templates/otps.html" 
	};
	
	PluginComponentService.addItem("/me",myOtps);
	
	
});

angular.module("autonubil-intranet-otp")
.controller("MyOtpController", function($scope, OtpService, AuthService) {

	$scope.supported = false;
	
	$scope.user = AuthService.getAuthStatus();
	
	$scope.update = function() {
		OtpService.list(
				function(tokens) {
					$scope.tokens = tokens;
					$scope.supported = true;
			},
				function(response) {
					if(response.status == 501) {
						$scope.supported = false;
					}
				}
			);
	};
	
	
	$scope.update();
	
	
	$scope.create = function() {
		
		OtpService.create($scope.update,$scope.update);
		
	}
	
	
});

angular.module("autonubil-intranet-otp")
.service("OtpService", function(Restangular,$location) {
	
	return {
		list : function(success,error) {
			return Restangular.all("autonubil/api/ldapotp/mytokens").getList().then(success,error);
		},
		create : function(success,error) {
			return Restangular.one("autonubil/api/ldapotp/mytokens").customPOST({}).then(success,error);
		}
	};
	
	
});
