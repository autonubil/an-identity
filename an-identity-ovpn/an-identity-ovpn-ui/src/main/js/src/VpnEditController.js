angular.module("autonubil-intranet-ovpn")
.controller("VpnEditController", function($scope,AuthService,OvpnService,LdapConfigService,LdapGroupService,$routeParams) {

	$scope.changed = false;
	
	$scope.newPermission = {
			source : {},
			group : {},
			name : "",
	};

	
	LdapConfigService.getList({},function(configs){
		$scope.configs=configs;
	});

 
	
	$scope.update = function() { 
		OvpnService.get($routeParams.id,function(source){
			$scope.source = source;
		});
		
		OvpnService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});

		
		OvpnService.getClientConfigProviderList("", function(clientConfigProviders){
			console.log("clientConfigProviders: "+clientConfigProviders.length);
			$scope.clientConfigProviders = clientConfigProviders;
		});
		
		OvpnService.getServerConfigProviderList("", function(serverConfigProviders){
			console.log("serverConfigProviders: "+serverConfigProviders.length);
			$scope.serverConfigProviders = serverConfigProviders;
		});
	};
	

	$scope.save = _.debounce(function() {
		OvpnService.save($scope.source,function(source) {
			$scope.source = source;
			$scope.changed = false;
			// $scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save(true);
	};
	
	
	$scope.update();
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.source.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.source.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		OvpnService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OvpnService.addPermission(
				$routeParams.id,
				{
					vpnId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
})
.directive('jsonText', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {            
          function into(input) {
            return JSON.parse(input);
          }
          function out(data) {
            return JSON.stringify(data);
          }
          ngModel.$parsers.push(into);
          ngModel.$formatters.push(out);

        }
    };
});
