angular.module("templates").run(["$templateCache", function($templateCache) {$templateCache.put("ldapusers/templates/ldap_group.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2><a href=\"#/main/admin/ldapusers_groups\">LDAP Group</a> | {{group.displayName}} <span ng-if=\"changed\">*</span></h2>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-12\">\n		<div class=\"row\">\n			<div class=\"col-md-12\">\n				<h4>Core Data</h4>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Display Name</label>\n					<input ng-model=\"group.displayName\" disabled=\"true\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>DN</label>\n					<input ng-model=\"group.dn\" disabled=\"true\" class=\"form-control\"/>\n				</div>\n			</div>\n		</div>\n	</div>\n</div>\n");
$templateCache.put("ldapusers/templates/ldap_groups.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2>LDAP Groups</h2>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-2\" ng-if=\"configs\">\n		<select class=\"form-control\" ng-model=\"search.config\" ng-change=\"setSource()\" ng-options=\"config.id as config.name for config in configs\">\n		</select>\n	</div>\n	<div class=\"col-md-8\">\n		<div class=\"form-group\">\n			<input class=\"form-control\" ng-model=\"search.search\" ng-change=\"updateTyping()\">\n		</div>\n	</div>\n	<div class=\"col-md-2\">\n		<a href=\"#/main/admin/ldapusers/add/{{search.config}}\" class=\"form-control btn btn-primary\"><i class=\"glyphicon glyphicon-plus\"> </i></a>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-12\">\n		<div ng-if=\"!groups || groups.length==0\">\n			<div class=\"alert alert-warning col-md-12\">\n				<i>No groups found</i>\n			</div>\n		</div> \n		<table class=\"table table-compact table-striped\">\n			<tr ng-repeat=\"group in groups\">\n				<td>{{group.displayName}}</td>\n				<td width=\"200\">{{user.username}}</td>\n				<td width=\"20\"><a href=\"#/main/admin/ldapusers/edit_group/{{search.config}}/{{group.id}}\" class=\"btn btn-primary\"><i class=\"glyphicon glyphicon-pencil\"> </i></a>\n			</tr>\n		</table>\n	</div>\n</div>\n");
$templateCache.put("ldapusers/templates/ldap_user.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2><a href=\"#/main/admin/ldapusers_users?source={{source}}\">LDAP Users</a> | {{user.cn}} ({{user.sourceName}})<span ng-if=\"state.changed\">*</span></h2>\n	</div>\n</div>\n<div class=\"row\" ng-if=\"user\">\n	<div class=\"col-md-12\" ng-if=\"error\">\n		<div class=\"alert alert-danger\">\n			<h4>Error</h4>\n			{{error.data.message}}\n		</div>\n	</div>\n	<div class=\"col-md-12\">\n		<div class=\"row\">\n			<div class=\"col-md-3\">\n				<div class=\"form form-group well well-sm\">\n					<label>username</label><br>\n					{{user.username}}\n					<!-- \n					<input ng-model=\"\" ng-disabled=\"true\"  ng-change=\"save()\" class=\"form-control\"/>\n					 -->\n				</div>\n			</div>\n			<div class=\"col-md-3\">\n				<div class=\"form form-group well well-sm\">\n					<label>account name</label><br>\n					{{user.accountName}}\n					<!-- \n					<input ng-model=\"user.accountName\" ng-disabled=\"true\"  ng-change=\"save()\" class=\"form-control\"/>\n					 -->\n				</div>\n			</div>\n			<div class=\"col-md-6\">\n				<div class=\"form form-group well well-sm\">\n					<label>DN</label><br>\n					{{user.dn}}\n					<!-- \n					<input ng-model=\"user.dn\" ng-disabled=\"true\"  ng-change=\"save()\" class=\"form-control\"/>\n					 -->\n				</div>\n			</div>\n		</div>\n		<div class=\"row\">\n			<div class=\"col-md-12\"><hr>\n			</div>\n		</div>\n		<div class=\"row\">\n			<div class=\"col-md-6\">\n				<div class=\"row\">\n					<div class=\"col-md-12\">\n						<h4>User Data <i class=\"glyphicon glyphicon-pencil\" ng-click=\"toggleEdit()\"></i></h4>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Display Name</label>\n							<input ng-model=\"user.displayName\" ng-disabled=\"!state.editMode\" ng-change=\"state.changed=true\" class=\"form-control\">\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Common Name</label>\n							<input ng-model=\"user.cn\" ng-disabled=\"!state.editMode\" ng-change=\"state.changed=true\" class=\"form-control\">\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Surname</label>\n							<input ng-model=\"user.sn\" ng-disabled=\"!state.editMode\" ng-change=\"state.changed=true\" class=\"form-control\">\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Phone</label>\n							<input ng-model=\"user.phone\" ng-disabled=\"!state.editMode\" ng-change=\"state.changed=true\" class=\"form-control\">\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Mobile</label>\n							<input ng-model=\"user.mobilePhone\" ng-disabled=\"!state.editMode\" ng-change=\"state.changed=true\" class=\"form-control\">\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>E-Mail</label>\n							<input ng-model=\"user.mail\" ng-disabled=\"!state.editMode\"  ng-change=\"state.changed=true\" class=\"form-control\"/>\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Organization</label>\n							<input ng-model=\"user.organization\" ng-disabled=\"!state.editMode\"  ng-change=\"state.changed=true\" class=\"form-control\"/>\n						</div>\n					</div>\n					<div class=\"col-md-12\">\n						<div class=\"form form-group\">\n							<label>Department</label>\n							<input ng-model=\"user.department\" ng-disabled=\"!state.editMode\"  ng-change=\"state.changed=true\" class=\"form-control\"/>\n						</div>\n					</div>\n					<div class=\"col-md-12 form form-group\" ng-if=\"user.passwordExpires < 1\">\n						<label>Password does not expire</label>\n					</div>\n					<div class=\"col-md-12 form form-group\" ng-click=\"state.showPasswordExpiryPicker=!state.showPasswordExpiryPicker\" ng-if=\"user.passwordExpires > 0\">\n						<label>Password Expires</label>\n						{{ user.passwordExpires | date : format : timezone}}\n					</div>\n					<div class=\"col-md-6 form form-group\" ng-if=\"user.userExpires < 1\">\n						<label>User does not expire</label>\n					</div>\n					<div class=\"col-md-6 form form-group\" ng-click=\"state.showUserExpiryPicker=!state.showUserExpiryPicker\" ng-if=\"user.userExpires > 0\">\n						<label>User Expires</label>\n						{{ user.userExpires | date : format : timezone}}\n					</div>\n					<div class=\"col-md-6 form form-group\">\n						<select ng-model=\"user.userExpires\" placeholder=\"selectToUpdate\" ng-options=\"expires.value as expires.display for expires in expiry\" ng-disabled=\"!state.editMode\"   ng-change=\"setUserExpiry(user.userExpires)\"  class=\"form-control\">\n						</select>\n					</div>\n					<div class=\"col-md-12 form form-group\" ng-if=\"state.showUserExpiryPicker\">\n						<date-picker ng-model=\"user.userExpires\" format-date=\"formatDate\" ng-change=\"setUserExpiry(user.userExpires)\">\n						</date-picker>\n					</div>\n				</div>\n			</div>\n			<div class=\"col-md-6\">\n				<div class=\"row\">\n					<div class=\"col-md-12\">\n						<h4>Alter Egos</h4>\n						<label>Accounts in other authentication sources</label>\n					</div>\n					<div class=\"col-md-12\" ng-if=\"!alterEgos || alterEgos.length==0\">\n						<div class=\"alert alert-warning\">\n							<i>No alter egos</i>\n						</div>\n					</div>\n					<div class=\"col-md-12\" ng-if=\"alterEgos && alterEgos.length>0\">\n						<label>&nbsp;</label>\n						{{alterEgo}}\n						<table class=\"table table-striped table-compact\">\n							<tr ng-repeat=\"alterEgo in alterEgos\">\n								<td width=\"50%\"><a href=\"#/main/admin/ldapusers/edit_user/{{alterEgo.source.id}}/{{alterEgo.user.id}}\">{{alterEgo.source.name}}</a></td>\n								<td width=\"50%\"><a href=\"#/main/admin/ldapusers/edit_user/{{alterEgo.source.id}}/{{alterEgo.user.id}}\">{{alterEgo.user.cn}}</a></td>\n							</tr>\n						</table>\n					</div>\n					<div class=\"col-md-12 form form-group\">\n						<h4>Object Classes</h4>\n						<label>Object classes assigned in the underlying LDAP</label>\n						<!-- \n						<div style=\"max-height: 20em; min-height: 20em; overflow:scroll; overflow-x: hidden;\">\n						 -->\n						 <div></div>\n							<table class=\"table table-striped table-compact\">\n								<tr ng-repeat=\"objectClass in user.objectClasses\">\n									<td>{{objectClass}}</td>\n								</tr>\n							</table>\n							\n						</div>\n					</div>\n				</div>\n			</div>\n		</div>\n		<div class=\"col-md-12\">\n			<div class=\"row\">\n				<div class=\"col-md-12\">\n					<hr>\n				</div>\n			</div>\n		</div>\n		<div class=\"col-md-12\">\n			<div class=\"row\">\n				<div class=\"col-md-12\">\n					<h4>Other Fields</h4>\n				</div>\n				<div class=\"col-md-4\" ng-repeat=\"customField in user.customFields\">\n					<label>{{customField.displayName}}</label><br>\n					<div ng-if=\"customField.multi\">\n						<div class=\"form-group\" ng-repeat=\"value in customField.values track by $index\">\n							<input class=\"form-control\" ng-model=\"customField.values[$index]\" ng-change=\"checkCustomFields()\" ng-if=\"customField.attributeType==\'String\'\">\n							<textarea class=\"form-control\" cols=\"40\" rows=\"5\" ng-model=\"customField.values[$index]\" ng-change=\"checkCustomFields()\" ng-if=\"customField.attributeType==\'Text\'\"></textarea>\n						</div>\n						<i class=\"glyphicon glyphicon-plus\" ng-click=\"customField.values.push(\'\')\"></i>\n					</div>\n					<div ng-if=\"!customField.multi\">\n						<input class=\"form-control\" ng-model=\"customField.values[0]\">\n					</div>\n				</div>\n			</div>\n		</div>\n		<div class=\"col-md-12\">\n			<div class=\"row\">\n				<div class=\"col-md-12\">\n					<hr>\n				</div>\n			</div>\n		</div>\n		<div class=\"col-md-12\">\n			<div class=\"row\">\n				<div class=\"col-md-12\">\n					<h4>Groups</h4>\n				</div>\n			</div>\n			<div class=\"row\">\n				<div class=\"col-md-6\">\n					<label>Assigned Groups</label>\n					<div class=\"alert alert-warning\" ng-if=\"!groups || groups.length==0\">\n						<i>No groups</i>\n					</div>\n					<div style=\"max-height: 20em; min-height: 20em; overflow:scroll; overflow-x: hidden;\" ng-if=\"groups && groups.length>0\">\n						<table class=\"table table-striped table-compact\">\n							<tr ng-repeat=\"group in groups\">\n								<td>{{group.displayName}}</td>\n								<td width=\"20\"><a><i ng-click=\"removeGroup(group.id)\" class=\"glyphicon glyphicon-minus\"></i></a></td>\n							</tr>\n						</table>\n					</div>\n				</div>\n				<div class=\"col-md-6\">\n					<label>Available groups</label>\n					<input class=\"form-control\" ng-model=\"groupList.search\" ng-change=\"searchGroups()\" style=\"margin-bottom: 1em\">\n					<div style=\"max-height: 20em; min-height: 20em; overflow:scroll; overflow-x: hidden;\">\n						<div class=\"alert alert-warning\" ng-if=\"groupList.groups<1\">\n							<i>No matching groups</i>\n						</div>\n						<table class=\"table table-striped table-compact\">\n							<tr ng-repeat=\"group in groupList.groups\">\n								<td>{{group.displayName}}</td>\n								<td width=\"20\"><a><i ng-click=\"addGroup(group.id)\" class=\"glyphicon glyphicon-plus\"></i></a></td>\n							</tr>\n						</table>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n");
$templateCache.put("ldapusers/templates/ldap_user_add.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2><a href=\"#/main/admin/ldapusers_users\">LDAP Users</a> | Add User ({{config.name}})</h2>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-12\">\n		<div class=\"row\">\n			<div class=\"col-md-12\">\n				<h4>Core Data</h4>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"error\">\n				<div class=\"alert alert-danger\">\n					<h4>Error</h4>\n					{{error.data.message}}\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Display Name</label>\n					<input ng-model=\"user.displayName\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Common Name</label>\n					<input ng-model=\"user.cn\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Surname</label>\n					<input ng-model=\"user.sn\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>username</label>\n					<input ng-model=\"user.username\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>E-Mail</label>\n					<input ng-model=\"user.mail\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Organization</label>\n					<input ng-model=\"user.organization\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Department</label>\n					<input ng-model=\"user.department\" class=\"form-control\"/>\n				</div>\n			</div>\n			\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Company</label>\n					<input ng-model=\"user.company\" class=\"form-control\"/>\n				</div>\n			</div>\n			\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Expires</label>\n					<select ng-model=\"user.userExpires\" ng-options=\"expires.value as expires.display for expires in expiry\"   class=\"form-control\">\n					</select>\n				</div>\n			</div>\n		</div>\n		<div class=\"row\">\n			<div class=\"col-md-12\">\n				<button type=\"submit\" class=\"btn btn-primary pull-right\" ng-click=\"create()\">Create!</button>\n			</div>\n		</div>\n	</div>\n</div>\n");
$templateCache.put("ldapusers/templates/ldap_users.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2>LDAP Users</h2>\n	</div>\n</div>\n<div class=\"row\" ng-if=\"!configs || configs.length==0\">\n	<div class=\"col-md-12\">\n		<div class=\"alert alert-warning col-md-12\">\n			<i>No LDAP servers configured!</i>\n		</div>\n	</div>\n</div>\n<div class=\"row\" ng-if=\"configs && configs.length > 0\">\n	<div class=\"col-md-2\" ng-if=\"configs\">\n		<select class=\"form-control\" ng-model=\"search.config\" ng-change=\"setSource()\" ng-options=\"config.id as config.name for config in configs\">\n		</select>\n	</div>\n	<div class=\"col-md-8\">\n		<div class=\"form-group\">\n			<input class=\"form-control\" ng-model=\"search.search\" ng-change=\"updateTyping()\">\n		</div>\n	</div>\n	<div class=\"col-md-2\">\n		<a href=\"#/main/admin/ldapusers/add_user/{{search.config}}\" class=\"form-control btn btn-primary\"><i class=\"glyphicon glyphicon-plus\"> </i></a>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-12\">\n		<div ng-if=\"!users || users.length==0\">\n			<div class=\"alert alert-warning col-md-12\">\n				<i>No users found</i>\n			</div>\n		</div> \n		<table class=\"table table-compact table-striped\">\n			<tr ng-repeat=\"user in users\">\n				<td>{{user.cn}}</td>\n				<td width=\"200\">{{user.username}}</td>\n				<td width=\"20\"><a href=\"#/main/admin/ldapusers/edit_user/{{search.config}}/{{user.id}}\" class=\"btn btn-primary\"><i class=\"glyphicon glyphicon-pencil\"> </i></a>\n			</tr>\n		</table>\n	</div>\n</div>\n");}]);