angular.module("autonubil-intranet-openid")
 .controller("ApplicationListController", function($scope, AuthService, OpenidConnectService, $location) {

	 
	
	 
	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OpenidConnectService.getList($scope.search, function(applications){
			$scope.applications = applications;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OAuth application ... ");
		
		OpenidConnectService.add(
				{ name : $scope.search.search },
				function(application){
					console.log("OAuth2 application saved... ",application);
					$location.path("/main/admin/openid/application/"+application.id);
				}
		);
	}
	
	AuthService.updateAuth();
	
	$scope.update();
	
})