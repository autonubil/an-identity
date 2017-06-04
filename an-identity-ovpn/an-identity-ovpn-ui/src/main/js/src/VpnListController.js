angular.module("autonubil-intranet-ovpn")
.controller("SourceListController", function($scope,AuthService,OvpnService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getSourceList($scope.search, function(sources){
			$scope.sources = sources;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN source ... ");
		var x = { name : $scope.search.search, description: $scope.search.search, configuration: "{}", serverConfigurationProvider: "default", clientConfigurationProvider:"default" };
		OvpnService.addSource(
				x,
				function(ovpn){
					console.log("OpenVPN source saved... ",ovpn);
					$location.path("/main/admin/ovpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})