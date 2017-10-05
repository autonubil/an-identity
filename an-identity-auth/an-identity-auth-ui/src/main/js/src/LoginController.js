angular.module("autonubil-intranet-auth")
.controller("LoginController", function($scope, AuthService, $location, $routeParams, usSpinnerService) {
	
	$scope.status = AuthService.getAuthStatus();

	$scope.isLoggingIn = false;
	
	$scope.startSpin = function(){
		$scope.isLoggingIn = true;
	 	usSpinnerService.spin('login-spinner');
	}
	 
	$scope.stopSpin = function(){
		$scope.isLoggingIn = false;
	    usSpinnerService.stop('login-spinner');
	}
	

	$scope.config = {
		sourceId : $routeParams.sourceId || "",
		mode : $routeParams.mode || "login",
		username : $routeParams.username || "",
		redirect: "/auth/dashboard"
	};

	$scope.reset = {
			username: $scope.config.username,
			secondFactor: "",
			token: $routeParams.token||"",
			email: $routeParams.email||"",
			oldPassword:"",
			newPassword:"",
	};
	
	$scope.credentials = {
			username : $scope.reset.username,
			password : ""
	};
	
	
	AuthService.getSources(function(sources) {
		var x = [];
		_.forEach(sources,function(e) {
			if (e.sourceId!="LOCAL" || $routeParams["local"] || (sources.length == 1) ) {
				console.log("matches: ",e);
				x.push(e);
			} else {
				console.log("doesnt match: ",e);
			}
		});
		sources = x;
		
		if($scope.config.sourceId=="") {
			$scope.config.sourceId = sources[0].sourceId;
		};
		_.forEach(sources,function(e) {
			if(e.sourceId == $scope.config.sourceId) {
				$scope.source = e;
			};
		});
		$scope.sources = sources;
	});
	
	$scope.showDashboard = function() {
		angular.module("autonubil-intranet-auth").goto("/auth/dashboard");
	}
	
	$scope.login = function() {
		$scope.success = undefined;
		$scope.error = undefined;
		$scope.credentials.sourceId = $scope.config.sourceId;
		$scope.startSpin();
		AuthService.login($scope.credentials,
				function(user) {
					$scope.stopSpin();
					if (user.status == 401 ) {
						$scope.error = {
								"data" : {
									"message": "Wrong credentials provided",
									"status": user.status
								}
								 
						}
						return;
					} else if (user.status != 200 ) {
						$scope.error = {
								"data" : {
									"message": "Server Error:" + user.message,
									"status": user.status
								}
								 
						}
						return;
					}
			
					$scope.status.loggedIn = true;
					if ($routeParams.return_url && $routeParams.return_url.length > 0 ) {
						redirect =$routeParams.return_url;
					} else {
						redirect = $scope.config.redirect;
					}
					if (redirect [0] != '#'){
						if (redirect [0] == '/') {
							angular.module("autonubil-intranet-auth").goto(redirect );
						} else {
							window.location=redirect;
						}
					}
				},
				function(response) {
					$scope.error = response.data.error;
					$scope.stopSpin();
				}
		);
	};
	
	$scope.resetPassword = function() {
		$scope.success = undefined;
		$scope.error = undefined;
		$scope.reset.sourceId = $scope.config.sourceId; 
		AuthService.reset($scope.reset,
			function() {
				console.log("(controller) reset success!");
				if($scope.config.mode=='reset') {
					$scope.success = "Success! Please check your mail.";
				} else if($scope.config.mode=='resetWithOldPassword') {
					$scope.success = "Success! You can now login with your new password.";
				} else if($scope.config.mode=='resetWithToken') {
					$scope.success = "Success! You can now login with your new password.";
				}
			},
			function(response) { 
				console.log("(controller) reset error!",response);
				$scope.error = response;
			}
		);

	}
	
});

