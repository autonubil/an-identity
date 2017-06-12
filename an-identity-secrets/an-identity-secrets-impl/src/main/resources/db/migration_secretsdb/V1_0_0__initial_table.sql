CREATE TABLE secrets (
   id VARCHAR(256) NOT NULL,
   secret bytea NOT NULL,
   PRIMARY KEY (path) 
)

