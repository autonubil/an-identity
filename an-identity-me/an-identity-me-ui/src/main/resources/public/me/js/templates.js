angular.module("templates").run(["$templateCache", function($templateCache) {$templateCache.put("me/templates/me.html","<div class=\"row\" ng-controller=\"MeController\">\n	<div class=\"col-md-12 page-header\">\n		<h2>Me</h2>\n	</div>\n	<div class=\"col-md-12\" include-components path=\"/me\">\n		<div class=\"row\" ng-include=\"item.templateUrl\">\n		</div>\n	</div>\n</div>\n	\n\n");
$templateCache.put("me/templates/my_apps.html","<div class=\"col-md-12\" ng-controller=\"MyAppsController\">\n	<div ng-if=\"apps && apps.length > 0\">\n		<h4>Applications</h4>\n		<div ng-if=\"apps && apps.length > 5\">\n			<div class=\"form-group\">\n				<input ng-model=\"search\" class=\"form-control\"\n					ng-change=\"updateApps()\" placeholder=\"type to filter\">\n			</div>\n		</div>\n		<ul class=\"list-group\">\n			<li class=\"row list-group-item\" ng-repeat=\"app in apps\">\n				<div class=\"col-md-1\">\n					<a target=\"{{app.id}}\" href=\"{{app.url}}\"><img\n						src=\"/autonubil/api/apps/apps/{{app.id}}/icon\"\n						style=\"max-height: 3.2em; min-height: 3.2em\" /></a>\n				</div>\n				<div class=\"col-md-11\">\n					<label><a target=\"{{app.id}}\" href=\"{{app.url}}\">{{app.name}}</a></label><br />\n					{{app.description}}\n				</div>\n			</li>\n		</ul>\n	</div>\n</div>\n");
$templateCache.put("me/templates/my_info.html","<div class=\"col-md-12\" ng-controller=\"MyInfoController\" >\n	<h4>Accounts</h4>\n	<div ng-repeat=\"user in identity.linked\">\n		<div class=\"well well-sm\"> \n			<span class=\"pull-right\" ng-click=\"collpase[user.id] = !collpase[user.id]\"><i class=\"glyphicon glyphicon-chevron-down\"> </i></span>\n			<h4>Account: {{user.displayName}} ({{user.username}}) | {{user.sourceName}}</h4> \n			<div class=\"form-group\" ng-if=\"!collpase[user.id]\">\n				<label>Groups</label><br>\n				<div class=\"alert alert-warning\" ng-if=\"user.groups.length == 0\">\n					no groups\n				</div>\n				<span class=\"badge\" style=\"margin-bottom: 8px; margin-right:8px;\" ng-repeat=\"group in user.groups\">{{group.displayName}}</span> \n			</div>\n			<div class=\"form-group\" ng-if=\"!collpase[user.id]\">\n				<label>Notifications</label><br>\n				<div class=\"alert alert-warning\" ng-if=\"user.notifications.length == 0\">\n					no notifications\n				</div>\n				<div ng-repeat=\"notification in user.notifications\">\n					<strong ng-class=\"[\'label\',{\'label-danger\':notification.level==\'ERROR\'},{\'label-warning\':notification.level==\'WARN\'},{\'label-info\':notification.level==\'INFO\'}]\">{{notification.level}}</strong> {{notification.message}}<br>\n				</div>\n			</div>\n			<div class=\"form-inline\" ng-if=\"!collpase[user.id]\">\n				<label>Password Reset</label><br>\n				<div class=\"alert alert-danger\" ng-if=\"error[user.id]\">Error! {{error[user.id].data.message}}</div>\n				<div class=\"alert alert-success\" ng-if=\"success[user.id]\">Success!</div>\n				<input class=\"form-control \" placeholder=\"Old Password\" ng-model=\"reset[user.id].oldPassword\" type=\"password\">\n				<input class=\"form-control\" placeholder=\"New Password\" ng-model=\"reset[user.id].newPassword\" type=\"password\">\n				<input class=\"form-control\" placeholder=\"New Password Repeat\" ng-model=\"reset[user.id].newPasswordRepeat\" type=\"password\">\n				<button class=\"btn btn-primary\" ng-click=\"reset(user.username,user.id,user.sourceId)\" ng-disabled=\"!(reset[user.id].oldPassword.length>0 && reset[user.id].newPassword.length>0 && reset[user.id].newPassword == reset[user.id].newPasswordRepeat)\">Reset Password</button>\n			</div>												\n		</div>\n		<hr>\n	</div>\n</div>\n");}]);