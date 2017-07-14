package com.autonubil.identity.openid.impl.entities;

import java.util.ArrayList;
import java.util.List;

public class WebfingerResponse {
	String subject;
	List<WebfingerLink> links;
	
	
	public  WebfingerResponse() {
		this.links = new ArrayList<>();
	}

	public  WebfingerResponse(String subject) {
		this();
		this.subject = subject;
	}
	
	public  WebfingerResponse(String subject, String rel, String href ) {
		this();
		this.subject = subject;
		this.links.add( new WebfingerLink(rel,href));
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<WebfingerLink> getLinks() {
		return links;
	}

	public void setLinks(List<WebfingerLink> links) {
		this.links = links;
	}
	
	
	
}
