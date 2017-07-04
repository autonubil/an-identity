angular.module("templates").run(["$templateCache", function($templateCache) {$templateCache.put("ldap/templates/ldap_config.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2><a href=\"#/main/admin/ldap\">LDAP Configs</a> | {{ldapConfig.name}} <span ng-if=\"changed\">*</span></h2>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-6\">\n		<div class=\"row\">\n			<div class=\"col-md-12\">\n				<h4>Directory Server</h4>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"error\">\n				<div class=\"alert alert-danger\">\n					<h4>Error</h4>\n					{{error.data.message}}\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Name</label>\n					<input ng-model=\"ldapConfig.name\" ng-change=\"startChange()\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"checkbox\">\n				    <label>\n						<input type=\"checkbox\" ng-model=\"ldapConfig.useAsAuth\" ng-change=\"startChange()\"> Use for Authentication\n				    </label>\n				</div>\n			</div>\n			<div class=\"col-md-8\">\n				<div class=\"form form-group\">\n					<label>Host</label>\n					<input ng-model=\"ldapConfig.host\" ng-change=\"startChange()\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-4\">\n				<div class=\"form form-group\">\n					<label>Port</label>\n					<input ng-model=\"ldapConfig.port\" ng-change=\"startChange()\" class=\"form-control\"/>\n				</div>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"types\">\n				<div class=\"form form-group\">\n					<label>Server Type</label>\n					<select ng-model=\"ldapConfig.serverType\"  ng-options=\"type.id as type.name for type in types\" ng-change=\"startChange()\" class=\"form-control\">\n					</select>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Root DSE</label>\n					<input ng-model=\"ldapConfig.rootDse\" ng-change=\"startChange()\" class=\"form-control\">\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>OTP Group</label>\n					<input ng-model=\"ldapConfig.otpGroup\" ng-change=\"startChange()\" class=\"form-control\">\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>encryption</label>\n					<select ng-model=\"ldapConfig.encryption\" ng-change=\"startChange()\" class=\"form-control\">\n						<option>NONE</option>\n						<option>START_TLS</option>\n						<option>SSL</option>\n					</select>\n				</div>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"ldapConfig.encryption!=\'NONE\'\">\n				<div class=\"checkbox\">\n				    <label>\n						<input type=\"checkbox\" ng-model=\"ldapConfig.trustAll\" ng-change=\"startChange()\"> Trust All\n				    </label>\n				</div>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"ldapConfig.encryption!=\'NONE\' && !ldapConfig.trustAll\">\n				<div class=\"form form-group\">\n					<label>Trusted Certificates</label>\n					<div>\n						<textarea cols=50 rows=10 ng-model=\"ldapConfig.cert\" ng-change=\"startChange()\" class=\"form-control\"></textarea>\n					</div>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>authentication</label>\n					<select ng-model=\"ldapConfig.auth\" ng-change=\"startChange()\" class=\"form-control\">\n						<option>SIMPLE</option>\n						<option>DIGEST_MD5</option>\n						<option>CRAM_MD5</option>\n						<option>GSSAPI</option>\n					</select>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Admin DN</label>\n					<input ng-model=\"ldapConfig.adminBindDn\" ng-change=\"startChange()\" class=\"form-control\">\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<hr>\n			</div>\n			<div class=\"col-md-12\" ng-click=\"enablePassword=true\">\n				<label>Admin Password</label>\n			</div>\n			<div class=\"col-md-8\" ng-click=\"enablePassword=true\">\n				<div class=\"form form-group\">\n					<input ng-model=\"password\" type=\"password\" ng-disabled=\"!enablePassword\" class=\"form-control\">\n				</div>\n			</div>\n			<div class=\"col-md-4\">\n				<div class=\"form form-group\">\n					<button class=\"btn btn-default form-control\" ng-disabled=\"password.length==0\" ng-click=\"setPassword()\">Set</button>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<hr>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group pull-right\">\n					<button type=\"button\" class=\"form-control btn btn-primary\" ng-click=\"test()\" ng-class=\"[{\'btn-success\' : status.success},{\'btn-danger\':status.error}]\">Test Connection\n						<i ng-if=\"status.success\" class=\"glyphicon glyphicon-ok\"></i>\n						<i ng-if=\"status.error\" class=\"glyphicon glyphicon-remove\"></i>\n					</button>\n				</div>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"status.error\">\n				<div class=\"alert alert-danger\">\n					Error! {{status.exception.message}}\n				</div>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"status.exception.pems\">\n				<label>Certificates presented by the server</label>\n				<div ng-repeat=\"pem in status.exception.pems\">\n					<pre>{{pem}}</pre>			\n				</div>\n			</div>\n		</div>\n	</div>\n	<div class=\"col-md-6\">\n		<div class=\"row\">\n			<div class=\"col-md-12\">\n				<h4>Custom Fields</h4>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"fields.length==0\">\n				<div class=\"alert alert-warning\">\n					no fields configured\n				</div>\n			</div>\n			<div class=\"col-md-12\" ng-if=\"fields.length>0\">\n				<table class=\"table table-compact table-striped\">\n					<tr>\n						<th>Object Class</th>\n						<th>Attribute</th>\n						<th>Display Name</th>\n						<th></th>\n					</tr>\n					<tr ng-repeat=\"field in fields\">\n						<td>\n							{{field.objectClass}}\n						</td>\n						<td>\n							{{field.attributeName}}\n						</td>\n						<td>\n							{{field.displayName}}\n						</td>\n						<td>\n							<i class=\"glyphicon glyphicon-minus\" ng-click=\"deleteField(field.sourceId,field.id)\"> </i>\n						</td>\n					</tr>\n				</table>\n			</div>\n			<div class=\"col-md-12\">\n				<h4>Add Field</h4>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group\">\n					<label>Object Class</label>\n					<input ng-model=\"field.objectClass\" class=\"form-control\">\n				</div>\n				<div class=\"form form-group\">\n					<label>Attribute Name</label>\n					<input ng-model=\"field.attributeName\" class=\"form-control\">\n				</div>\n				<div class=\"form form-group\">\n					<label>Attribute Type</label>\n					<select ng-model=\"field.attributeType\" class=\"form-control\">\n						<option value=\"String\">String</option>\n						<option value=\"Text\">Text</option>\n						<option value=\"Number\">Number</option>\n						<option value=\"Date\">Date</option>\n					</select>\n				</div>\n				<div class=\"form form-group\">\n					<label>Display Name</label>\n					<input ng-model=\"field.displayName\" class=\"form-control\">\n				</div>\n				<div class=\"form form-group\">\n				  	<label><input type=\"checkbox\" ng-model=\"field.multi\">&nbsp;Multi-Value</label>\n				</div>\n				<div class=\"form form-group\">\n				  	<label><input type=\"checkbox\" ng-model=\"field.adminEditable\">&nbsp;Editable by admins</label>\n				</div>\n				<div class=\"form form-group\">\n				  	<label><input type=\"checkbox\" ng-model=\"field.userEditable\">&nbsp;Editable by Users (self service)</label>\n				</div>\n			</div>\n			<div class=\"col-md-12\">\n				<div class=\"form form-group pull-right\">\n					<span class=\"btn btn-primary\" ng-click=\"addField()\"><i class=\"glyphicon glyphicon-plus\"> </i></span>\n				</div>\n			</div>\n		</div>\n	</div>\n</div>\n");
$templateCache.put("ldap/templates/ldap_configs.html","<div class=\"row\">\n	<div class=\"col-md-12 page-header\">\n		<h2>LDAP Configs</h2>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-10\">\n		<div class=\"form-group\">\n			<input class=\"form-control\" ng-model=\"search.search\" ng-change=\"updateTyping()\">\n		</div>\n	</div>\n	<div class=\"col-md-2\">\n		<button class=\"btn btn-primary form-control\" ng-click=\"add()\">\n			<i class=\"glyphicon glyphicon-plus\"></i>\n		</button>\n	</div>\n</div>\n<div class=\"row\">\n	<div class=\"col-md-12\">\n		<table class=\"table table-compact table-striped\">\n			<tr ng-repeat=\"ldapConfig in ldapConfigs\">\n				<td>{{ldapConfig.name}}</td>\n				<td>{{ldapConfig.host}}:{{ldapConfig.port}}</td>\n				<td width=\"20\"><a href=\"#/main/admin/ldap/{{ldapConfig.id}}\" class=\"btn btn-primary\"><i class=\"glyphicon glyphicon-pencil\"> </i></a>\n				<td width=\"20\"><a href=\"#/main/admin/ldap\" ng-click=\"remove(ldapConfig.id)\" class=\"btn btn-danger\"><i class=\"glyphicon glyphicon-remove-circle\"> </i></a>\n			</tr>\n		</table>\n	</div>\n</div>\n");}]);