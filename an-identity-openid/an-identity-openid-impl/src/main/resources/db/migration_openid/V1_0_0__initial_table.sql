CREATE TABLE application (
   client_id VARCHAR(36) NOT  NULL,
   name character varying(128) UNIQUE,
   secret bytea NOT NULL,
   scopes VARCHAR(64)  NOT NULL default '',
   linked_app_id character varying(36),
   callback_url character varying(256),
   trusted_app boolean default false,
   cliennt_signing_alg  character varying(5) not NULL default 'RS256',
   PRIMARY KEY (client_id) 
);

CREATE TABLE session (
   id VARCHAR(36) NOT NULL,
   expires numeric(20,0) NOT NULL,
   definition TEXT NOT NULL,
   PRIMARY KEY (id) 
);

CREATE TABLE application_permission (
   client_id VARCHAR(36) NOT NULL,
   name VARCHAR(256),
   source VARCHAR(36) NOT NULL,
   group_id VARCHAR(128) NOT NULL,
   PRIMARY KEY (client_id,source,group_id) 
);

