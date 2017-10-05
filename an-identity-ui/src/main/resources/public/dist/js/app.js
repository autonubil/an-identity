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



/**
 * Checklist-model
 * AngularJS directive for list of checkboxes
 * https://github.com/vitalets/checklist-model
 * License: MIT http://opensource.org/licenses/MIT
 */

angular.module('checklist-model', [])
.directive('checklistModel', ['$parse', '$compile', function($parse, $compile) {
  // contains
  function contains(arr, item, comparator) {
    if (angular.isArray(arr)) {
      for (var i = arr.length; i--;) {
        if (comparator(arr[i], item)) {
          return true;
        }
      }
    }
    return false;
  }

  // add
  function add(arr, item, comparator) {
    arr = angular.isArray(arr) ? arr : [];
      if(!contains(arr, item, comparator)) {
          arr.push(item);
      }
    return arr;
  }  

  // remove
  function remove(arr, item, comparator) {
    if (angular.isArray(arr)) {
      for (var i = arr.length; i--;) {
        if (comparator(arr[i], item)) {
          arr.splice(i, 1);
          break;
        }
      }
    }
    return arr;
  }

  // http://stackoverflow.com/a/19228302/1458162
  function postLinkFn(scope, elem, attrs) {
     // exclude recursion, but still keep the model
    var checklistModel = attrs.checklistModel;
    attrs.$set("checklistModel", null);
    // compile with `ng-model` pointing to `checked`
    $compile(elem)(scope);
    attrs.$set("checklistModel", checklistModel);

    // getter / setter for original model
    var getter = $parse(checklistModel);
    var setter = getter.assign;
    var checklistChange = $parse(attrs.checklistChange);
    var checklistBeforeChange = $parse(attrs.checklistBeforeChange);

    // value added to list
    var value = attrs.checklistValue ? $parse(attrs.checklistValue)(scope.$parent) : attrs.value;


    var comparator = angular.equals;

    if (attrs.hasOwnProperty('checklistComparator')){
      if (attrs.checklistComparator[0] == '.') {
        var comparatorExpression = attrs.checklistComparator.substring(1);
        comparator = function (a, b) {
          return a[comparatorExpression] === b[comparatorExpression];
        };
        
      } else {
        comparator = $parse(attrs.checklistComparator)(scope.$parent);
      }
    }

    // watch UI checked change
    scope.$watch(attrs.ngModel, function(newValue, oldValue) {
      if (newValue === oldValue) { 
        return;
      } 

      if (checklistBeforeChange && (checklistBeforeChange(scope) === false)) {
        scope[attrs.ngModel] = contains(getter(scope.$parent), value, comparator);
        return;
      }

      setValueInChecklistModel(value, newValue);

      if (checklistChange) {
        checklistChange(scope);
      }
    });

    function setValueInChecklistModel(value, checked) {
      var current = getter(scope.$parent);
      if (angular.isFunction(setter)) {
        if (checked === true) {
          setter(scope.$parent, add(current, value, comparator));
        } else {
          setter(scope.$parent, remove(current, value, comparator));
        }
      }
      
    }

    // declare one function to be used for both $watch functions
    function setChecked(newArr, oldArr) {
      if (checklistBeforeChange && (checklistBeforeChange(scope) === false)) {
        setValueInChecklistModel(value, scope[attrs.ngModel]);
        return;
      }
      scope[attrs.ngModel] = contains(newArr, value, comparator);
    }

    // watch original model change
    // use the faster $watchCollection method if it's available
    if (angular.isFunction(scope.$parent.$watchCollection)) {
        scope.$parent.$watchCollection(checklistModel, setChecked);
    } else {
        scope.$parent.$watch(checklistModel, setChecked, true);
    }
  }

  return {
    restrict: 'A',
    priority: 1000,
    terminal: true,
    scope: true,
    compile: function(tElement, tAttrs) {
      if ((tElement[0].tagName !== 'INPUT' || tAttrs.type !== 'checkbox') && (tElement[0].tagName !== 'MD-CHECKBOX') && (!tAttrs.btnCheckbox)) {
        throw 'checklist-model should be applied to `input[type="checkbox"]` or `md-checkbox`.';
      }

      if (!tAttrs.checklistValue && !tAttrs.value) {
        throw 'You should provide `value` or `checklist-value`.';
      }

      // by default ngModel is 'checked', so we set it if not specified
      if (!tAttrs.ngModel) {
        // local scope var storing individual checkbox model
        tAttrs.$set("ngModel", "checked");
      }

      return postLinkFn;
    }
  };
}]);
"use strict";

exports.__esModule = true;

exports.default = function () {
  return {
    manipulateOptions: function manipulateOptions(opts, parserOpts) {
      parserOpts.plugins.push("exponentiationOperator");
    }
  };
};

module.exports = exports["default"];