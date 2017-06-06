openvpn.pol
path "/pki-vpn/sign/vpn*" {
        policy = "write"
}
path "/pki-vpn/sign-verbatim/vpn*" {
        policy = "write"
}
path "/pki-vpn/tidy" {
     policy = "write"
}

path "/secret/vpn/*" {
     policy = "write"
}

openvpn_client.pol
/sys/policy/vpn_admin
path "/pki-vpn/sign/vpn*" {
        policy = "write"
}
path "/pki-vpn/tidy" {
     policy = "write"
}

path "/secret/vpn/*" {
     policy = "read"
}


$ vault write  auth/approle/role/an-identity-ovpn token_num_uses=10 token_ttl=5m token_max_ttl=30m bound_cidr_list="127.0.0.1/32,10.0.0.0/8,172.16.0.0/12,192.168.42.0/24" policies="vpn_admin,default"

$ vault read auth/approle/role/an-identity-ovpn/role-id
# get app id
$ vault write -f auth/approle/role/an-identity-ovpn/secret-id
