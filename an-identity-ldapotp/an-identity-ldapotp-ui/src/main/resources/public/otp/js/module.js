
angular.module("autonubil-intranet").requires.push("autonubil-intranet-otp");

angular.module("autonubil-intranet-otp", [ "angular-plugin", "restangular","autonubil-intranet-ldap", "angularMoment"]);
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
.controller("MyOtpController", function($scope, OtpService, AuthService, $location, $rootScope, $timeout) {

	$scope.supported = false;
	
	$scope.status = AuthService.getAuthStatus();
	
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
	
	$scope.newTokenRequest = function() {
		$scope.tokenRequest = {
				comment : $scope.status.user.username +"@"+ $location.host(),
				hash : "sha1",
				stepSeconds : 30,
				offsetSeconds : 0,
				length : 6
		};
	}
	
	$rootScope.$on("authChanged", function(e,status) {
		$scope.status = AuthService.getAuthStatus();
		$scope.newTokenRequest();
	});
	
	
	$scope.update();
	$scope.newTokenRequest();
	
	
	
	$scope.addToken = function() {
		console.log("posting token request")
		$scope.requestError = undefined;
		OtpService.create(
			$scope.tokenRequest,
			function (t) {
				$scope.countdown = 20;
				$scope.update();
				
				var cdf = 
					function() {
						if($scope.countdown > 0) {
							$scope.countdown = $scope.countdown -1;
							$timeout(cdf,1000);
						} else {
							$scope.imageUrl = undefined;
						}
					};
				
				$timeout(cdf,1000);
				$scope.imageUrl = "/autonubil/totp?secret="+encodeURIComponent(t.secret)+"&subject="+encodeURIComponent(t.comment);
			},
			function (e) {
				$scope.requestError = "Error creating token: "+e.message;
			}
		);
		
	}
	
	
});

angular.module("autonubil-intranet-otp")
.service("OtpService", function(Restangular,$location) {
	
	return {
		list : function(success,error) {
			return Restangular.all("autonubil/api/ldapotp/mytokens").getList().then(success,error);
		},
		create : function(token,success,error) {
			return Restangular.one("autonubil/api/ldapotp/mytokens").customPOST(token).then(success,error);
		}
	};
	
	
});
