CREATE TABLE admin_user (
   id VARCHAR(36) NOT NULL,
   username VARCHAR(50) NOT NULL,
   PRIMARY KEY (id) 
);

CREATE TABLE admin_user_password (
   id VARCHAR(36) NOT NULL,
   admin_user_id VARCHAR(36) NOT NULL,
   password VARCHAR(50) NOT NULL,
   PRIMARY KEY (id) 
);