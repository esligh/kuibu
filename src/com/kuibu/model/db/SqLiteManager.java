package com.kuibu.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SqLiteManager {
	    private SqLiteHelper helper;  
	    protected SQLiteDatabase db;  
	      
	    public SqLiteManager(Context context) {  
	        helper = new SqLiteHelper(context);  
	        db = helper.getWritableDatabase();  
	    }  	    	     
	    public SQLiteDatabase getDB()
	    {
	    	return db ; 
	    }
	    /** 
	     * close database 
	     */  
	    public void closeDB() {  
	        db.close();  
	    }  	    
}
