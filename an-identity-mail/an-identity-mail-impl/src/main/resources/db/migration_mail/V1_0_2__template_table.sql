CREATE TABLE mail_template (
   id VARCHAR(36) NOT NULL,
   name VARCHAR(128) NOT NULL,
   module VARCHAR(128) NOT NULL,
   subject VARCHAR(4096) NOT NULL,
   text TEXT NOT NULL,
   html TEXT NULL,
   model TEXT NULL,
   PRIMARY KEY (id) 
);
	