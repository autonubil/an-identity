CREATE TABLE admin_user_otp (
   admin_user_id VARCHAR(36) NOT NULL,
   secret VARCHAR(50) NOT NULL,
   PRIMARY KEY (admin_user_id) 
);

