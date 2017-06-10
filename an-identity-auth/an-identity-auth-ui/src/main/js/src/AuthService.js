angular.module("autonubil-intranet-auth")
.service("AuthService", function(Restangular,$location, $interval, $rootScope) {
	
	var AuthStatus = {
			loggedIn : false,
			admin : false,
			user: {
				name : "anonymous"
			}
	};
	

	var setAuthStatus = function(l,a,n) {
		changed = false;
		if(l!=AuthStatus.loggedIn) {
			AuthStatus.loggedIn = l;
			changed = true;
		}
		if(a!=AuthStatus.admin) {
			AuthStatus.admin = a;
			changed = true;
		}
		if(n!=AuthStatus.user.name) {
			AuthStatus.user.name = n;
			changed = true;
		}
		console.log ( "Auth changed? "+changed);
		if(changed) {
			$rootScope.$emit("authChanged",AuthStatus);
		}
	} 
	
	
	var updateAuth = function() {
		console.log ( " update Auth ... ");
		Restangular.all("autonubil/api/authentication").customGET("authenticate").then(
				function(e) {
					x = false;
					_.forEach(e.user.groups,function(group){
						if(group.name == "admin") {
							x = true;
						}
					});
					setAuthStatus(true,x,e.user.displayName);
				},
				function(e) {
					setAuthStatus(false,false,"anonymous");
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
		},
	
		updateAuth : updateAuth
		
	}
	
});

