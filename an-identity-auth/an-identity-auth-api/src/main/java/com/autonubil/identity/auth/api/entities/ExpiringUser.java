package com.autonubil.identity.auth.api.entities;

import java.util.Date;

public interface ExpiringUser extends User {
	public boolean isExpired();
	public Date getUserExpires();

}
