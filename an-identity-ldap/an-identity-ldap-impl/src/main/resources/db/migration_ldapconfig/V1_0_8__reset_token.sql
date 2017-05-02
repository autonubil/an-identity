CREATE TABLE reset_token (
   source VARCHAR(128) NOT NULL,
   user_id VARCHAR(128) NOT NULL,
   token VARCHAR(128) NOT NULL,
   token_expires TIMESTAMP NOT NULL,
   PRIMARY KEY (source,user_id) 
)

