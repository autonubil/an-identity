angular.module("autonubil-intranet").requires.push("autonubil-intranet-ldapusers");

angular.module("autonubil-intranet-ldapusers", [ "angular-plugin", "restangular", "autonubil-intranet-ldap", "mp.datePicker" ]);
angular.module("autonubil-intranet-ldapusers")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for ldapusers ... ");

	PluginMenuService.addItem("/main/admin", "/ldapusers_users", {
		visible: true,
		title : "LDAP Users"
	}, {
		controller : "LdapUserListController",
		templateUrl : "ldapusers/templates/ldap_users.html" 
	});

	PluginMenuService.addItem("/main/admin", "/ldapusers_groups", {
		visible: true,
		title : "LDAP Groups"
	}, {
		controller : "LdapGroupListController",
		templateUrl : "ldapusers/templates/ldap_groups.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/ldapusers/edit_user/:connectionId/:userId", {
		controller : "LdapUserEditController",
		templateUrl : "ldapusers/templates/ldap_user.html" 
	});

	PluginMenuService.addRoute("/main/admin/ldapusers/edit_group/:connectionId/:groupId", {
		controller : "LdapGroupEditController",
		templateUrl : "ldapusers/templates/ldap_group.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/ldapusers/add_user/:connectionId", {
		controller : "LdapUserAddController",
		templateUrl : "ldapusers/templates/ldap_user_add.html" 
	});
	
	
});

angular.module("autonubil-intranet-ldap")
.controller("LdapGroupEditController", function($scope,AuthService,LdapGroupService,$routeParams) {

	$scope.changed = false;
	
	$scope.enablePassword = false;
	$scope.password = "";
	
	$scope.update = function() {
		LdapGroupService.get($routeParams.connectionId,$routeParams.groupId,function(group){
			$scope.group = group;
			$scope.changed = false;
		});
	};
	
	$scope.update();
	
	
	$scope.formatDate = function(date) {
		return date.getTime();
	};
	
})
angular.module("autonubil-intranet-ldap")
.controller("LdapGroupListController", function($scope,AuthService,LdapConfigService,LdapGroupService,$location,$routeParams) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 25,
			config : $routeParams["source"] || ""
	};

	LdapConfigService.getList({},function(configs) {
		if($scope.search.config == "") {
			$scope.search.config=configs[0].id
		}
		$scope.configs = configs;
		$scope.update();
	});

	$scope.update = function() {
		LdapGroupService.getList($scope.search.config, $scope.search, function(groups){
			$scope.groups = groups;
		});
	}
	
	$scope.setSource = function() {
		console.log("source: "+$scope.search.config)
		$location.search("source",$scope.search.config);
	}

	$scope.updateTyping = _.debounce($scope.update,400);
	
})
angular.module("autonubil-intranet-ldap")
.service("LdapGroupService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(connectionId, params, success, error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/groups").getList(params).then(success,error);
		},
		get : function(connectionId,groupId,success) {
			return Restangular.one("autonubil/api/ldapusers/"+connectionId+"/groups",groupId).get().then(
					function(user) {
						var a = [];
						for (var key in user.attributes) {
							a.push({ key : key, value : user.attributes[key] })
						}
						user.attributes = a;
					    console.log(user);
						success(user);
					}
			);
		}
	};
	
});

