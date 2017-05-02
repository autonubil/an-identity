CREATE TABLE migrate_group (
   id         VARCHAR(36) NOT NULL,
   migrate_id VARCHAR(36) NOT NULL,
   from_group VARCHAR(128) NOT NULL,
   to_group   VARCHAR(128) NOT NULL,
   PRIMARY KEY (id) 
)

