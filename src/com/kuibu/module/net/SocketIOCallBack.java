package com.kuibu.module.net;

import io.socket.IOAcknowledge;
import io.socket.SocketIOException;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kuibu.module.iterf.IEventHandler;

public class SocketIOCallBack extends EventCallback{
	
	public List<IEventHandler> receivers = new ArrayList<IEventHandler>();
	public SocketIOCallBack() 
	{}
	
	@Override
	protected void attachReceiver(IEventHandler handler) {
		// TODO Auto-generated method stub
		if(!receivers.contains(handler)){
			receivers.add(handler);
		}		
	}

	@Override
	protected void detachReceiver(IEventHandler handler) {
		// TODO Auto-generated method stub
		receivers.remove(handler);
	}

	@Override
	protected void notifyReceiver(JSONObject json) {
		// TODO Auto-generated method stub
		for(int i =0;i<receivers.size();i++){
			receivers.get(i).eventResponse(json);
		}
	}
	
	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
			notifyReceiver(json);
	}
	
	@Override
	public void onMessage(String data, IOAcknowledge ack) {
	    try {
	    	JSONObject obj = new JSONObject() ;
			obj.put("msg", data);
			notifyReceiver(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	@Override
	public void onError(SocketIOException socketIOException) {
	    System.out.println("an Error occured");
	    socketIOException.printStackTrace();
	}
	
	@Override
	public void onDisconnect() {
	    System.out.println("Connection terminated.");
	}
	
	@Override
	public void onConnect() {
	    System.out.println("Connection established");
	}
	
	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
	    System.out.println("Server triggered event '" + event + "'");
	    
	}

}
