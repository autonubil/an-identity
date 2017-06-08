package com.autonubil.identity.ovpn.api.entities;

import java.util.ArrayList;
import java.util.List;

public class OvpnServerConfig {
	private String local; 
	private String netmask;
	
	/**
	 * Don't inherit the global push list for a specific client instance. Specify this option in a client-specific context such as with a --client-config-dir configuration file. This option will ignore --push options at the global config file level.
	 */
	private boolean pushReset = true;
	
	/**
	Push a config file option back to the client for remote execution. Note that option must be enclosed in double quotes (""). The client must specify --pull in its config file. The set of options which can be pushed is limited by both feasibility and security. Some options such as those which would execute scripts are banned, since they would effectively allow a compromised server to execute arbitrary code on the client. Other options such as TLS or MTU parameters cannot be pushed because the client needs to know them before the connection to the server can be initiated.
	This is a partial list of options which can currently be pushed: --route, --route-gateway, --route-delay, --redirect-gateway, --ip-win32, --dhcp-option, --inactive, --ping, --ping-exit, --ping-restart, --setenv, --persist-key, --persist-tun, --echo
	*/
	private List<OvpnPushOption> push = new ArrayList<>();
	
	/**
	 * Generate an internal route to a specific client. The netmask parameter, if omitted, defaults to 255.255.255.255.
	 * This directive can be used to route a fixed subnet from the server to a particular client, regardless of where the client is connecting from. Remember that you must also add the route to the system routing table as well (such as by using the --route directive). The reason why two routes are needed is that the --route directive routes the packet from the kernel to OpenVPN. Once in OpenVPN, the --iroute directive routes to the specific client.
	 * 		
	 * This option must be specified either in a client instance config file using --client-config-dir or dynamically generated using a --client-connect script.
	 * 	
	 * The --iroute directive also has an important interaction with --push "route ...". --iroute essentially defines a subnet which is owned by a particular client (we will call this client A). If you would like other clients to be able to reach A's subnet, you can use --push "route ..." together with --client-to-client to effect this. In order for all clients to see A's subnet, OpenVPN must push this route to all clients EXCEPT for A, since the subnet is already owned by A. OpenVPN accomplishes this by not not pushing a route to a client if it matches one of the client's iroutes.
	 */
	private List<OpvnIroute> iroute;
	// --push, --push-reset, --iroute, --ifconfig-push, and --config.
	
	/**
	Push virtual IP endpoints for client tunnel, overriding the --ifconfig-pool dynamic allocation.
	The parameters local and remote-netmask are set according to the --ifconfig directive which you want to execute on the client machine to configure the remote end of the tunnel. Note that the parameters local and remote-netmask are from the perspective of the client, not the server. They may be DNS names rather than IP addresses, in which case they will be resolved on the server at the time of client connection.

	This option must be associated with a specific client instance, which means that it must be specified either in a client instance config file using --client-config-dir or dynamically generated using a --client-connect script.

	Remember also to include a --route directive in the main OpenVPN config file which encloses local, so that the kernel will know to route it to the server's TUN/TAP interface.

	OpenVPN's internal client IP address selection algorithm works as follows:

	1 -- Use --client-connect script generated file for static IP (first choice). 
	2 -- Use --client-config-dir file for static IP (next choice). 
	3 -- Use --ifconfig-pool allocation for dynamic IP (last choice).
   */
	private List<OvpnIfConfigPush> ifConfigPush;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (this.pushReset) {
			sb.append("push-reset\n");
		}
		if (this.ifConfigPush != null) {
			for (OvpnIfConfigPush ovpnIfConfigPush : this.ifConfigPush) {
				sb.append(ovpnIfConfigPush);
				sb.append("\n");
			}
		}
		
		if (this.iroute != null) {
			for (OpvnIroute opvnIroute : this.iroute) {
				sb.append(opvnIroute);
				sb.append("\n");
			}
		}
		
		
		if (this.push != null) {
			for (OvpnPushOption push : this.push) {
				sb.append(push);
				sb.append("\n");
			}
		}
		
	
		return sb.toString();
	}

	public boolean isPushReset() {
		return pushReset;
	}

	public void setPushReset(boolean pushReset) {
		this.pushReset = pushReset;
	}

	public List<OvpnPushOption> getPush() {
		return push;
	}

	public void setPush(List<OvpnPushOption> push) {
		this.push = push;
	}

	public List<OpvnIroute> getIroute() {
		return iroute;
	}

	public void setIroute(List<OpvnIroute> iroute) {
		this.iroute = iroute;
	}

	public List<OvpnIfConfigPush> getIfConfigPush() {
		return ifConfigPush;
	}

	public void setIfConfigPush(List<OvpnIfConfigPush> ifConfigPush) {
		this.ifConfigPush = ifConfigPush;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	
	
	
}
	
	
