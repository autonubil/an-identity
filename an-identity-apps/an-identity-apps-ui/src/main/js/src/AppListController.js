angular.module("autonubil-intranet-apps")
.controller("AppListController", function($scope,AuthService,AppService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		AppService.getList($scope.search, function(apps){
			$scope.apps = apps;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving app ... ");
		var x = { name : $scope.search.search, url : "http://www.example.com" };
		AppService.add(
				x,
				function(app){
					console.log("app saved... ",app);
					$location.path("/main/admin/apps/"+app.id);
				}
		);
	}
	
	$scope.update();
	
	
})