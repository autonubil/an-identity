angular.module("autonubil-intranet-otp")
.service("OtpService", function(Restangular,$location) {
	
	return {
		list : function(success,error) {
			return Restangular.all("autonubil/api/ldapotp/mytokens").getList().then(success,error);
		},
		create : function(token,success,error) {
			return Restangular.one("autonubil/api/ldapotp/mytokens").customPOST(token).then(success,error);
		}
	};
	
	
});
