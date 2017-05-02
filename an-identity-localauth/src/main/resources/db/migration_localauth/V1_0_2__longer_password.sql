ALTER TABLE admin_user_password DROP COLUMN password;

ALTER TABLE admin_user_password ADD COLUMN password VARCHAR(128);
