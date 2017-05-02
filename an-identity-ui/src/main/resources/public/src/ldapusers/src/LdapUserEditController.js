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