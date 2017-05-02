angular.module("angular-plugin").service("PluginComponentService" , function() {
    	
    	var components = {};

        return {
        	get : function(path) {
	    		console.log("getting: ",path);
	    		console.log("getting: ",components[path]);
        		return components[path].children;
        	},
        	addItem : function(path,component) {
        		console.log("adding: "+path,component);
        		components[path] = components[path] || {children:[]};
	    		components[path].children.push(component);
	    		console.log("now: ",components[path]);
        	}
    	}
     });
