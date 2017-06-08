package com.autonubil.identity.ovpn.api.entities;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class OvpnPushOption {
	private String option;
	private Object value;
	
	
	public OvpnPushOption() {
		
	}
	
	public OvpnPushOption(String option, Object value) {
        this.option = option;
        this.value = value;
    }
	
	@JsonAnySetter
    public void set(String option, Object value) {
        this.option = option;
        this.value = value;
    }
	
	 // "any getter" needed for serialization    
    @JsonAnyGetter
    public Map<String,Object> any() {
    	Map<String,Object> map = new LinkedHashMap<>(); 
        map.put( this.option, this.value); 
        return map; 
    }

    
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("push %s %s", this.getOption(), this.getValue());
	}
	
	
}
