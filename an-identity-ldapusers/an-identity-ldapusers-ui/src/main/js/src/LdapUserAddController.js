angular.module("autonubil-intranet-ldap")
.controller("LdapUserAddController", function($scope,AuthService,LdapConfigService,LdapUserService,$routeParams,$location) {

	
	$scope.expiry = [
		{
			value :  Date.now()+(1000*60*60*24*30*6),
			display : "6 months"
		},
		{
			value :  Date.now()+(1000*60*60*24*30*3),
			display : "3 months"
		},
		{
			value :  Date.now()+(1000*60*60*24*30*1),
			display : "1 month"
		},
		{
			value :  -1,
			display : "never"
		}
	];
	
	$scope.user = {
		userExpires : $scope.expiry[0].value
	};

	
	LdapConfigService.get($routeParams.connectionId,function(config) {
		$scope.config = config;
	})
	
	$scope.create = function() {
		LdapUserService.create($routeParams.connectionId,$scope.user,function(user) {
			console.log("success: ",user);
			$location.path("/main/admin/ldapusers/edit_user/"+$routeParams.connectionId+"/"+user.id);
		},function(response) {
			console.log("error: ",response);
			$scope.error = response;
		});
	};
	
	
})