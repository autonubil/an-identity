ALTER TABLE session ADD COLUMN source_id VARCHAR(36);
ALTER TABLE session ADD COLUMN user_name VARCHAR(50);
CREATE INDEX session_owner_idx ON session (source_id, user_name);
