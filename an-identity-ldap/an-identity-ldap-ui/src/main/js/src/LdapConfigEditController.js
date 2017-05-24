angular.module("autonubil-intranet-ldap")
.controller("LdapConfigEditController", function($scope,AuthService,LdapConfigService,LdapConfigFieldService,$routeParams) {

	$scope.changed = false;
	
	$scope.enablePassword = false;
	$scope.password = "";
	$scope.status = { error: false, success : false};

	$scope.update = function() {
		LdapConfigFieldService.list(
				$routeParams.id,
				{},
				function(fields){
					$scope.fields = fields;
				},
				function(response){
				}
		);
		LdapConfigService.get($routeParams.id,function(ldapConfig){
			$scope.ldapConfig = ldapConfig;
		});
		
		LdapConfigService.getTypes(function(types) {
			$scope.types = types;
		});
		
	};


	$scope.save = _.debounce(function() {
		LdapConfigService.save($scope.ldapConfig,function(ldapConfig) {
			$scope.status = { error: false, success : false};
			$scope.ldapConfig = ldapConfig;
			$scope.changed = false;
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.test = function() {
		$scope.status = { error: false, success : false};
		LdapConfigService.test(
			$routeParams.id,
			function(result) {
				$scope.status = result;
				$scope.status.error = !$scope.status.success; 
				console.log($scope.status);
			});
	};
	
	$scope.setPassword = function() {
		console.log("setting password");
		LdapConfigService.setPassword($routeParams.id,$scope.password, function() {
			console.log("set password reset");
			$scope.password = "";
			$scope.enablePassword = false;
		});
	};
	
	
	$scope.addField = function() {
		$scope.fieldError = undefined;
		LdapConfigFieldService.save(
				$routeParams.id,
				$scope.field,
				function(field) {
					$scope.update();
				},
				function(response) {
					$scope.fieldError = response;
				}
		);
	}
	
	$scope.deleteField = function(sourceId, id) {
		LdapConfigFieldService.deleteField(sourceId,id,$scope.update);
	}

	
	$scope.update();
	
})