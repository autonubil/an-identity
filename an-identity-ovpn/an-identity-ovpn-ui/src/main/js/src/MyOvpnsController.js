angular.module("autonubil-intranet-myovpns")
.controller("MyOvpnsController", function($scope,MeService, MyOvpnsService, AuthService, $location) {
	
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


 