angular.module("autonubil-intranet-less")
.controller("LessConfigController", function($scope,LessConfigService) {
	
	$scope.lessConfig = {};
	
	$scope.update = function() {
		LessConfigService.get(function(config) {
			$scope.lessConfig = config;
		})
	};
	
	$scope.save = function() {
		LessConfigService.put($scope.lessConfig, function() {
			$scope.update();
			console.log("success saving styles!");
			console.log($("#main_styles").attr("href","/autonubil/api/less/stylesheet?"+Math.random()));
		});
	};
	
	$scope.updateTyping = _.debounce($scope.save,400);

	$scope.update();
	
});
