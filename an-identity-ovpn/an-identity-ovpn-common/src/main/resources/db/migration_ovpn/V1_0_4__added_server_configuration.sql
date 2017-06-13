ALTER TABLE vpn ADD COLUMN server_config_provider VARCHAR(36) NOT NULL;
ALTER TABLE vpn ADD COLUMN server_configuration TEXT NOT NULL;
