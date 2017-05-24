angular.module("autonubil-intranet-ldap")
.controller("LdapGroupEditController", function($scope,AuthService,LdapGroupService,$routeParams) {

	$scope.changed = false;
	
	$scope.enablePassword = false;
	$scope.password = "";
	
	$scope.update = function() {
		LdapGroupService.get($routeParams.connectionId,$routeParams.groupId,function(group){
			$scope.group = group;
			$scope.changed = false;
		});
	};
	
	$scope.update();
	
	
	$scope.formatDate = function(date) {
		return date.getTime();
	};
	
})