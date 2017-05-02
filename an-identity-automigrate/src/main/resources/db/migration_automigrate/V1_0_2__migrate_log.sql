CREATE TABLE migrate_log (
   id         VARCHAR(36) NOT NULL,
   migrate_id VARCHAR(36) NOT NULL,
   from_group VARCHAR(128) NOT NULL,
   user_id    VARCHAR(128) NOT NULL,
   success    BOOLEAN NOT NULL,
   message    TEXT NOT NULL,
   PRIMARY KEY (id) 
)

