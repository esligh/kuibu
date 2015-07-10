package com.kuibu.module.net;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import com.kuibu.data.global.Constants;

public class EventSocket{
	private SocketIO mSocket ; 
	private IOCallback callback ; 
	public EventSocket(IOCallback callback) 
	{
		this.callback = callback ; 
	}

	public void SetUp() throws MalformedURLException, NoSuchAlgorithmException
	{
		SocketIO.setDefaultSSLSocketFactory(SSLContext.getDefault());
		mSocket = new SocketIO(Constants.Config.SOCKETIO_SERVER);
        mSocket.connect(callback);
	}
	
	public SocketIO getSocketIO()
	{
		return mSocket ; 
	}
	
	public void closeSocket()
	{
		mSocket.disconnect();  
	}
	
	public void reConnect()
	{
		mSocket.reconnect(); 
	}
}
