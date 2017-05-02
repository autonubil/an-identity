CREATE TABLE ldap_config (
   id VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   host VARCHAR(128) NOT NULL,
   port DECIMAL(5,0) NOT NULL,
   encryption VARCHAR(24) NOT NULL,
   auth VARCHAR(24) NOT NULL,
   admin_bind_dn VARCHAR(24) NOT NULL,
   server_type VARCHAR(24) NOT NULL,
   root_dse VARCHAR(24) NOT NULL,
   use_as_auth DECIMAL(1,0) NOT NULL,
   PRIMARY KEY (id) 
)

