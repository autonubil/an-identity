CREATE TABLE ldap_config_field (
   id VARCHAR(36) NOT NULL,
   ldap_config_id VARCHAR(36) NOT NULL,
   object_class VARCHAR(128) NOT NULL,
   attribute_name VARCHAR(128) NOT NULL,
   attribute_type VARCHAR(128) NOT NULL,
   multi boolean NOT NULL,
   PRIMARY KEY (id)
);