
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
