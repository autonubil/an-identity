angular.module("autonubil-intranet-ovpn")
.controller("VpnListController", function($scope,AuthService,OvpnService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getList($scope.search, function(vpns){
			$scope.vpns= vpns;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN source ... ");
		var x = { name : $scope.search.search, 
				secretsProvider: "internal",   description: $scope.search.search, 
				serverConfiguration: {}, serverConfigurationProvider: "default", 
				sessionConfiguration: {}, sessionConfigurationProvider: "default", 
				clientConfiguration: {}, clientConfigurationProvider: "default"
		};
		OvpnService.add(
				x,
				function(ovpn){
					console.log("OpenVPN source saved... ",ovpn);
					$location.path("/main/admin/ovpn/vpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})