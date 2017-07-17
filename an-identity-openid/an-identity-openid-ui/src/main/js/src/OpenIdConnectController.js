angular.module("autonubil-intranet-openid")
.controller("OpenIdConnectController", function($scope, $rootScope, AuthService, Restangular, $routeParams) {
	
	$scope.status = AuthService.getAuthStatus();
	
	$scope.redirect_uri = $routeParams.redirect_uri; 
	$scope.app= {
			name: "Application"
	}
	$scope.scopes = [];
	
	$scope.code = $routeParams.code; 
	
	var update = function() {
	
		console.log ( "OAuth2:  Getting auth from approval... ");
		params = {
				client_id: $routeParams.client_id,
				response_type: $routeParams.response_type	
		};
		
		if ($routeParams.nonce) {
			params['nonce'] = $routeParams.nonce;
		}
		if ($routeParams.state) {
			params['state'] = $routeParams.state;
		}
		if ($routeParams.scope) {
			params['scope'] = $routeParams.scope;
		}
		if ($scope.code) {
			params['code'] = $scope.code
		}
		params['_t'] = Date.now();
		
		Restangular.one('oauth').customGET("approve", params).then( function(e) {
			if (e.scopes) {
				$scope.scopes = e.scopes;
			} else {
				$scope.scopes = [];
			}
			
			if (e.app) {
				$scope.app = e.app;
			} else {
				$scope.app = {
					name: "Application"
				}
			}
			
			// remember the code
			$scope.code = e.code;
			
			if ($scope.status.loggedIn && $routeParams.redirect_uri){
				newUrl = $routeParams.redirect_uri;
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
			}
		});
		
		
	};
	
	$rootScope.$on("authChanged", function(e,status) {
		update();
		if (!$scope.status.loggedIn){
			console.log ( "OAuth2:  User needs to login first... ");
		}
	});
	update();
	
})