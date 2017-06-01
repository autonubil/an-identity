angular.module("autonubil-intranet-ovpn")
.controller("MyVpnsController", function($scope,MeService,AuthService,$location) {
	
	$scope.search =  "";

	$scope.updateApps = _.debounce(function() {
		OvpnService.getVpns({search:$scope.search},function(vpns){
			$scope.vpns = vpns;
		});
	},250);
	
	$scope.updateVpns();
	
});
