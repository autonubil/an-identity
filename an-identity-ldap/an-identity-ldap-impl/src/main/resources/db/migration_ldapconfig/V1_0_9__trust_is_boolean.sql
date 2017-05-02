ALTER TABLE ldap_config 
DROP COLUMN 
	trust_all;
    
ALTER TABLE ldap_config 
ADD COLUMN trust_all BOOL;
	