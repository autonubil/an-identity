
angular.module("autonubil-intranet").requires.push("autonubil-intranet-localauth");

angular.module("autonubil-intranet-localauth", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-localauth")
.run(function(PluginMenuService, PluginComponentService, $location) {

	console.log("register menu items for localauth ... ");
	
	PluginMenuService.addItem("/main/admin", "/localusers", {
		visible: true,
		title : "Local Users"
	}, {
		controller : "LocalAuthUserListController",
		templateUrl : "localauth/templates/localusers.html" 
	});

	PluginMenuService.addRoute("/main/admin/localusers/:id", {
		controller : "LocalAuthUserEditController",
		templateUrl : "localauth/templates/localuser.html" 
	});

});

angular.module("autonubil-intranet-localauth")
.controller("LocalAuthUserEditController", function($scope,AuthService,LocalAuthUserService,$routeParams) {

	$scope.changed = false;
	
	$scope.reset = {
			resetFinished : false,
			success: false,
			message: "",
			password : "",
			passwordRepeat : ""
	};
	
	LocalAuthUserService.get($routeParams.id,function(user){
		console.log(user);
		$scope.user = user;
		$scope.resetUrl = "/autonubil/api/localauth/users/"+user.id+"/otp";
	});

	$scope.save = _.debounce(function() {
		LocalAuthUserService.save($scope.user,function(user) {
			$scope.user = user;
			$scope.changed = false;
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save();
	};

	$scope.resetOTP = function() {
		LocalAuthUserService.resetOTP($scope.reset.token,function(secret) {
			$scope.secret = secret;
		});
	};
	
	$scope.resetPassword = function() {
		$scope.reset.resetFinished = false;
		LocalAuthUserService.resetPassword($scope.user.id, undefined, $scope.reset.password, 
			function() {
				$scope.reset = "(controller) reset OK!";
				$scope.reset.resetFinished = true;
				$scope.reset.success = true;
				
			},
			function() {
				$scope.reset = "(controller) reset ERROR!";
				$scope.reset.resetFinished = true;
				$scope.reset.success = false;
			}
		);
	};
	
})
angular.module("autonubil-intranet-localauth")
.controller("LocalAuthUserListController", function($scope,AuthService,LocalAuthUserService,$location) {

	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		LocalAuthUserService.getList($scope.search, function(users){
			$scope.users = users;
		});
		if($scope.search.search.length > 0) {
			$scope.enableAdd = false;
			LocalAuthUserService.getList({username:$scope.search.search}, function(users){
				if(users.length > 0) {
					$scope.enableAdd = false;
				} else {
					$scope.enableAdd = true;
				}
			});
		} else {
			$scope.enableAdd = false;
		};
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function() {
		LocalAuthUserService.add({username:$scope.search.search}, function(user) {
			$location.url($location.url()+"/"+user.id);
		});
		
	}

	$scope.remove = function(id) {
		LocalAuthUserService.remove(id, function() {
			$scope.update();
		});
		
	}
	
	$scope.update();
	
	
})
angular.module("autonubil-intranet-localauth")
.service("LocalAuthUserService", function(Restangular,$location, $interval) {
	
	return {
		resetPassword : function(id, oldPassword, newPassword, success, error) {
			return Restangular.one("autonubil/api/localauth/users/"+id+"/password").customPOST(undefined,"",{"oldPassword" : oldPassword, "newPassword" : newPassword}).then(success,error);
		},
		resetOTP : function(id, success) {
			return Restangular.one("autonubil/api/localauth/users/"+id+"/otp").customPUT({}).then(success);
		},
		getList : function(params, success) {
			return Restangular.all("autonubil/api/localauth/users").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/localauth/users",id).get().then(success);
		},
		add : function(user,success) {
			return Restangular.all("autonubil/api/localauth/users").post(user).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/localauth/users",id).remove().then(success);
		},
		save : function(user,success) {
			return user.put().then(success);
		}
	};
	
});
