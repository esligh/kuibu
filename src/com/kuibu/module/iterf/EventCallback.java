package com.kuibu.module.iterf;

import io.socket.IOCallback;

import org.json.JSONObject;

public abstract class EventCallback 
	implements IOCallback{
	protected abstract void attachReceiver(IEventHandler handler);
	protected abstract void detachReceiver(IEventHandler handler);
	protected abstract void notifyReceiver(JSONObject json);	
}
