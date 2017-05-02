angular.module("autonubil-intranet-auth")
.controller("LoginStatusController", function($scope,AuthService) {
	$scope.status = AuthService.getAuthStatus();
	$scope.logout = function() {
		console.log("logout ... ");
		AuthService.logout();
	}
})