angular.module("autonubil-intranet-mail")
.controller("MailTemplateListController", function($scope,AuthService,MailTemplateService,$location) {

	MailTemplateService.listLocales(function(locales){$scope.locales = locales;});
	MailTemplateService.listModules(function(modules){$scope.modules = modules;});

	$scope.search = {
			locale : "",
			module : "",
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		MailTemplateService.getList($scope.search, function(mailTemplates){
			$scope.mailTemplates = mailTemplates;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.remove = function(id) {
		MailTemplateService.remove(id, function() {
			$scope.update();
		});
		
	}
	
	$scope.update();
	
	
})