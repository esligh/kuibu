package com.kuibu.module.net;

import io.socket.SocketIO;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import com.kuibu.data.global.Constants;
import com.kuibu.module.iterf.EventCallback;
/**
 * socketio 用来建立与服务端的长连接
 * */
public class EventSocket{
	
	private SocketIO mSocket; 
	private EventCallback callback; //回调 
	
	public EventSocket(EventCallback callback){
		this.callback = callback ; 
	}
	
	public void SetUp() throws MalformedURLException, NoSuchAlgorithmException
	{
		if(mSocket == null) {
			SocketIO.setDefaultSSLSocketFactory(SSLContext.getDefault());
			mSocket = new SocketIO(Constants.Config.SOCKETIO_SERVER);
	        mSocket.connect(callback);  
		}
	
	}
	
	public SocketIO getSocketIO()
	{
		return mSocket ; 
	}
	
	public void closeSocket(){
		mSocket.disconnect();  
	}
	
	public void reConnect(){
		mSocket.reconnect(); 
	}

}
