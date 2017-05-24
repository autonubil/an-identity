angular.module("autonubil-intranet-mail")
.controller("MailConfigEditController", function($scope,AuthService,MailConfigService,$routeParams) {

	$scope.changed = false;

	$scope.params = "";
	
	$scope.parseError = false;
	
	$scope.update = function() {
		MailConfigService.get($routeParams.id,function(mailConfig){
			$scope.mailConfig = mailConfig;
			$scope.params = JSON.stringify(mailConfig.params, null, 2);
		});
	};

	$scope.save = _.debounce(function() {
		
		mc = $scope.mailConfig;
		
		try {
			$scope.error = false;
			$scope.parseError = false;
			mc.params = JSON.parse($scope.params);
		} catch (e) {
			$scope.parseError = true;
			$scope.error = e;
			return;
		}
		
		MailConfigService.save(
			mc,
			function(mailConfig) {
				$scope.update();
				$scope.changed = false;
			},
			function(response) {
				$scope.error = response;
			}
		);
	},700)
	
	$scope.startChange = function() {
		$scope.error = undefined;
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.setPassword = function(id,password) {
		$scope.resetSuccess = false;
		$scope.resetError = false;
		MailConfigService.setPassword(
			id,
			password,
			function() {
				// success
				$scope.resetSuccess = true;
			},
			function() {
				$scope.resetError = true;
			}
		)
	};
	
	$scope.update();
	
})