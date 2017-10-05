/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports) {

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




/***/ })
/******/ ]);