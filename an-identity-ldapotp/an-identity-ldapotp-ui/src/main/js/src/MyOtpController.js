angular.module("autonubil-intranet-otp")
.controller("MyOtpController", function($scope, OtpService, AuthService, $location, $rootScope, $timeout, $routeParams) {

	$scope.loaded = false;
	$scope.supported = false;
	
	AuthService.updateAuth();
	$scope.status = AuthService.getAuthStatus();
	
	$scope.update = function() {
		OtpService.list(
				function(tokens) {
					$scope.tokens = tokens;
					$scope.supported = true;
					$scope.loaded = true;
			},
				function(response) {
					$scope.loaded = true;
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
	
	
	$scope.deleteToken = function(tokenId) {
		OtpService.remove(tokenId, $scope.update);
	};
	
	
	$scope.addToken = function() {
		console.log("posting token request")
		$scope.requestError = undefined;
		OtpService.create(
			$scope.tokenRequest,
			function (t) {
				$scope.countdown = 600;
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
	
	if (!$routeParams.selectedTab ||  'otptokens'==$routeParams.selectedTab) {
		$scope.update();
		$scope.newTokenRequest();
	}
	
	
});
