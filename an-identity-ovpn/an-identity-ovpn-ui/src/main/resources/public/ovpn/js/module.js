angular.module("autonubil-intranet").requires.push("autonubil-intranet-ovpn");
angular.module("autonubil-intranet-ovpn", [ "angular-plugin", "restangular","autonubil-intranet-auth"]);
angular.module("autonubil-intranet-ovpn")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {

	PluginMenuService.addItem("/main/admin", "/ovpn/vpns", {
		title: "OpenVPN",
		visible: true
	}, {
		controller : "VpnListController",
		templateUrl : "ovpn/templates/vpns.html" 
	});
	
	
	PluginMenuService.addRoute("/main/admin/ovpn/vpns/:id", {
		controller: "VpnEditController",
		templateUrl: "ovpn/templates/vpn.html" 
	});

});


angular.module("autonubil-intranet").requires.push("autonubil-intranet-myovpns");
angular.module("autonubil-intranet-myovpns", [ "angular-plugin", "restangular","autonubil-intranet-auth", "autonubil-intranet-me" ]);

angular.module("autonubil-intranet-myovpns")
.run(function(PluginMenuService, PluginComponentService, $location, $rootScope) {
	myVpns= {
			visible: true,
			defaultItem: true,
			id: "vpns",
			status: "My VPNs",
			title: "VPNs",
			
			templateUrl : "ovpn/templates/my_vpns.html"
	};
	
	PluginComponentService.addItem("/me",myVpns); 
	PluginComponentService.addItem("/dashboard",myVpns); 

});

