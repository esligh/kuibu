package com.kuibu.module.net;

import io.socket.IOCallback;

import org.json.JSONObject;

import com.kuibu.module.iterf.IEventHandler;

public abstract class EventCallback 
	implements IOCallback{
	protected abstract void attachReceiver(IEventHandler handler);
	protected abstract void detachReceiver(IEventHandler handler);
	protected abstract void notifyReceiver(JSONObject json);	
}
