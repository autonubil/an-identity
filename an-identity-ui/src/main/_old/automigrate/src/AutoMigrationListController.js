angular.module("autonubil-intranet-automigrate")
.controller("AutoMigrationListController", function($scope,AutoMigrationService,LdapConfigService,$location) {

	$scope.update = function() {
		$scope.configMap = {};
		$scope.newConfig = {};
		LdapConfigService.getList({},function(configs) {
			$scope.configs = configs;
			//$scope.newConfig.toLdap = configs[0].id; 			
			//$scope.newConfig.fromLdap = configs[0].id; 			
			
			_.forEach(configs,function(x){
				$scope.configMap[x.id] = x.name;
			});
		});

		AutoMigrationService.list({}, function(migrationConfigs) {
			$scope.migrationConfigs = migrationConfigs;
		})
		
	}
	
	$scope.update();
	
	$scope.remove = function(id) {
		AutoMigrationService.remove(
			id,
			function(config) {
				$scope.update();
			},
			function(response) {
				$scope.error = response;
			}
		);
	}
	
	$scope.add = function() {
		AutoMigrationService.create(
			$scope.newConfig,
			function(config) {
				$location.path("/main/admin/migrate/"+config.id);
			},
			function(response) {
				$scope.error = response;
			}
		);
	}
	

})