/* Blob.js
 * A Blob implementation.
 * 2014-07-24
 *
 * By Eli Grey, http://eligrey.com
 * By Devin Samarin, https://github.com/dsamarin
 * License: MIT
 * See https://github.com/eligrey/Blob.js/blob/master/LICENSE.md
 */ /*global self, unescape */ /*jslint bitwise: true, regexp: true, confusion: true, es5: true, vars: true, white: true,
  plusplus: true */ /*! @source http://purl.eligrey.com/github/Blob.js/blob/master/Blob.js */ (function (view) {
	"use strict";
	view.URL = view.URL || view.webkitURL;
	if (view.Blob && view.URL) {
		try {
			new Blob;
			return;
		} catch (e) {}
	}
	// Internally we use a BlobBuilder implementation to base Blob off of
	// in order to support older browsers that only have BlobBuilder
	var BlobBuilder = view.BlobBuilder || view.WebKitBlobBuilder || view.MozBlobBuilder || (function(view) {
		var
			  get_class = function(object) {
				return Object.prototype.toString.call(object).match(/^\[object\s(.*)\]$/)[1];
			}
			, FakeBlobBuilder = function BlobBuilder() {
				this.data = [];
			}
			, FakeBlob = function Blob(data, type, encoding) {
				this.data = data;
				this.size = data.length;
				this.type = type;
				this.encoding = encoding;
			}
			, FBB_proto = FakeBlobBuilder.prototype
			, FB_proto = FakeBlob.prototype
			, FileReaderSync = view.FileReaderSync
			, FileException = function(type) {
				this.code = this[this.name = type];
			}
			, file_ex_codes = (
				  "NOT_FOUND_ERR SECURITY_ERR ABORT_ERR NOT_READABLE_ERR ENCODING_ERR "
				+ "NO_MODIFICATION_ALLOWED_ERR INVALID_STATE_ERR SYNTAX_ERR"
			).split(" ")
			, file_ex_code = file_ex_codes.length
			, real_URL = view.URL || view.webkitURL || view
			, real_create_object_URL = real_URL.createObjectURL
			, real_revoke_object_URL = real_URL.revokeObjectURL
			, URL = real_URL
			, btoa = view.btoa
			, atob = view.atob
			, ArrayBuffer = view.ArrayBuffer
			, Uint8Array = view.Uint8Array
			, origin = /^[\w-]+:\/*\[?[\w\.:-]+\]?(?::[0-9]+)?/
		;
		FakeBlob.fake = FB_proto.fake = true;
		while (file_ex_code--) {
			FileException.prototype[file_ex_codes[file_ex_code]] = file_ex_code + 1;
		}
		// Polyfill URL
		if (!real_URL.createObjectURL) {
			URL = view.URL = function(uri) {
				var
					  uri_info = document.createElementNS("http://www.w3.org/1999/xhtml", "a")
					, uri_origin
				;
				uri_info.href = uri;
				if (!("origin" in uri_info)) {
					if (uri_info.protocol.toLowerCase() === "data:") {
						uri_info.origin = null;
					} else {
						uri_origin = uri.match(origin);
						uri_info.origin = uri_origin && uri_origin[1];
					}
				}
				return uri_info;
			};
		}
		URL.createObjectURL = function(blob) {
			var
				  type = blob.type
				, data_URI_header
			;
			if (type === null) {
				type = "application/octet-stream";
			}
			if (blob instanceof FakeBlob) {
				data_URI_header = "data:" + type;
				if (blob.encoding === "base64") {
					return data_URI_header + ";base64," + blob.data;
				} else if (blob.encoding === "URI") {
					return data_URI_header + "," + decodeURIComponent(blob.data);
				} if (btoa) {
					return data_URI_header + ";base64," + btoa(blob.data);
				} else {
					return data_URI_header + "," + encodeURIComponent(blob.data);
				}
			} else if (real_create_object_URL) {
				return real_create_object_URL.call(real_URL, blob);
			}
		};
		URL.revokeObjectURL = function(object_URL) {
			if (object_URL.substring(0, 5) !== "data:" && real_revoke_object_URL) {
				real_revoke_object_URL.call(real_URL, object_URL);
			}
		};
		FBB_proto.append = function(data/*, endings*/) {
			var bb = this.data;
			// decode data to a binary string
			if (Uint8Array && (data instanceof ArrayBuffer || data instanceof Uint8Array)) {
				var
					  str = ""
					, buf = new Uint8Array(data)
					, i = 0
					, buf_len = buf.length
				;
				for (; i < buf_len; i++) {
					str += String.fromCharCode(buf[i]);
				}
				bb.push(str);
			} else if (get_class(data) === "Blob" || get_class(data) === "File") {
				if (FileReaderSync) {
					var fr = new FileReaderSync;
					bb.push(fr.readAsBinaryString(data));
				} else {
					// async FileReader won't work as BlobBuilder is sync
					throw new FileException("NOT_READABLE_ERR");
				}
			} else if (data instanceof FakeBlob) {
				if (data.encoding === "base64" && atob) {
					bb.push(atob(data.data));
				} else if (data.encoding === "URI") {
					bb.push(decodeURIComponent(data.data));
				} else if (data.encoding === "raw") {
					bb.push(data.data);
				}
			} else {
				if (typeof data !== "string") {
					data += ""; // convert unsupported types to strings
				}
				// decode UTF-16 to binary string
				bb.push(unescape(encodeURIComponent(data)));
			}
		};
		FBB_proto.getBlob = function(type) {
			if (!arguments.length) {
				type = null;
			}
			return new FakeBlob(this.data.join(""), type, "raw");
		};
		FBB_proto.toString = function() {
			return "[object BlobBuilder]";
		};
		FB_proto.slice = function(start, end, type) {
			var args = arguments.length;
			if (args < 3) {
				type = null;
			}
			return new FakeBlob(
				  this.data.slice(start, args > 1 ? end : this.data.length)
				, type
				, this.encoding
			);
		};
		FB_proto.toString = function() {
			return "[object Blob]";
		};
		FB_proto.close = function() {
			this.size = 0;
			delete this.data;
		};
		return FakeBlobBuilder;
	}(view));
	view.Blob = function(blobParts, options) {
		var type = options ? (options.type || "") : "";
		var builder = new BlobBuilder();
		if (blobParts) {
			for (var i = 0, len = blobParts.length; i < len; i++) {
				if (Uint8Array && blobParts[i] instanceof Uint8Array) {
					builder.append(blobParts[i].buffer);
				}
				else {
					builder.append(blobParts[i]);
				}
			}
		}
		var blob = builder.getBlob(type);
		if (!blob.slice && blob.webkitSlice) {
			blob.slice = blob.webkitSlice;
		}
		return blob;
	};
	var getPrototypeOf = Object.getPrototypeOf || function(object) {
		return object.__proto__;
	};
	view.Blob.prototype = getPrototypeOf(new view.Blob());
}(typeof self !== "undefined" && self || typeof window !== "undefined" && window || this.content || this));

