package com.kuibu.model.bean;

import org.json.JSONObject;


/**
 * @author ThinkPad
 * class BaseBean 
 * 
 */

public abstract class BaseBean {	 
	public abstract void parseFromJson(JSONObject obj);	
}
