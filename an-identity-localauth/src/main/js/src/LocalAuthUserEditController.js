angular.module("autonubil-intranet-localauth")
.controller("LocalAuthUserEditController", function($scope,AuthService,LocalAuthUserService,$routeParams) {

	$scope.changed = false;
	
	$scope.reset = {
			resetFinished : false,
			success: false,
			message: "",
			password : "",
			passwordRepeat : ""
	};
	
	LocalAuthUserService.get($routeParams.id,function(user){
		console.log(user);
		$scope.user = user;
		$scope.resetUrl = "/autonubil/api/localauth/users/"+user.id+"/otp";
	});

	$scope.save = _.debounce(function() {
		LocalAuthUserService.save($scope.user,function(user) {
			$scope.user = user;
			$scope.changed = false;
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};

	$scope.resetOTP = function() {
		LocalAuthUserService.resetOTP($scope.reset.token,function(secret) {
			$scope.secret = secret;
		});
	};
	
	$scope.resetPassword = function() {
		$scope.reset.resetFinished = false;
		LocalAuthUserService.resetPassword($scope.user.id, undefined, $scope.reset.password, 
			function() {
				$scope.reset = "(controller) reset OK!";
				$scope.reset.resetFinished = true;
				$scope.reset.success = true;
				
			},
			function() {
				$scope.reset = "(controller) reset ERROR!";
				$scope.reset.resetFinished = true;
				$scope.reset.success = false;
			}
		);
	};
	
})