CREATE TABLE mail_config_password (
   mail_config_id VARCHAR(36) NOT NULL,
   password VARCHAR(128) NULL,
   PRIMARY KEY (mail_config_id) 
);

ALTER TABLE mail_config DROP COLUMN username;
ALTER TABLE mail_config DROP COLUMN password;
ALTER TABLE mail_config ADD COLUMN username VARCHAR(128);

