angular.module("autonubil-intranet-less")
.controller("CssEditController", function($scope,CssConfigService,$routeParams) {
	
	$scope.update = function() {
		CssConfigService.get($routeParams.id,function(config) {
			$scope.config = config;
		})
	};
	
	$scope.update();
	
	
	$scope.save = function() {
		CssConfigService.put($scope.config,$scope.update);
	}
	
	
});
