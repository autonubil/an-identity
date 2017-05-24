angular.module("autonubil-intranet-mail")
.controller("MailTemplateEditController", function($scope,AuthService,MailTemplateService,MailConfigService,$routeParams) {

	MailTemplateService.listModules(function(modules){$scope.modules = modules;});
	$scope.changed = false;
	
	$scope.update = function() {
		MailTemplateService.get($routeParams.id,function(mailTemplate){
			$scope.mailTemplate = mailTemplate;
		});
		MailConfigService.getList({},function(mailConfigs){
			$scope.mailConfigs = mailConfigs;
			$scope.exampleMailConfig = mailConfigs[0].id;
			$scope.example();
		})
		
	};

	$scope.example = function() {
		MailTemplateService.render($routeParams.id,$scope.exampleMailConfig,function(mail) {
			$scope.mail = mail;
			$("#htmlExample").html($scope.mail.html);
		});
	};
	
	$scope.update();
	
	$scope.save = _.debounce(function() {
		MailTemplateService.save($scope.mailTemplate,function(mailTemplate) {
			$scope.mailTemplate = mailTemplate;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.sendTest = function (recipient,exampleMailConfig) {
		$scope.testSuccess = undefined;
		$scope.testError = undefined;
		MailTemplateService.save($scope.mailTemplate,function(mailTemplate) {
			MailTemplateService.sendTest(
					mailTemplate.id,
					exampleMailConfig,
					recipient,
					function() {
						$scope.testSuccess = true;

					},
					function(response) {
						$scope.testError = response;
					}
			);
		});
	}
	
})