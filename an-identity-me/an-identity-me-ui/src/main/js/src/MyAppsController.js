angular.module("autonubil-intranet-me")
.controller("MyAppsController", function($scope,MeService,AuthService,$location) {
	
	$scope.search =  "";

	$scope.updateApps = _.debounce(function() {
		MeService.getApps({search:$scope.search},function(apps){
			$scope.apps = apps;
		});
	},250);
	
	$scope.updateApps();
	
});
