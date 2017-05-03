angular.module("autonubil-intranet-auth")
.controller("LoginStatusController", function($scope,AuthService,$rootScope) {
	$scope.status = AuthService.getAuthStatus();
	$rootScope.$on("authChanged", function(e,status) {
		$scope.status = status;
	})
	$scope.logout = function() {
		console.log("logout ... ");
		AuthService.logout();
	}
})