angular.module("autonubil-intranet-ldap")
.controller("LdapUserAddController", function($scope,AuthService,LdapConfigService,LdapUserService,$routeParams,$location) {

	
	$scope.expiry = [
		{
			value :  Date.now()+(1000*60*60*24*30*6),
			display : "6 months"
		},
		{
			value :  Date.now()+(1000*60*60*24*30*3),
			display : "3 months"
		},
		{
			value :  Date.now()+(1000*60*60*24*30*1),
			display : "1 month"
		},
		{
			value :  -1,
			display : "never"
		}
	];
	
	$scope.user = {
		userExpires : $scope.expiry[0].value
	};

	
	LdapConfigService.get($routeParams.connectionId,function(config) {
		$scope.config = config;
	})
	
	$scope.create = function() {
		LdapUserService.create($routeParams.connectionId,$scope.user,function(user) {
			console.log("success: ",user);
			$location.path("/main/admin/ldapusers/edit_user/"+$routeParams.connectionId+"/"+user.id);
		},function(response) {
			console.log("error: ",response);
			$scope.error = response;
		});
	};
	
	
})
angular.module("autonubil-intranet-ldap")
.controller("LdapUserEditController", function($scope,AuthService,LdapConfigService,LdapUserService,LdapGroupService,$routeParams) {

	$scope.enablePassword = false;
	$scope.password = "";
	
	$scope.groupList = {
			groups : [],
			search : ""
	};
	
	$scope.state = {
			changed : false,
			editMode : false,
			showPasswordExpiryPicker : false,
			showUserExpiryPicker : false
	} 
	
	$scope.expiry = [
		{
			value :  Date.now()+(1000*60*60*24*30*6),
			display : "6 months"
		},
		{
			value :  Date.now()+(1000*60*60*24*30*3),
			display : "3 months"
		},
		{
			value :  Date.now()+(1000*60*60*24*30*1),
			display : "1 month"
		},
		{
			value :  Date.now(),
			display : "now"
		},
		{
			value :  -1,
			display : "never"
		}
	];
	
	
	$scope.source=$routeParams.connectionId;
	
	$scope.update = function() {
		
		$scope.alterEgos = [];
		console.log("updating user ... ");

		LdapUserService.get($routeParams.connectionId,$routeParams.userId,function(user){
			$scope.user = user;
			$scope.changed = false;
			console.log("got user: "+user.username);
			LdapConfigService.getList({},function(configs) {
				_.forEach(configs,function(config){
					if(config.id!=$scope.source) {
						LdapUserService.getList(config.id, { username : user.username }, function(users) {
							_.forEach(users,function(user){
								alterEgo = {
										source : {
											name : config.name,
											id : config.id
										},
										user : {
											id : user.id,
											displayName : user.displayName, 
											username : user.username,
										}
								}
								$scope.alterEgos.push(alterEgo)
							});
						});
					}
				}) 
			});
			
		});
		LdapUserService.getGroups($routeParams.connectionId,$routeParams.userId,function(groups){
			$scope.groups = groups;
			$scope.searchGroupsInternal();
		});
	};
	
	$scope.formatDate = function(date) {
		return date.getTime();
	};
	
	$scope.setUserExpiry = function(date) {
		$scope.changed = true;
		console.log(" ----- setting user expiry .... ");
		LdapUserService.setUserExpiry($routeParams.connectionId,$routeParams.userId,date,$scope.update,$scope.update);
	};
	
	$scope.setPasswordExpiry = function(date) {
		$scope.changed = true;
		LdapUserService.setPasswordExpiry($routeParams.connectionId,$routeParams.userId,date,$scope.update,$scope.update);
	}
	
	$scope.saveInternal = _.debounce(function() {
		console.log("save .... after debounce");
		$scope.error = undefined;
		LdapUserService.save($scope.user,
			function(user) {
				$scope.state.changed = false;
				$scope.user = user;
			},
			function(response) {
				console.log(response);
				$scope.error = response;
			}
		);
	},500);
	
	$scope.save = function() {
		console.log("save .... ");
		$scope.state.changed = true;
		$scope.saveInternal();
	}
	
	$scope.checkCustomFields = function() {
		_.forEach($scope.user.customFields, function(cf) {
			a = [];
			_.forEach(cf.values, function(v) {
				if(v!='') a.push(v);
			});
			cf.values = a;
		});
		$scope.save();
	}
	
	$scope.addGroup = function(id) {
		$scope.error = undefined;
		console.log("adding user to group: "+id);
		LdapUserService.addUserToGroup($scope.user,id,
				$scope.update, 
				function(response) {
					$scope.error = response;
				}
		);
	};
	
	$scope.removeGroup = function(id) {
		$scope.error = undefined;
		console.log("removing user from group: "+id);
		LdapUserService.removeUserFromGroup($scope.user,id,
				$scope.update, 
				function(response) {
			$scope.error = response;
		}
		);
	};
	
	$scope.searchGroupsInternal = function() {
		params = {search:$scope.groupList.search};
		console.log("updating group list ... ",params);
		LdapGroupService.getList($routeParams.connectionId,params,
			function(groups) {
				$scope.groupList.groups = [];
				_.forEach(groups, function(g){
					add = true;
					_.forEach($scope.groups,function(x) {
						if(x.displayName == g.displayName) {
							add = false;
						}
					});
					if(add) {
						$scope.groupList.groups.push(g);
					}
				});
			}
		);
	};
	
	$scope.toggleEdit = function() {
		$scope.state.editMode = !$scope.state.editMode;
		if(!$scope.state.editMode) {
			$scope.save();
		}
	}
	
	$scope.searchGroups = _.debounce($scope.searchGroupsInternal,500);
	
	$scope.update();
	
})
angular.module("autonubil-intranet-ldap")
.controller("LdapUserListController", function($scope,AuthService,LdapConfigService,LdapUserService,$location,$routeParams) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 25,
			config : $routeParams["source"] || ""
	};
	
	LdapConfigService.getList({},function(configs) {
		if($scope.search.config == "") {
			$scope.search.config = configs[0].id;
		}
		$scope.configs = configs;
		$scope.update();
	});

	$scope.update = function() {
		LdapUserService.getList($scope.search.config, $scope.search, function(users){
			$scope.users = users;
		});
	}
	
	$scope.setSource = function() {
		console.log("source: "+$scope.search.config)
		$location.search("source",$scope.search.config);
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
})
angular.module("autonubil-intranet-ldap")
.service("LdapUserService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(connectionId, params, success) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users").getList(params).then(success);
		},
		setUserExpiry : function(connectionId, userId, date, success, error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users/"+userId+"/expiration_user").customPUT({},"",{date:date}).then(success,error);
		},
		setPasswordExpiry : function(connectionId, userId, date, success, error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users/"+userId+"/expiration_password").customPUT({},"",{date:date}).then(
					function() {
						console.log("success");
						success();
					},
					function() {
						console.log("error");
						success();
					}
			);
		},
		get : function(connectionId,userId,success) {
			return Restangular.one("autonubil/api/ldapusers/"+connectionId+"/users",userId).get().then(
					function(user) {
						var a = [];
						for (var key in user.attributes) {
							a.push({ key : key, value : user.attributes[key] })
						}
						user.attributes = a;
						success(user);
					}
			);
		},
		create : function(connectionId,user,success,error) {
			return Restangular.all("autonubil/api/ldapusers/"+connectionId+"/users").post(user).then(success,error);
		},
		save : function(user,success,error) {
			user.attributes = undefined;
			user.put().then(success,error);
		},
		getGroups : function(connectionId,userId,success) {
			return Restangular.one("autonubil/api/ldapusers/"+connectionId+"/users/"+userId,"groups").get().then(success);
		},
		addUserToGroup : function(connectionId,userId,groupId,success,error) {
			
		},
		removeUserFromGroup : function(user,groupId,success,error) {
			Restangular.one("autonubil/api/ldapusers/"+user.sourceId+"/users/"+user.id,"groups").customDELETE("",{groupId: groupId}).then(success,error);
		},
		addUserToGroup : function(user,groupId,success,error) {
			user.post("groups",{},{groupId: groupId}).then(success,error);
		}
		
	};
	
});
