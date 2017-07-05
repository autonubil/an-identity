angular.module("autonubil-intranet-auth")
.controller("LoginController", function($scope,AuthService,$location,$routeParams) {
	
	$scope.status = AuthService.getAuthStatus();

	$scope.selectedTab = $routeParams.selectedTab || "applications";
	
	
	$scope.config = {
		sourceId : $routeParams.sourceId || "",
		mode : $routeParams.mode || "login",
		username : $routeParams.username || ""
	};

	console.log($scope.config);
	
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
	
	$scope.login = function() {
		$scope.success = undefined;
		$scope.error = undefined;
		$scope.credentials.sourceId = $scope.config.sourceId; 
		AuthService.login($scope.credentials,
				function(user) {
					$scope.status.loggedIn = true;
				},
				function(response) { 
					$scope.error = response;
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

