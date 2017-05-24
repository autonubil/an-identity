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
