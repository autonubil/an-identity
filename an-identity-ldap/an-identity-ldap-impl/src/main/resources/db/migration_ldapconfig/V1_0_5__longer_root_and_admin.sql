ALTER TABLE ldap_config 
ALTER COLUMN "admin_bind_dn" TYPE VARCHAR(128),
ALTER COLUMN "admin_bind_dn" DROP NOT NULL,
ALTER COLUMN "admin_bind_dn" SET DEFAULT '',
ALTER COLUMN "root_dse" TYPE VARCHAR(128),
ALTER COLUMN "root_dse" DROP NOT NULL,
ALTER COLUMN "root_dse" SET DEFAULT '';
