angular.module("autonubil-intranet-mail")
.controller("MailConfigListController", function($scope,AuthService,MailConfigService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		MailConfigService.getList($scope.search, function(mailConfigs){
			$scope.mailConfigs = mailConfigs;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.remove = function(id) {
		MailConfigService.remove(id, function() {
			$scope.update();
		});
		
	}
	
	$scope.update();
	
	
})