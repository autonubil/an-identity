CREATE TABLE session (
   id VARCHAR(36) NOT NULL,
   expires numeric(20,0) NOT NULL,
   definition TEXT NOT NULL,
   PRIMARY KEY (id) 
)