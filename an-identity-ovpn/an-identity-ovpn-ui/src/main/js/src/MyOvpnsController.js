angular.module("autonubil-intranet-myovpns")
.controller("MyOvpnsController", function($scope, MeService, MyOvpnsService, AuthService, $location) {
	
	$scope.search =  "";

	$scope.updateVpns = _.debounce(function() {
		MyOvpnsService.getVpns({search:$scope.search},function(vpns){
			$scope.vpns = vpns;
		});
	},250);
	
	$scope.updateVpns();
	
});
