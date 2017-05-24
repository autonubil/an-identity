
angular.module("autonubil-intranet").requires.push("autonubil-intranet-less");

angular.module("autonubil-intranet-less", [ "angular-plugin", "restangular" ]);
angular.module("autonubil-intranet-less")
.run(function(PluginMenuService, $location) {

	console.log("register menu items for less ... ");
	
	PluginMenuService.addItem("/main/admin", "/less", {
		visible: true,
		title : "Less Config"
	}, {
		controller : "LessConfigController",
		templateUrl : "less/templates/less.html" 
	});

	PluginMenuService.addItem("/main/admin", "/css", {
		visible: true,
		title : "CSS Config"
	}, {
		controller : "CssListController",
		templateUrl : "less/templates/css_configs.html" 
	});
	
	PluginMenuService.addRoute("/main/admin/css/:id", {
		controller : "CssEditController",
		templateUrl : "less/templates/css_config.html" 
	});
	
	
});

angular.module("autonubil-intranet-less")
.service("CssConfigService", function(Restangular,$location, $interval) {
	return {
		list : function(success) {
			return Restangular.all("autonubil/api/less/css").getList().then(success);
		},
		create : function(config,success) {
			return Restangular.all("autonubil/api/less/css").post(config).then(
				function(config) {
					console.log("config: ",config);
					success(config);
				});
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/less/css",id).get().then(success);
		},
		put : function(config,success) {
			Restangular.one("autonubil/api/less/css",config.id).customPUT(
				{
					id : config.id,
					name : config.name,
					order : config.order,
					rel : config.rel,
					type : config.type,
					href : config.href
				}
			).then(success);
		},
		remove : function(config,success) {
			Restangular.one("autonubil/api/less/css",config.id).remove().then(success);
		}
	};
});

angular.module("autonubil-intranet-less")
.controller("CssEditController", function($scope,CssConfigService,$routeParams) {
	
	$scope.update = function() {
		CssConfigService.get($routeParams.id,function(config) {
			$scope.config = config;
		})
	};
	
	$scope.update();
	
	
	$scope.save = function() {
		CssConfigService.put($scope.config,$scope.update);
	}
	
	
});

angular.module("autonubil-intranet-less")
.controller("CssListController", function($scope,CssConfigService,$location) {
	
	$scope.update = function() {
		CssConfigService.list(function(configs) {
			$scope.configs = configs;
		})
	};
	
	$scope.update();
	
	$scope.add = function() {
		config = { order: 0, name : "", rel : "", type : "", href : ""};
		CssConfigService.create(
			config,
			function(config) {
				$location.path("/main/admin/css/"+config.id);
			}
		);
	};
	
	$scope.remove = function(config) {
		CssConfigService.remove(
				config,
				function() {
					console.log("deleted ... ");
					$scope.update();
				}
			);
	}
	
	
});

angular.module("autonubil-intranet-less")
.controller("LessConfigController", function($scope,LessConfigService) {
	
	$scope.lessConfig = {};
	
	$scope.update = function() {
		LessConfigService.get(function(config) {
			$scope.lessConfig = config;
		})
	};
	
	$scope.save = function() {
		LessConfigService.put($scope.lessConfig, function() {
			$scope.update();
			console.log("success saving styles!");
			console.log($("#main_styles").attr("href","/autonubil/api/less/stylesheet?"+Math.random()));
		});
	};
	
	$scope.updateTyping = _.debounce($scope.save,400);

	$scope.update();
	
});

angular.module("autonubil-intranet-less")
.service("LessConfigService", function(Restangular,$location, $interval) {
	return {
		get : function(success) {
			return Restangular.one("autonubil/api/less/bootstrap").get().then(success);
		},
		put : function(config,success) {
			config.put().then(success);
		}
	};
});
