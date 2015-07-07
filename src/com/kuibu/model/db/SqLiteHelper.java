package com.kuibu.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqLiteHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "caddy.db";  
    private static final int DATABASE_VERSION = 1;  
      
    public SqLiteHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS collection" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, pid INTEGER ,tid INTEGER, type INTEGER, title VARCHAR ,"
                + "content TEXT, is_pub INTEGER DEFAULT 0 ,is_sync INTEGER DEFAULT 0 , create_by BIGINT, cid VARCHAR UNIQUE ,last_modify DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE IF NOT EXISTS imglib" +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, cid INTEGER, img_url VARCHAR, "
                + " last_modify  DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE IF NOT EXISTS collectpack " + 
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, pack_name VARCHAR, pack_desc VARCHAR, collect_count INTEGER DEFAULT 0,"
                + " create_by BIGINT,is_private INTEGER , topic_id VARCHAR , pack_id VARCHAR UNIQUE, is_sync DEFAULT 0, "
                + " last_modify DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }  
    
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        //db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
    }  
}
