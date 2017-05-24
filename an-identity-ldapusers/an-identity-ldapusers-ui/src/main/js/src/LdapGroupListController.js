angular.module("autonubil-intranet-ldap")
.controller("LdapGroupListController", function($scope,AuthService,LdapConfigService,LdapGroupService,$location,$routeParams) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 25,
			config : $routeParams["source"] || ""
	};

	LdapConfigService.getList({},function(configs) {
		if($scope.search.config == "") {
			$scope.search.config=configs[0].id
		}
		$scope.configs = configs;
		$scope.update();
	});

	$scope.update = function() {
		LdapGroupService.getList($scope.search.config, $scope.search, function(groups){
			$scope.groups = groups;
		});
	}
	
	$scope.setSource = function() {
		console.log("source: "+$scope.search.config)
		$location.search("source",$scope.search.config);
	}

	$scope.updateTyping = _.debounce($scope.update,400);
	
})