/*
package org.springframework.vault.authentication;
import org.springframework.util.Assert;

public class LdapAuthenticationOptions {
	public static final String DEFAULT_APPID_AUTHENTICATION_PATH = "ldap";

	/**
	 * Path of the appid authentication backend mount.
	 * /
	private final String path;

	/**
	 * The Ldap
	 * /
	private final String username;
 

	private LdapAuthenticationOptions(String path, String username) {

		this.path = path;
		this.username = username;
 
	}

	/**
	 * @return a new {@link LdapAuthenticationOptionsBuilder}.
	 * /
	public static LdapAuthenticationOptionsBuilder builder() {
		return new LdapAuthenticationOptionsBuilder();
	}

	/**
	 * @return the mount path.
	 * /
	public String getPath() {
		return path;
	}

	/**
	 * @return the Username.
	 * /
	public String getUsername() {
		return username;
	}

 

	/**
	 * Builder for {@link LdapAuthenticationOptions}.
	 * /
	public static class LdapAuthenticationOptionsBuilder {

		private String path = DEFAULT_APPID_AUTHENTICATION_PATH;

		private String username;


		LdapAuthenticationOptionsBuilder() {
		}

		/**
		 * Configure the mount path.
		 *
		 * @param path must not be empty or {@literal null}.
		 * @return {@code this} {@link LdapAuthenticationOptionsBuilder}.
		 * @see #DEFAULT_APPID_AUTHENTICATION_PATH
		 * /
		public LdapAuthenticationOptionsBuilder path(String path) {

			Assert.hasText(path, "Path must not be empty");

			this.path = path;
			return this;
		}

		/**
		 * Configure the username.
		 *
		 * @param username must not be empty or {@literal null}.
		 * @return {@code this} {@link LdapAuthenticationOptionsBuilder}.
		 * /
		public LdapAuthenticationOptionsBuilder username(String username) {

			Assert.hasText(path, "Username must not be empty");

			this.username = username;
			return this;
		}

 
		/**
		 * Build a new {@link LdapAuthenticationOptions} instance. Requires
		 * {@link #userIdMechanism(LdapUserIdMechanism)} to be configured.
		 *
		 * @return a new {@link LdapAuthenticationOptions}.
		 * /
		public LdapAuthenticationOptions build() {

			Assert.hasText(username, "Ldap must not be empty");

			return new LdapAuthenticationOptions(path, username);
		}
}
}
*/