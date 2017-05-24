angular.module("autonubil-intranet-localauth")
.controller("LocalAuthUserEditController", function($scope,AuthService,LocalAuthUserService,$routeParams) {

	$scope.changed = false;
	
	$scope.reset = {};
	
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
		LocalAuthUserService.resetPassword($scope.user.id,
			function() {
				console.log("(controller) reset OK!")
			},
			function() {
				console.log("(controller) reset ERROR!")
			}
		);
	};
	
})