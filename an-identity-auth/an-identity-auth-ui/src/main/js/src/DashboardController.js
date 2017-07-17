angular.module("autonubil-intranet-auth")
.controller("DashboardController", function($scope, AuthService, $location, $routeParams) {
	
	$scope.status = AuthService.getAuthStatus();
	$scope.selectedTab = $routeParams.selectedTab || "applications";
	AuthService.updateAuth();
});

