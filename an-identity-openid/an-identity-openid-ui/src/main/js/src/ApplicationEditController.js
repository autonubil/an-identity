angular.module("autonubil-intranet-openid")
 .controller("ApplicationEditController", function($scope, AuthService, OpenidConnectService, LdapConfigService, LdapGroupService, AppService, $routeParams, Restangular ) {
 
	 
	$scope.changed = false;
	
	$scope.newPermission = {
			application : {},
			group : {},
			name : "",
	};

	
 
	$scope.update = function() { 
		OpenidConnectService.get($routeParams.id,function(application){
			$scope.application = application;
		});

		
		if (!$scope.meta) {
			OpenidConnectService.getMeta(function(meta){
				$scope.meta = meta;
			});
				
		}
		
		if (!$scope.permissions) {
			OpenidConnectService.listPermissions($routeParams.id,function(permissions){
				console.log("permssions: "+permissions.length);
				$scope.permissions = permissions;
			});
		}

		if (!$scope.apps) {
			AppService.getList(	$scope.search = { search : "", offset : 0, limit : 5 },  function(apps){
				console.log("apps: "+apps.length);
				$scope.apps =   apps;
			});
		}
		
	};
	
	$scope.testOAuthApp = function(){
		console.log ( "OAuth2:  Getting auth from approval... ");
		params = {
				client_id: $scope.application.id,
				response_type: "code",
				nonce: "test.nonce",
//				state: "an-identity-test",
				scope: $scope.application.scopes.join(' ')
		}; 
		params['_t'] = Date.now();
		
		Restangular.one('oauth').customGET("approve", params).then( function(e) {
			
			// remember the code
			$scope.code = e.code;
			
			//if ($scope.status.loggedIn){
			newUrl = $scope.application.callbackUrl;
			if (newUrl.indexOf('?') > -1) {
				newUrl += "&";
			} else {
				newUrl += "?";
			}
			
			newUrl += "code=" + e.code +"&state="+e.state + "&nonce="+e.nonce;
			if (e.authenticated) {
				console.log("OAuth2: redirecting to "+ newUrl);
				window.location.href = newUrl;
			} 
			// }
		});
	};
	
	
	$scope.save = _.debounce(function() {
		OpenidConnectService.save($scope.application,function(application) {
			$scope.application = application;
			$scope.changed = false;
			$scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save(true);
	};
	
	AuthService.updateAuth();
	
	$scope.update();
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.application.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.application.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	
	$scope.removePermission = function(application,groupId) {
		console.log("remove permission",application,groupId);
		OpenidConnectService.deletePermission($routeParams.id,application,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OpenidConnectService.addPermission(
				$routeParams.id,
				{
					vpnId: $routeParams.id,
					applicationId : $scope.newPermission.application.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.application.name+": "+$scope.newPermission.group.displayName
				},
				$scope.update);
	};
	
})
.directive('jsonText', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {            
          function into(input) {
            return JSON.parse(input);
          }
          function out(data) {
            return JSON.stringify(data,null, 2);
          }
          ngModel.$parsers.push(into);
          ngModel.$formatters.push(out);

        }
    };
})
