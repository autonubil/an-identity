CREATE TABLE migrate (
   id           VARCHAR(36) NOT NULL,
   from_ldap_id VARCHAR(128) NOT NULL,
   to_ldap_id   VARCHAR(128) NOT NULL,
   PRIMARY KEY (id) 
)