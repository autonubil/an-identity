angular.module("autonubil-intranet-me")
.controller("MyAppsController", function($scope, MeService, AuthService, $location, $routeParams) {
	
	$scope.search = {
			term : ""
	};
	
	console.log("search initialized to: ",$scope.search);

	$scope.updateApps = function() {
		console.log("updating apps with filter: "+$scope.search.term);
		MeService.getApps({search:$scope.search.term},function(apps){
			$scope.apps = apps;
		});
	};
	
	if (!$routeParams.selectedTab ||  'applications'==$routeParams.selectedTab)
		$scope.updateApps();
	
});
