package com.kuibu.model.vo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.db.SqLiteManager;

/**
 * @class collection view operation 
 * @author ThinkPad
 * 
 */
public class CollectionVo extends SqLiteManager {
	public static final String table_name = "collection" ; 
	private final String BASE_QUERY_STR = " SELECT _id,cid ,pid,tid,type,title,content,is_pub,is_sync,last_modify FROM collection " ;
	
    public CollectionVo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
  
    public void add(List<CollectionBean> collections) {  
        db.beginTransaction();   
        try {  
            for (CollectionBean c : collections) {  
                db.execSQL("INSERT INTO collection(pid,tid,type,title,content,create_by,cid,is_sync) VALUES( ?, ?, ?, ?, ?, ?, ?,?)", 
                		new Object[]{c.pid, c.tid,c.type,c.title,c.content,c.createBy,c.cid,c.isSync});
            }  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
        }  
    }  
    
    public void add(CollectionBean c)
    {
    	db.beginTransaction();
    	try{
    		db.execSQL("INSERT INTO collection(pid,tid,type,title,content,create_by,cid,is_sync) VALUES(?, ?, ?, ?, ?, ?, ?,?)",
    				new Object[]{c.pid, c.tid,c.type,c.title,c.content,c.createBy,c.cid,c.isSync});
    		db.setTransactionSuccessful();
    	}finally{
    		db.endTransaction();
    	}
    }
    
    public int getlastkey()
    {
    	int key=0; 
    	Cursor cursor = db.rawQuery("select last_insert_rowid() from collection ",null);
    	if(cursor.moveToFirst()){
    		key = cursor.getInt(0);
    	}
    	return key ; 
    }
    
    /**
     * @param cons eg title = ? ,content = ?
     * @param args 
     */
    public void update(String fields, String where ,String[] args)
    {
    	db.beginTransaction();
    	try{
    		StringBuffer SQL = new StringBuffer("update collection set ");
    		SQL.append(fields);
    		SQL.append(" where ").append(where);
    		db.execSQL(SQL.toString(),args);
    		db.setTransactionSuccessful(); 
    	}finally{
    		db.endTransaction();
    	}
    }
    
    /** 
     * delete old collection 
     * @param collection 
     */  
    public void delete(CollectionBean c) {  
        db.delete("collection", "_id = ?", new String[]{String.valueOf(c._id)});  
    }  
    
    public void delete(int pid)
    {
    	db.delete("collection", "pid = ?", new String[]{String.valueOf(pid)});
    }
    
    public void delete (String cons,String []args)
    {
    	db.beginTransaction();
    	try{
        	StringBuffer SQL = new StringBuffer("delete from collection where "); 	
        	SQL.append(cons);
        	db.execSQL(SQL.toString(),args);
        	db.setTransactionSuccessful();
    	}finally{
    		db.endTransaction();
    	} 	
    }
    
    public List<CollectionBean> queryAll() {  
        ArrayList<CollectionBean> collections = new ArrayList<CollectionBean>();  
        Cursor c = db.rawQuery("SELECT _id,cid,pid,tid,type,title,content,is_pub,is_sync,last_modify FROM collection ", null);  
        while (c.moveToNext()) {  
        	CollectionBean collection = new CollectionBean();  
        	collection._id = c.getString(c.getColumnIndex("_id")); 
        	collection.cid = c.getString(c.getColumnIndex("cid"));
        	collection.pid = c.getString(c.getColumnIndex("pid"));  
            collection.tid = c.getString(c.getColumnIndex("tid"));
            collection.type= c.getString(c.getColumnIndex("type"));
            collection.title = c.getString(c.getColumnIndex("title"));
            collection.content = c.getString(c.getColumnIndex("content"));
            collection.isPublish = c.getInt(c.getColumnIndex("is_pub"));
            collection.isSync = c.getInt(c.getColumnIndex("is_sync"));
            collection.createDate = c.getString(c.getColumnIndex("last_modify"));
            collections.add(collection);  
        }  
        c.close();  
        return collections;  
    }  
       
    public CollectionBean querywithkey(String id)
    {
    	CollectionBean collection = new CollectionBean();
    	Cursor c = db.rawQuery("SELECT _id,cid,pid,tid,type,title,content,is_pub,is_sync,last_modify FROM collection where _id = ? ",
    			new String[]{id});
    	
        while (c.moveToNext()) {    
        	collection._id = c.getString(c.getColumnIndex("_id"));  
        	collection.cid = c.getString(c.getColumnIndex("cid"));
        	collection.pid = c.getString(c.getColumnIndex("pid"));  
            collection.tid = c.getString(c.getColumnIndex("tid"));
            collection.type= c.getString(c.getColumnIndex("type"));
            collection.title = c.getString(c.getColumnIndex("title"));
            collection.isPublish = c.getInt(c.getColumnIndex("is_pub"));
            collection.isSync = c.getInt(c.getColumnIndex("is_sync"));
            collection.content = c.getString(c.getColumnIndex("content"));
            collection.createDate =c.getString(c.getColumnIndex("last_modify"));  
            break;
        }  
        c.close();  
        return collection;  
    }
    
    public List<CollectionBean> queryWithcons(String cons ,String[] args)
    {
    	ArrayList<CollectionBean> collections = new ArrayList<CollectionBean>();
    	StringBuffer SQL = new StringBuffer(BASE_QUERY_STR);
    	SQL.append(" where ").append(cons);
        Cursor c = db.rawQuery(SQL.toString(),args);
        
        while (c.moveToNext()) {  
        	CollectionBean collection = new CollectionBean();  
        	collection._id = c.getString(c.getColumnIndex("_id"));  
        	collection.cid = c.getString(c.getColumnIndex("cid"));
        	collection.pid = c.getString(c.getColumnIndex("pid"));  
            collection.tid = c.getString(c.getColumnIndex("tid"));
            collection.type= c.getString(c.getColumnIndex("type"));
            collection.isPublish = c.getInt(c.getColumnIndex("is_pub"));
            collection.isSync = c.getInt(c.getColumnIndex("is_sync"));
            collection.title = c.getString(c.getColumnIndex("title"));
            collection.content = c.getString(c.getColumnIndex("content"));
            collection.createDate = c.getString(c.getColumnIndex("last_modify"));
            collections.add(collection);  
        }  
        c.close();  
        return collections; 
    }
}