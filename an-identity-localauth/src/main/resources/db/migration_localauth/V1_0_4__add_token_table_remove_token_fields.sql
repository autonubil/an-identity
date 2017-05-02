ALTER TABLE admin_user DROP COLUMN token;
ALTER TABLE admin_user DROP COLUMN token_expires;

CREATE TABLE admin_user_token (
   admin_user_id VARCHAR(36) NOT NULL,
   token VARCHAR(50) NOT NULL,
   token_expires DECIMAL(20,0) NOT NULL,
   PRIMARY KEY (admin_user_id) 
);

DROP TABLE admin_user_password;

CREATE TABLE admin_user_password (
   admin_user_id VARCHAR(36) NOT NULL,
   password VARCHAR(128) NOT NULL,
   PRIMARY KEY (admin_user_id) 
);
