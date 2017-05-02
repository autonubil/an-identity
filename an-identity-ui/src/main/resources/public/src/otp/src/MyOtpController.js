angular.module("autonubil-intranet-otp")
.controller("MyOtpController", function($scope, OtpService) {

	$scope.supported = false;
	
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
	
	
});
