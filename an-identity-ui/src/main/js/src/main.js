angular.module("templates", []);

angular.module("autonubil-intranet",["templates","ngRoute", "restangular", "angular-plugin", "angularSpinner"]);


angular.module("autonubil-intranet").config(['usSpinnerConfigProvider', function (usSpinnerConfigProvider) {
	usSpinnerConfigProvider.setTheme('default', {color: 'silver', radius: 20});
}]);


		
angular.module("autonubil-intranet")
.factory('errorInterceptor', function($q, $location) {
	return {
		responseError : function(rejection) {
			if (rejection.status == 400  || rejection.status >  404) {
				message = ""; //"<h2>"+ rejection.status + " " +rejection.data.error +"</h2>";
				message += "<div style=\"zoom: 40%\">"
				if (rejection.data) {
					message += "<h1>Details<h1>";
					message += "<label>Path:</label> " +rejection.data.path+"<br/>";
					message += "<label>Timestamp:</label>"+  new Date(rejection.data.timestamp)+" (" +rejection.data.timestamp +")<br/>";
					if (rejection.data.exception)
						message += "<label>Exception:</label> " +rejection.data.exception+"<br/>";
					if (rejection.data.message)
						message += "<label>Message:</label>" + rejection.data.message+"<br/>";
				}

				
				if (rejection.config) {
					message += "<h1><b>Request<h1>";
					message += "<label>Url:</label> " +rejection.config.url+"<br/>";
					message += "<label>Method:</label> " +rejection.config.method+"<br/>";
					if (rejection.config.data)
						message += "<label>Data:</label> " + JSON.stringify(rejection.config.data)+"<br/>";
					if (rejection.config.header)
						message += "<label>Header:</label> " + JSON.stringify(rejection.config.header)+"<br/>";
				}
				message += "</div>"
				bootbox.alert({
					size: 'large',
					backdrop: true,
				    title: "An Error Occured ("+ rejection.status +") " +rejection.data.error,
				    message:  message  
				    });
	            }
				return $q.reject(rejection);
			}
		}
});


angular.module("autonubil-intranet")
 .config(function($httpProvider) {
	$httpProvider.interceptors.push("errorInterceptor");
});


angular.module("autonubil-intranet")
 .run(function(PluginMenuService) {
	 	
	 
	PluginMenuService.addItem("","/welcome",{title:"Welcome",visible:true}, {
		controller: function() {},
		templateUrl: "welcome.html"
	});
	
	PluginMenuService.setDefault("/auth/dashboard");
	

});


