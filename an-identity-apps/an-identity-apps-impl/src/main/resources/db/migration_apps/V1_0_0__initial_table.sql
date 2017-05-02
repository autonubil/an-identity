CREATE TABLE app (
   id VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   url VARCHAR(256) NOT NULL,
   PRIMARY KEY (id) 
);

CREATE TABLE app_permission (
   app_id VARCHAR(36) NOT NULL,
   source VARCHAR(128) NOT NULL,
   group_id VARCHAR(128) NOT NULL,
   PRIMARY KEY (app_id,source,group_id) 
);
