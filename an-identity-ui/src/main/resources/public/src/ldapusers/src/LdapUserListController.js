angular.module("autonubil-intranet-ldap")
.controller("LdapUserListController", function($scope,AuthService,LdapConfigService,LdapUserService,$location,$routeParams) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 25,
			config : $routeParams["source"] || ""
	};
	
	LdapConfigService.getList({},function(configs) {
		if($scope.search.config == "") {
			$scope.search.config = configs[0].id;
		}
		$scope.configs = configs;
		$scope.update();
	});

	$scope.update = function() {
		LdapUserService.getList($scope.search.config, $scope.search, function(users){
			$scope.users = users;
		});
	}
	
	$scope.setSource = function() {
		console.log("source: "+$scope.search.config)
		$location.search("source",$scope.search.config);
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
})