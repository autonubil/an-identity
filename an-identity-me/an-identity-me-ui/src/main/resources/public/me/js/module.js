
angular.module("autonubil-intranet").requires.push("autonubil-intranet-me");

angular.module("autonubil-intranet-me", [ "angular-plugin", "restangular","autonubil-intranet-auth" ]);
angular.module("autonubil-intranet-me")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	console.log("register menu items for me ... ");
	
	var meMenuItem = {
			visible: false,
			title : "Me"
		};
	
	PluginMenuService.addItem("/main", "/me", meMenuItem, { templateUrl : "me/templates/me.html" });
	
	$rootScope.$on("authChanged",function(e,status) {
		meMenuItem.visible = status.loggedIn;
	});
	
	
	myInfo = {
		visible : true,
		title : "My Accounts",
		templateUrl : "me/templates/my_info.html" 
	};
	
	
	PluginComponentService.addItem("/me",myInfo);
	
	myApps = {
		visible : true,
		status : "My Apps",
		templateUrl : "me/templates/my_apps.html"
	};
	
	
	PluginComponentService.addItem("/me",myApps); 
	PluginComponentService.addItem("/dashboard",myApps); 
	
});

angular.module("autonubil-intranet-me")
.controller("MeController", function($scope) {
	
	$scope.active = {
			title : ""
	};
	
});

angular.module("autonubil-intranet-me")
.service("MeService", function(Restangular,$location) {
	
	return {
		getUsers : function(params, success) {
			return Restangular.all("autonubil/api/authentication").customGET("authenticate").then(success);
		},
		getApps : function(params,success) {
			return Restangular.all("autonubil/api/apps/my_apps").getList(params).then(success);
		},
	};
	
	
});

angular.module("autonubil-intranet-me")
.controller("MyAppsController", function($scope,MeService,AuthService,$location) {
	
	$scope.search =  "";

	$scope.updateApps = _.debounce(function() {
		MeService.getApps({search:$scope.search},function(apps){
			$scope.apps = apps;
		});
	},250);
	
	$scope.updateApps();
	
});

angular.module("autonubil-intranet-me")
.controller("MyInfoController", function($scope,MeService,AuthService,$location) {
	
	$scope.error = {};
	$scope.success = {};
	
	MeService.getUsers({},function(identity){
		$scope.identity = identity;
	});
	
	$scope.reset = function(username,userId,sourceId) {
		reset = {
			sourceId : sourceId,
			username : username,
			oldPassword : $scope.reset[userId].oldPassword,
			newPassword : $scope.reset[userId].newPassword
		};
		$scope.error[userId] = undefined;
		$scope.success[userId] = undefined;
		AuthService.reset(
			reset,
			function() {
				$scope.success[userId] = "Success!";
			}, 
			function(response) {
				$scope.error[userId] = response;
			}
		);
	};
	
	
});
