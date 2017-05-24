angular.module("autonubil-intranet-me")
.controller("MyInfoController", function($scope,MeService,AuthService,$location) {
	
	$scope.error = {};
	$scope.success = {};
	
	MeService.getUsers({},function(identity){
		$scope.identity = identity;
	});
	
	$scope.reset = function(username,userId,sourceId) {
		reset = {
			sourceId : sourceId,
			username : username,
			oldPassword : $scope.reset[userId].oldPassword,
			newPassword : $scope.reset[userId].newPassword
		};
		$scope.error[userId] = undefined;
		$scope.success[userId] = undefined;
		AuthService.reset(
			reset,
			function() {
				$scope.success[userId] = "Success!";
			}, 
			function(response) {
				$scope.error[userId] = response;
			}
		);
	};
	
	
});
