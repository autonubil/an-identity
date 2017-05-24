
angular.module("autonubil-intranet").requires.push("autonubil-intranet-apps");

angular.module("autonubil-intranet-apps", [ "angular-plugin", "restangular", "autonubil-intranet-ldap", "autonubil-intranet-ldapusers", "lr.upload" ]);
angular.module("autonubil-intranet-apps")
.run(function(PluginMenuService, PluginComponentService, $location) {

	PluginMenuService.addItem("/main/admin", "/apps", {
		title : "App Links",
		visible:true
	}, {
		controller : "AppListController",
		templateUrl : "apps/templates/apps.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/apps/:id", {
		controller : "AppEditController",
		templateUrl : "apps/templates/app.html" 
	});

});

angular.module("autonubil-intranet-mail")
.controller("AppEditController", function($scope,AuthService,AppService,LdapConfigService,LdapGroupService,$routeParams) {

	$scope.changed = false;
	
	$scope.newPermission = {
			source : {},
			group : {},
			name : "",
	};

	LdapConfigService.getList({},function(configs){
		$scope.configs=configs;
	});

	
	$scope.update = function() {
		AppService.get($routeParams.id,function(app){
			$scope.app = app;
		});
		AppService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});
	};
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.source.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.source.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	

	$scope.save = _.debounce(function() {
		AppService.save($scope.app,function(app) {
			$scope.app = app;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		AppService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		AppService.addPermission(
				$routeParams.id,
				{
					appId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
	$scope.uploadSuccess = function() {
		s = $("#icon").attr("src")+"?"+Math.random();
		console.log("image uploaded ... new image: "+s);
		$("#icon").attr("src", s);
	}
	
	$scope.update();
	
})
angular.module("autonubil-intranet-mail")
.controller("AppListController", function($scope,AuthService,AppService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		AppService.getList($scope.search, function(apps){
			$scope.apps = apps;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving app ... ");
		var x = { name : $scope.search.search, url : "http://www.example.com" };
		AppService.add(
				x,
				function(app){
					console.log("app saved... ",app);
					$location.path("/main/admin/apps/"+app.id);
				}
		);
	}
	
	$scope.update();
	
	
})
angular.module("autonubil-intranet-apps")
.service("AppService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/apps/apps").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/apps/apps",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/apps/apps").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/apps/apps",id).remove().then(success);
		},
		save : function(app,success) {
			return app.put().then(success);
		},
		listPermissions : function(id,success) {
			return Restangular.all("autonubil/api/apps/apps/"+id+"/permissions").getList({}).then(success);
		},
		addPermission : function(appId,permission,success) {
			return Restangular.all("autonubil/api/apps/apps/"+appId+"/permissions").customPOST(permission).then(success);
		},
		deletePermission : function(appId,sourceId,groupId,success) {
			return Restangular.all("autonubil/api/apps/apps/"+appId+"/permissions").customDELETE("",{sourceId:sourceId,groupId:groupId}).then(success);
		}
	};
	
});