angular.module("autonubil-intranet-myovpns")
.controller("MyOvpnsController", function($scope,MeService, MyOvpnsService, AuthService, $location) {
	
	AuthService.updateAuth();
	
	$scope.search =  "";

	$scope.updateVpns = _.debounce(function() {
		MyOvpnsService.getVpns({search:$scope.search},function(vpns){
			$scope.vpns = vpns;
		});
	},250);
	
	$scope.updateVpns();
	
	
	$scope.downloadVpn = _.debounce(function(vpnId, name) {
		MyOvpnsService.download(vpnId, name  ,function(){
			$scope.updateVpns();
		});
	},250);

	$scope.revokeVpnCertificate = _.debounce(function(vpnId) {
		bootbox.confirm({
		    message: "Are you sure you want to delete you current OpenVPN configuration?",
		    buttons: {
		        confirm: {
		            label: 'Yes',
		            className: 'btn-success'
		        },
		        cancel: {
		            label: 'No',
		            className: 'btn-danger'
		        }
		    },
		    callback: function (result) {
		        if (result) {
		        	MyOvpnsService.revokeVpnCertificate(vpnId,function(){
						$scope.updateVpns();
					});
		        }
		    }
		});
		
		
		 
	},250);
	
	$scope.newVpnConfig = _.debounce(function(vpnId, name) {
		bootbox.confirm({
		    message: "Are you sure you want to delete you current OpenVPN configuration and create a new one?",
		    buttons: {
		        confirm: {
		            label: 'Yes',
		            className: 'btn-success'
		        },
		        cancel: {
		            label: 'No',
		            className: 'btn-danger'
		        }
		    },
		    callback: function (result) {
		        if (result) {
		        	MyOvpnsService.revokeVpnCertificate(vpnId,function(){
						MyOvpnsService.download(vpnId,  name  ,function(){
							$scope.updateVpns();
						});
					});
		        }
		    }
		});
 
	},250);


});


 
angular.module("autonubil-intranet-myovpns")
.service("MyOvpnsService", function(Restangular,$location) {
	
	return {
		getUsers : function(params, success) {
			return Restangular.all("autonubil/api/authentication").customGET("authenticate").then(success);
		},
		getVpns : function(params,success) {
			return Restangular.all("autonubil/api/ovpn/myvpns").getList(params).then(success);
		},
		revokeVpnCertificate : function(vpnId,success) {
			return Restangular.one("autonubil/api/ovpn/vpns/"+vpnId+"/client-config").remove().then(success);
		},
		download : function(vpnId,name, success) {
			return Restangular.one("autonubil/api/ovpn/vpns/"+vpnId+"/client-config").get().then(function (res) {
				var file = new Blob([res], { type: 'application/x-openvpn-profile' });
			    if (saveAs(file, name+'.ovpn') ) {
			    	success();
			    }
			});
		},
		
		newVpnConfig: function(vpnId,name, success) {
			
		}
	};
	
});
angular.module("autonubil-intranet-ovpn")
.service("OvpnService", function(Restangular,$location, $interval) {
	
	return {
		getList : function(params, success) {
			return Restangular.all("autonubil/api/ovpn/vpns").getList(params).then(success);
		},
		get : function(id,success) {
			return Restangular.one("autonubil/api/ovpn/vpns",id).get().then(success);
		},
		add : function(config,success) {
			return Restangular.all("autonubil/api/ovpn/vpns").post(config).then(success);
		},
		remove : function(id,success) {
			return Restangular.one("autonubil/api/ovpn/vpns",id).remove().then(success);
		},
		save : function(ovpn,success) {
			return ovpn.put().then(success);
		},
		
		getSessionConfigProviderList : function(search,success) {
			return Restangular.all("autonubil/api/ovpn/providers/session").getList({search: search}).then(success);
		},
		getClientConfigProviderList : function(search,success) {
			return Restangular.all("autonubil/api/ovpn/providers/client").getList({search: search}).then(success);
		},
		getServerConfigProviderList : function(search,success) {
			return Restangular.all("autonubil/api/ovpn/providers/server").getList({search: search}).then(success);
		},		 
		
		 
		
		listPermissions : function(id,success) {
			return Restangular.all("autonubil/api/ovpn/vpns/"+id+"/permissions").getList({}).then(success);
		},
		addPermission : function(ovpnId,permission,success) {
			return Restangular.all("autonubil/api/ovpn/vpns/"+ovpnId+"/permissions").customPOST(permission).then(success);
		},
		deletePermission : function(ovpnId,sourceId,groupId,success) {
			return Restangular.all("autonubil/api/ovpn/vpns/"+ovpnId+"/permissions").customDELETE("",{sourceId:sourceId,groupId:groupId}).then(success);
		}
		
		
		
		 
	};
	
});

// window.saveAs
// Shims the saveAs method, using saveBlob in IE10. 
// And for when Chrome and FireFox get round to implementing saveAs we have their vendor prefixes ready. 
// But otherwise this creates a object URL resource and opens it on an anchor tag which contains the "download" attribute (Chrome)
// ... or opens it in a new tab (FireFox)
// @author Andrew Dodson
// @copyright MIT, BSD. Free to clone, modify and distribute for commercial and personal use.

