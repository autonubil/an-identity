angular.module("autonubil-intranet-openid")
.controller("OpenIdConnectController", function($scope, $rootScope, AuthService, Restangular, $routeParams) {
	
	$scope.status = AuthService.getAuthStatus();
	
	$scope.redirect_uri = $routeParams.redirect_uri; 
	$scope.app= {
			id: null,
			prefix: "The ",
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
			
			if (e.error) {
				$scope.error = {
						data: {
							message: e.error
						}
				};
				return;
			}
			
			
			
			
			if (e.scopes) {
				$scope.scopes = e.scopes;
			} else {
				$scope.scopes = [];
			}
			
			if (e.linkedApplication) {
				$scope.app = e.linkedApplication;
			} else {
				$scope.app = {
						id: null,
					prefix: "The ",
					name: "Application"
				}
			}
			
			// remember the code
			$scope.code = e.code;
			
			if ($scope.status.loggedIn && $routeParams.redirect_uri){
				var newUrl = $routeParams.redirect_uri;
				if (newUrl.indexOf('?') > -1) {
					newUrl += "&";
				} else {
					newUrl += "?a=b" ;
				}
				
				params = ""
				// newUrl += "code=" + e.code +"&state="+e.state;
				if (e.code)
					params  +=  "&code="+e.code;
				if (e.state)
					params+=  "&state="+e.state;
				if (e.nonce)
					params  +=  "&nonce="+e.nonce;
				
				if (newUrl.indexOf('?') > -1) {
					newUrl += "&"+params;
				} else {
					newUrl += "?"+params ;
				}
				
				$scope.redirect_uri = newUrl;
				if (e.authenticated) {
					// console.log("OAuth2: redirecting to "+ newUrl);
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