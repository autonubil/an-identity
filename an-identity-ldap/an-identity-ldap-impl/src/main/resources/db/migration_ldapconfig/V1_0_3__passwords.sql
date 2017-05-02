CREATE TABLE ldap_config_passwords (
   ldap_config_id VARCHAR(36) NOT NULL,
   password VARCHAR(128) NOT NULL,
   PRIMARY KEY (ldap_config_id) 
)

