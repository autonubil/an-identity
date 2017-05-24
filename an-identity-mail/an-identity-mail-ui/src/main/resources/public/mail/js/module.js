
angular.module("autonubil-intranet").requires.push("autonubil-intranet-mail");

angular.module("autonubil-intranet-mail", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-mail")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for mail ... ");
	
	PluginMenuService.addItem("/main/admin", "/mailconfigs", {
		visible: true,
		title : "Mail Server"
	}, {
		controller : "MailConfigListController",
		templateUrl : "mail/templates/mail_configs.html" 
	});
	
	PluginMenuService.addItem("/main/admin", "/mailtemplates", {
		visible: true,
		title : "Mail Templates"
	}, {
		controller : "MailTemplateListController",
		templateUrl : "mail/templates/mail_templates.html" 
	});

	PluginMenuService.addRoute("/main/admin/mailconfigs/:id", {
		controller : "MailConfigEditController",
		templateUrl : "mail/templates/mail_config.html" 
	});

	PluginMenuService.addRoute("/main/admin/mailtemplates/:id", {
		controller : "MailTemplateEditController",
		templateUrl : "mail/templates/mail_template.html" 
	});
	
});

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
angular.module("autonubil-intranet-mail")
.service("MailConfigService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/mail/configs").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/mail/configs",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/mail/configs").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/mail/configs",id).remove().then(success);
		},
		save : function(config,success,error) {
			return config.put().then(success,error);
		},
		setPassword : function(id,password,success,error) { 
			Restangular.all("autonubil/api/mail/configs/"+id+"/password").customPUT("","",{"password":password}).then(success,error);
		}
	};
	
});

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
angular.module("autonubil-intranet-mail")
.service("MailTemplateService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/mail/templates").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/mail/templates",id).get().then(success);
		},
		render : function(id,mailConfigId,success) {
			return Restangular.one("autonubil/api/mail/templates/"+id,"example").get({mailConfigId:mailConfigId}).then(success);
		},
		add : function(template,success) {
			return Restangular.all("autonubil/api/mail/templates").post(template).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/mail/templates",id).remove().then(success);
		},
		save : function(template,success) {
			return template.put().then(success);
		},
		listLocales : function(success) {
			return Restangular.all("autonubil/api/mail/template_locales").getList({}).then(success);
		},
		listModules : function(success) {
			return Restangular.all("autonubil/api/mail/template_modules").getList({}).then(success);
		},
		sendTest : function(templateId,mailConfigId,recipient,success,error) {
			return Restangular.one("autonubil/api/mail/templates/"+templateId).customPOST({},"",{configId : mailConfigId, recipient: recipient}).then(success,error);
		}
	};
	
});
