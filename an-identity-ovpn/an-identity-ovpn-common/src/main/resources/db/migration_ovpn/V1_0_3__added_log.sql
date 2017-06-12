CREATE TABLE vpn_log (
   id VARCHAR(36) NOT NULL,
   activity VARCHAR(36) NOT NULL,
   "date" DECIMAL(20,0) NOT NULL,
   vpn_id VARCHAR(36) NOT NULL,
   "user" VARCHAR(256) NOT NULL,
   source_ip VARCHAR(15) NOT NULL,
   source VARCHAR(255) NOT NULL,
   remote VARCHAR(64) NOT NULL,
   description VARCHAR(1024) NOT NULL,
   PRIMARY KEY (id) 
);

