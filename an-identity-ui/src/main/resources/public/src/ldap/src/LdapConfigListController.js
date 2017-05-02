angular.module("autonubil-intranet-ldap")
.controller("LdapConfigListController", function($scope,AuthService,LdapConfigService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		LdapConfigService.getList($scope.search, function(ldapConfigs){
			$scope.ldapConfigs = ldapConfigs;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.remove = function(id) {
		LdapConfigService.remove(id, function() {
			$scope.update();
		});
		
	}

	$scope.add = function() {
		LdapConfigService.save({"name":$scope.search.search},function(lc) {
			$location.path($location.path()+"/"+lc.id);
		});
	};
	
	$scope.update();
	
	
})