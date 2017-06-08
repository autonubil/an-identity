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