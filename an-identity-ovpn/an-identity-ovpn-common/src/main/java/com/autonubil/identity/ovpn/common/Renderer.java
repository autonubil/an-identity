package com.autonubil.identity.ovpn.common;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.api.entities.OvpnOptions;


public class Renderer {
	private static VelocityEngine ve = new VelocityEngine();


	private static String runTemplate(Context ctx, String template) {
		if(template == null) {
			return null;
		}
		StringWriter w = new StringWriter();
		try {
			ve.evaluate(ctx, w, "", new StringReader(template));
		} catch (Exception e) {
			throw new RuntimeException("Faild to parse template",e );
		} 
		return w.toString();
	}
	
	
	public static String render(String template, Map<String, Object> params) {
		
		VelocityContext ctx = new VelocityContext();
		for(Map.Entry<String, Object> e : params.entrySet()) {
			ctx.put(e.getKey(), e.getValue());
		}
		
		return runTemplate(ctx, template);

	}
	
    public static String renderClientConfig(Map<String, Object> params) throws IOException {
		String template;
		try {
			template = IOUtils.toString(Renderer.class.getResourceAsStream("/com/autonubil/intranet/ovpn/templates/client.ovpn.vm"), "UTF-8");
		} catch (IOException  e) {
			throw new IOException("Failed to read template", e);
		}
		return render(template, params);
	}
	
    public static String renderServerConfig(Map<String, Object> params) throws IOException {
		String template;
		try {
			template = IOUtils.toString(Renderer.class.getResourceAsStream("/com/autonubil/intranet/ovpn/templates/server.conf.vm"), "UTF-8");
		} catch (IOException  e) {
			throw new IOException("Failed to read template", e);
		}
		return render(template, params);
	}
	
    
	public static Map<String,Object> ovpnConfigToParamMap(OvpnOptions ovpnOptions) {
		Map<String,Object> result = new HashMap<>();
		
		result.put("dev", ovpnOptions.getDev());
		result.put("fragment ", ovpnOptions.getFragment());
		result.put("mssfix", ovpnOptions.getMssfix());
		result.put("sndbuf", ovpnOptions.getSndbuf());
		result.put("rcvbuf", ovpnOptions.getRcvbuf());
		result.put("cipher", ovpnOptions.getCipher());
		result.put("auth", ovpnOptions.getAuth());
		
		result.put("resolv-retry", ovpnOptions.getResolvRetry());
		result.put("nobind", ovpnOptions.isNobind());
		result.put("persist-key", ovpnOptions.isPersistKey());
		result.put("persist-tun", ovpnOptions.isPersistTun());
		result.put("ns-cert-type", ovpnOptions.getNsCertType());
		result.put("setenv", ovpnOptions.getSetenv());
		result.put("verb", ovpnOptions.getVerb());
		
		result.put("ca", ovpnOptions.getCa());
		result.put("tls-auth", ovpnOptions.getTlsAuth());
		result.put("cert", ovpnOptions.getCert());
		
		result.put("remotes", ovpnOptions.getRemotes());
		
		result.put("auth-user-pass", ovpnOptions.isAuthUserPass());
		
		
		long renegSecs = ovpnOptions.getRenegSec();
		if ( (renegSecs > 0) && (renegSecs < OvpnConfigService.SESSION_EXPIRY - 30L)   ) {
			renegSecs = Math.min(10L,  OvpnConfigService.SESSION_EXPIRY - 30L);
		}
		result.put("reneg-sec", renegSecs );
		
		
		return result;
	} 
	
}
