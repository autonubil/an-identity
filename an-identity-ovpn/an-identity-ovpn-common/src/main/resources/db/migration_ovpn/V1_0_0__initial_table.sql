CREATE TABLE vpn (
   id VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   description VARCHAR(256),
   client_config_provider VARCHAR(36) NOT NULL,
   server_config_provider VARCHAR(36) NOT NULL,
   client_configuration TEXT NOT NULL DEFAULT '{}',
   server_configuration TEXT NOT NULL DEFAULT '{}',
   PRIMARY KEY (id) 
);



CREATE TABLE vpn_permission (
   vpn_id VARCHAR(36) NOT NULL,
   name VARCHAR(256),
   source VARCHAR(36) NOT NULL,
   group_id VARCHAR(128) NOT NULL,
   PRIMARY KEY (vpn_id,source,group_id) 
);
