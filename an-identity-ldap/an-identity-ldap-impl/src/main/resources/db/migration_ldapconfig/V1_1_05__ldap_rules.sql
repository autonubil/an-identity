CREATE TABLE ldap_rule (
   id VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   ldap_config_id VARCHAR(36) NOT NULL,
   source_filter VARCHAR(4096) NOT NULL,
   source_attributes VARCHAR(512) NOT NULL,
   target_filter VARCHAR(4096) NOT NULL,
   target_attribute VARCHAR(512) NOT NULL,
   interval INTEGER,
   PRIMARY KEY (id) 
)

