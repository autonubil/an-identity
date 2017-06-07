package com.autonubil.identity.ldap.api.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Notification;
import com.autonubil.identity.auth.api.entities.User;

public class LdapUser extends LdapObject implements Comparable<LdapUser>, User {

	private String sourceId;
	private String sourceName;
	private String username;
	private String accountName;
	private String displayName;
	private String cn;
	private String sn;
	private String mail;
	private String phone;
	private String mobilePhone;
	private String organization;
	private String department;
	private Date passwordExpires;
	private Date userExpires;
	private List<Group> groups = new ArrayList<>();
	private List<Notification> notifications = new ArrayList<>();

	public LdapUser() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Date getPasswordExpires() {
		return passwordExpires;
	}

	public void setPasswordExpires(Date passwordExpires) {
		this.passwordExpires = passwordExpires;
	}

	public Date getUserExpires() {
		return userExpires;
	}

	public void setUserExpires(Date userExpires) {
		this.userExpires = userExpires;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@Override
	public int compareTo(LdapUser o) {
		if(o==null) {
			return -1;
		}
		return (getCn()+"").compareToIgnoreCase(o.getCn()+"");
	}

	public void setGroups(List<Group> groups) {
		this.groups.clear();
		if(groups!=null) {
			this.groups.addAll(groups);
		}
	}
	
	public List<Group> getGroups() {
		return new ArrayList<>(groups);
	}

	public boolean isAdmin() {
		return false;
	}

	public void addGroup(Group g) {
		if(g!=null) {
			this.groups.add(g);
		}
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
	
}
