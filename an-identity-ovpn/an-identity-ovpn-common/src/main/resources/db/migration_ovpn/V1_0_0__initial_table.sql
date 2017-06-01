CREATE TABLE ovpn_source (
   id VARCHAR(36) NOT NULL,
   client_config_provider VARCHAR(36) NOT NULL,
   server_config_provider VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   description VARCHAR(256),
   configuration TEXT NOT NULL,
   PRIMARY KEY (id) 
);

CREATE TABLE ovpn_permission (
   ovpn_id VARCHAR(36) NOT NULL,
   source VARCHAR(36) NOT NULL,
   group_id VARCHAR(128) NOT NULL,
   name VARCHAR(256),
   PRIMARY KEY (ovpn_id,source,group_id) 
);
