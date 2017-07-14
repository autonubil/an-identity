package com.autonubil.identity.openid.impl.entities;

public class WebfingerLink {
	String rel;
	String href;
	
	public WebfingerLink() {
		
	}
	
	public WebfingerLink(String rel, String href) {
		this.rel = rel;
		this.href = href;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
	
	
}	
