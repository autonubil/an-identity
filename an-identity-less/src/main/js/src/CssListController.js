angular.module("autonubil-intranet-less")
.controller("CssListController", function($scope,CssConfigService,$location) {
	
	$scope.update = function() {
		CssConfigService.list(function(configs) {
			$scope.configs = configs;
		})
	};
	
	$scope.update();
	
	$scope.add = function() {
		config = { order: 0, name : "", rel : "", type : "", href : ""};
		CssConfigService.create(
			config,
			function(config) {
				$location.path("/main/admin/css/"+config.id);
			}
		);
	};
	
	$scope.remove = function(config) {
		CssConfigService.remove(
				config,
				function() {
					console.log("deleted ... ");
					$scope.update();
				}
			);
	}
	
	
});
