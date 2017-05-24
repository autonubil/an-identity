angular.module("autonubil-intranet-automigrate")
.controller("AutoMigrationEditController", function($scope,AutoMigrationService,LdapConfigService,LdapGroupService, $location,$routeParams) {

	$scope.groupsMap = {};
	
	
	$scope.groupMapping = {
			from : undefined,
			to : undefined
	}
	
	$scope.update = function() {
		$scope.configMap = {};
		$scope.newConfig = {};
		LdapConfigService.getList({},function(configs) {
			$scope.configs = configs;
			_.forEach(configs,function(x){
				$scope.configMap[x.id] = x.name;
			});
		});

			
		AutoMigrationService.get($routeParams.id, function(migrationConfig) {
			$scope.migrationConfig = migrationConfig;
			LdapGroupService.getList(migrationConfig.fromLdap,{},function(groups) {
				_.forEach(groups,function(g) {
					$scope.groupsMap[g.id] = g.displayName;
				});
				$scope.fromGroups = groups;
			});
			LdapGroupService.getList(migrationConfig.toLdap,{},function(groups) {
				_.forEach(groups,function(g) {
					$scope.groupsMap[g.id] = g.displayName;
				});
				$scope.toGroups = groups;
			});
		})
		
	}
	
	$scope.update();
	
	$scope.save = function() {
		AutoMigrationService.save(
				$scope.migrationConfig, 
				function(config) {
					$scope.update();
				},
				function(response) {
					$scope.error = response;
				}
			);
	};
	
	$scope.add = function() {
		$scope.migrationConfig.groupMappings.push({fromGroup : $scope.groupMapping.from, toGroup : $scope.groupMapping.to});
		$scope.save();
	};
	
	$scope.remove = function(index) {
		$scope.migrationConfig.groupMappings.splice(index,1);
		$scope.save();
	};

	

})