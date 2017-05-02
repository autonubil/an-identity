CREATE TABLE mail_config (
   id VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   description VARCHAR(512) NULL,
   host VARCHAR(128) NOT NULL,
   port DECIMAL(5,0) NOT NULL,
   encryption VARCHAR(16) NOT NULL,
   auth DECIMAL(1,0) NOT NULL,
   username DECIMAL(1,0) NULL,
   password DECIMAL(1,0) NULL,
   PRIMARY KEY (id) 
);
