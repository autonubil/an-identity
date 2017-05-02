angular.module("autonubil-intranet-auth")
.service("AuthService", function(Restangular,$location, $interval) {
	
	var AuthStatus = {
			loggedIn : false,
			user: {
				name : "anonymous"
			}
	};
	
	var updateAuth = function() {
		console.log ( " update Auth ... ");
		Restangular.all("autonubil/api/authentication").customGET("authenticate").then(
				function(e) {
					angular.module("autonubil-intranet-auth").meMenuState.visible = true;
					x = false;
					_.forEach(e.user.groups,function(group){
						if(group.name == "admin") {
							x = true;
						}
					});
					angular.module("autonubil-intranet-auth").adminMenuState.visible = x;					
					AuthStatus.loggedIn = true;
					AuthStatus.user.name = e.user.displayName;
					AuthStatus.identity = e;
				},
				function(e) {
					angular.module("autonubil-intranet-auth").meMenuState.visible = false;
					angular.module("autonubil-intranet-auth").adminMenuState.visible = false;
					AuthStatus.loggedIn = false;
					AuthStatus.user.name = "anonymous";
					console.log("failed: ");
				}			
		);
	};
	
	$interval(updateAuth,10000);
	updateAuth();

	return {
		getSources : function(callback) { 
			return Restangular.all("autonubil/api/authentication/sources").getList().then(callback);
		},
		getAuthStatus : function() {
			return AuthStatus;
		},
		
		login : function(credentials,success,error) {
			Restangular.all("autonubil/api/authentication/authenticate").post(credentials).then(
					function(identity) {
						updateAuth();
						success(identity);
					},error);
		}, 
	
		reset : function(reset,success,error) {
			Restangular.all("autonubil/api/authentication/reset").post(reset).then(success,error);
		}, 
		
		logout : function(username,password) {
			Restangular.one("autonubil/api/authentication/authenticate").customDELETE()
			.then(
				function(data) {
					updateAuth();
				},
				function(err) {
					updateAuth();
				}
			);
		} 
	
		
	}
	
})