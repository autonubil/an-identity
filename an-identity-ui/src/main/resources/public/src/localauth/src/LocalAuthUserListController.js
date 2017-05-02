angular.module("autonubil-intranet-localauth")
.controller("LocalAuthUserListController", function($scope,AuthService,LocalAuthUserService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		LocalAuthUserService.getList($scope.search, function(users){
			$scope.users = users;
		});
		if($scope.search.search.length > 0) {
			$scope.enableAdd = false;
			LocalAuthUserService.getList({username:$scope.search.search}, function(users){
				if(users.length > 0) {
					$scope.enableAdd = false;
				} else {
					$scope.enableAdd = true;
				}
			});
		} else {
			$scope.enableAdd = false;
		};
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function() {
		LocalAuthUserService.add({username:$scope.search.search}, function(user) {
			$location.url($location.url()+"/"+user.id);
		});
		
	}

	$scope.remove = function(id) {
		LocalAuthUserService.remove(id, function() {
			$scope.update();
		});
		
	}
	
	$scope.update();
	
	
})