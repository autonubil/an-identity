package com.autonubil.identity.ovpn.common;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.autonubil.identity.ovpn.api.entities.OvpnOptions;


public class Renderer {
	private static VelocityEngine ve = new VelocityEngine();


	private static String runTemplate(Context ctx, String template) {
		if(template == null) {
			return null;
		}
		StringWriter w = new StringWriter();
		ve.evaluate(ctx, w, "", new StringReader(template));
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
			template = new String(Files.readAllBytes(Paths.get(Renderer.class.getResource("/templates/client.ovpn.vm").toURI())));
		} catch (IOException | URISyntaxException e) {
			throw new IOException("Failed to read template", e);
		}
		return render(template, params);

	}
	
	public static Map<String,Object> ovpnConfigToParamMap(OvpnOptions ovpnOptions) {
		Map<String,Object> result = new HashMap<>();
		
		result.put("dev", ovpnOptions.getDev());
		result.put("tun-mtu", ovpnOptions.getTunMtu());
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
		
		
		return result;
	}
	
	public Map<String,Object> getDefaultOvpnParams() {
		Map<String,Object> result = new HashMap<>();
		result.put("dev", "tun");
		result.put("tun-mtu", 6000);
		result.put("fragment ", null);
		result.put("mssfix", 0);
		result.put("sndbuf", 100000);
		result.put("rcvbuf", 100000);
		result.put("cipher", "AES-256-CBC");
		result.put("auth", "SHA256");
		
		result.put("resolv-retry", "infinite");
		result.put("nobind", true);
		result.put("persist-key", true);
		result.put("persist-tun", true);
		result.put("ns-cert-type", "server");
		result.put("setenv", "PUSH_PEER_INFO");
		result.put("verb", 3);
		
		result.put("ca", null);
		result.put("tls-auth", null);
		result.put("cert", null);
		
		return result;
	}
	
}
