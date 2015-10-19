package com.kuibu.data.global;

public class AppInfo {
	public int screenWidth ; 
	public int screenHeight ; 
	
	private static AppInfo instance ; 
	public static AppInfo getInstance()
	{
		if(instance == null){
			instance = new AppInfo();
		}
		return instance ; 
	}
	
}
