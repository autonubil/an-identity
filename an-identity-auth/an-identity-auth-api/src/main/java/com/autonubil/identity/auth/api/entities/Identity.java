package com.autonubil.identity.auth.api.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Identity {

	private Date expires;
	private User user;
	private List<User> linked = new ArrayList<>();

	public List<User> getLinked() {
		return Collections.unmodifiableList(linked);
	}

	public void setLinked(List<User> linked) {
		this.linked.clear();
		if(linked!=null) {
			this.linked.addAll(linked);
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public void addLinked(User u) {
		this.linked.add(u);
		
	}

}
