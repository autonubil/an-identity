CREATE TABLE audit_log (
   "id" VARCHAR(36) NOT NULL,
   "date" DECIMAL(20,0) NOT NULL,
   "component" VARCHAR(64) NOT NULL,
   "type" VARCHAR(64) NOT NULL,
   "session_id" VARCHAR(128) NOT NULL,
   "remote" VARCHAR(64) NOT NULL,
   "user" VARCHAR(256) NOT NULL,
   "action" VARCHAR(1024) NOT NULL,
   PRIMARY KEY (id) 
)