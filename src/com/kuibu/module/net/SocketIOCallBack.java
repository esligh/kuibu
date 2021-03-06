package com.kuibu.module.net;

import io.socket.IOAcknowledge;
import io.socket.SocketIOException;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.kuibu.module.iterfaces.EventCallback;
import com.kuibu.module.iterfaces.IEventHandler;

public class SocketIOCallBack extends EventCallback{
	
	public List<IEventHandler> receivers = new ArrayList<IEventHandler>(); //事件处理者
	public SocketIOCallBack() 
	{}
	
	@Override
	protected void attachReceiver(IEventHandler handler) {
		if(!receivers.contains(handler)){
			receivers.add(handler);
		}		
	}

	@Override
	protected void detachReceiver(IEventHandler handler) {
		receivers.remove(handler);
	}

	@Override
	protected void notifyReceiver(JSONObject json) {
		for(int i =0;i<receivers.size();i++){
			receivers.get(i).eventResponse(json);
		}
	}
	
	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack){
			notifyReceiver(json);
	}
	
	@Override
	public void onMessage(String data, IOAcknowledge ack){
	    
	}
	
	@Override
	public void onError(SocketIOException socketIOException) {		
		Log.d("socketio", "error occur");
	    socketIOException.printStackTrace();
	}
	
	@Override
	public void onDisconnect() {
		Log.d("socketio", "Connection established");
	}
	
	@Override
	public void onConnect() {
		Log.d("socketio", "Connection established");
	}
	
	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		Log.d("socketio", "Server triggered event '" + event + "'");
	}
}
