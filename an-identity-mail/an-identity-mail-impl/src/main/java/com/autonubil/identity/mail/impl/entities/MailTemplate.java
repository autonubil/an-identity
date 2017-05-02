package com.autonubil.identity.mail.impl.entities;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MailTemplate {
	
	private String id;
	private String module;
	private String name;
	private String locale;
	private String subject;
	private String text;
	private String html;
	private String model;
	
	public MailTemplate() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		Map<String,Object> x = new HashMap<>();
		ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		if(model!=null) {
			try {
				x = om.readValue(model, new TypeReference<Map<String,Object>>() {});
			} catch (Exception e) {
				throw new RuntimeException("model must be a map, but this is not a map!",e);
			}
		}
		try {
			this.model = om.writeValueAsString(x);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("i'm really sorry ... this shouldn't happen");
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}


}
