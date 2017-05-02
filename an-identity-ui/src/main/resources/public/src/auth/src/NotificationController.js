angular.module("autonubil-intranet-auth")
.controller("NotificationController", function($scope,AuthService,$location,$routeParams) {
	
	$scope.status = AuthService.getAuthStatus();
	
})