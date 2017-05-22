package com.autonubil.identity.ovpn.common.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.ovpn.api.OvpnConfigService;

@RestController
public class OvpnConfigController {
	
	@Autowired
	private OvpnConfigService ovpnConfigService;
	

}
