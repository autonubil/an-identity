angular.module("autonubil-intranet-mail")
.controller("AppEditController", function($scope,AuthService,AppService,LdapConfigService,LdapGroupService,$routeParams) {

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
		AppService.get($routeParams.id,function(app){
			$scope.app = app;
		});
		AppService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});
	};
	
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
	

	$scope.save = _.debounce(function() {
		AppService.save($scope.app,function(app) {
			$scope.app = app;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		AppService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		AppService.addPermission(
				$routeParams.id,
				{
					appId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
	$scope.uploadSuccess = function() {
		s = $("#icon").attr("src")+"?"+Math.random();
		console.log("image uploaded ... new image: "+s);
		$("#icon").attr("src", s);
	}
	
	$scope.update();
	
})