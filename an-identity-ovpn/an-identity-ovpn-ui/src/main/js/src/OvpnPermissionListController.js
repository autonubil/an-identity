angular.module("autonubil-intranet-ovpn")
.controller("OvpnListController", function($scope,AuthService,OvpnService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getList($scope.search, function(ovpns){
			$scope.ovpns = ovpns;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN ... ");
		var x = { name : $scope.search.search, description: $scope.search.search  };
		OvpnService.add(
				x,
				function(ovpn){
					console.log("OpenVPN saved... ",ovpn);
					$location.path("/main/admin/ovpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})