package com.autonubil.identity.ovpn.api.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.autonubil.identity.auth.api.entities.Notification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

public class MyOvpn extends Ovpn {
	private Date validFrom;
	private Date validTil;
	private String serial;
	private boolean valid;
	
	private List<Notification> notifications = new ArrayList<>();
	
	public MyOvpn() {
		
	}
	
	public MyOvpn(Ovpn ovpn){
		this.setId(ovpn.getId());
		this.setDescription(ovpn.getDescription());
		this.setName(ovpn.getName());
		this.setClientConfigurationProvider(ovpn.getClientConfigurationProvider());
		this.setSessionConfigurationProvider(ovpn.getSessionConfigurationProvider());
		this.setClientConfiguration(ovpn.getClientConfiguration());
		this.setSessionConfiguration(ovpn.getSessionConfiguration());
	}
	
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public Date getValidTil() {
		return validTil;
	}
	public void setValidTil(Date validTil) {
		this.validTil = validTil;
	}
 
	
	public List<Notification> getNotifications() {
		return new ArrayList<>(notifications);
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications.clear();
		if(notifications!=null) {
			this.notifications.addAll(notifications);
		}
	}
	
	public void addNotification(Notification notification) {
		this.notifications.add(notification);
	}
	
	@JsonIgnore
	public String getClientConfigurationProvider() {
		return super.getClientConfigurationProvider();
	}
	
	@JsonIgnore
	public String getSessionConfigurationProvider() {
		return super.getSessionConfigurationProvider();
	}
	
	@JsonIgnore
	public JsonNode getClientConfiguration() {
		return super.getClientConfiguration();
	}

	@JsonIgnore
	public JsonNode getSessionConfiguration() {
		return super.getSessionConfiguration();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getSerial() {
		return serial;
	}


	public void setSerial(String serial) {
		this.serial = serial;
	}
}