window.saveAs || ( window.saveAs = (window.navigator.msSaveBlob ? function(b,n){ return window.navigator.msSaveBlob(b,n); } : false) || window.webkitSaveAs || window.mozSaveAs || window.msSaveAs || (function(){

	// URL's
	window.URL || (window.URL = window.webkitURL);

	if(!window.URL){
		return false;
	}

	return function(blob,name){
		var url = URL.createObjectURL(blob);

		// Test for download link support
		if( "download" in document.createElement('a') ){

			var a = document.createElement('a');
			a.setAttribute('href', url);
			a.setAttribute('download', name);

			// Create Click event
			var clickEvent = document.createEvent ("MouseEvent");
			clickEvent.initMouseEvent ("click", true, true, window, 0, 
				event.screenX, event.screenY, event.clientX, event.clientY, 
				event.ctrlKey, event.altKey, event.shiftKey, event.metaKey, 
				0, null);

			// dispatch click event to simulate download
			a.dispatchEvent (clickEvent);

		}
		else{
			// fallover, open resource in new tab.
			window.open(url, '_blank', '');
		}
	};

})() );
angular.module("autonubil-intranet-ovpn")
.controller("VpnEditController", function($scope,AuthService,OvpnService,LdapConfigService,LdapGroupService,$routeParams) {

	AuthService.updateAuth();
	
	$scope.changed = false;
	
	$scope.newPermission = {
			source : {},
			group : {},
			name : "",
	};

	
	LdapConfigService.getList({},function(configs){
		$scope.configs=configs;
	});

 
	
	$scope.update = function() { 
		OvpnService.get($routeParams.id,function(source){
			$scope.source = source;
		});
		
		OvpnService.listPermissions($routeParams.id,function(permissions){
			console.log("permssions: "+permissions.length);
			$scope.permissions = permissions;
		});

		
		OvpnService.getClientConfigProviderList("", function(clientConfigProviders){
			console.log("clientConfigProviders: "+clientConfigProviders.length);
			$scope.clientConfigProviders = clientConfigProviders;
		});
		
		OvpnService.getServerConfigProviderList("", function(serverConfigProviders){
			console.log("serverConfigProviders: "+serverConfigProviders.length);
			$scope.serverConfigProviders = serverConfigProviders;
		});
		OvpnService.getSessionConfigProviderList("", function(sessionConfigProviders){
			console.log("sessionConfigProviders: "+sessionConfigProviders.length);
			$scope.sessionConfigProviders = sessionConfigProviders;
		});
	};
	

	$scope.save = _.debounce(function() {
		OvpnService.save($scope.source,function(source) {
			$scope.source = source;
			$scope.changed = false;
			// $scope.update();
		});
	},700)
	
	$scope.startChange = function() {
		$scope.changed = true;
		$scope.save(true);
	};
	
	
	$scope.update();
	
	$scope.updateGroups = function() {
		console.log("updating groups");
		if(!$scope.newPermission.source.id) return;
		$scope.groups = [];
		LdapGroupService.getList($scope.newPermission.source.id,{},function(groups) {
			if(!$scope.newPermission.groupId || $scope.newPermission.groupId=="") {
				$scope.newPermission.groupId = groups[0].id;
			}
			$scope.groups = groups;
		});
	}
	
	$scope.removePermission = function(source,groupId) {
		console.log("remove permission",source,groupId);
		OvpnService.deletePermission($routeParams.id,source,groupId,$scope.update);
	};
	
	$scope.addPermission = function() {
		console.log("add permission");
		OvpnService.addPermission(
				$routeParams.id,
				{
					vpnId: $routeParams.id,
					sourceId : $scope.newPermission.source.id,
					groupId : $scope.newPermission.group.id,
					name : $scope.newPermission.source.name+": "+$scope.newPermission.group.displayName
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
});

angular.module("autonubil-intranet-ovpn")
.controller("VpnListController", function($scope,AuthService,OvpnService,$location) {

	AuthService.updateAuth();
	
	$scope.enableAdd = false;
	
	$scope.search = {
			search : "",
			offset : 0,
			limit : 5
	};
	
	$scope.update = function() {
		OvpnService.getList($scope.search, function(vpns){
			$scope.vpns= vpns;
		});
	}
	
	$scope.updateTyping = _.debounce($scope.update,400);
	
	$scope.add = function(id) {
		console.log("saving OpenVPN source ... ");
		var x = { name : $scope.search.search, 
				secretsProvider: "internal",   description: $scope.search.search, 
				serverConfiguration: {}, serverConfigurationProvider: "default", 
				sessionConfiguration: {}, sessionConfigurationProvider: "default", 
				clientConfiguration: {}, clientConfigurationProvider: "default"
		};
		OvpnService.add(
				x,
				function(ovpn){
					console.log("OpenVPN source saved... ",ovpn);
					$location.path("/main/admin/ovpn/vpn/"+ovpn.id);
				}
		);
	}
	
	$scope.update();
	
	
})