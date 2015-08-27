package com.kuibu.model.vo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.kuibu.model.bean.CollectionBean;

/**
 * @class collection view operation 
 * @author ThinkPad
 * 
 */
public class CollectionVo extends BaseDbVo {
	public static final String table_name = "collection" ; 
	private final String BASE_QUERY_STR = " SELECT _id,cid ,pid,tid,type,title,content,cover,is_pub,is_sync,last_modify FROM collection " ;
	
    public CollectionVo(Context context) {
		super(context);
	}
  
    public void add(List<CollectionBean> collections) {  
        getDB().beginTransaction();   
        try {  
            for (CollectionBean c : collections) {  
            	getDB().execSQL("INSERT INTO collection(pid,tid,type,title,content,cover,create_by,cid,is_sync) VALUES( ?, ?, ?, ?, ?, ?, ?, ?,?)", 
                		new Object[]{c.pid, c.tid,c.type,c.title,c.content,c.cover,c.createBy,c.cid,c.isSync});
            }  
            getDB().setTransactionSuccessful();  
        } finally {  
        	getDB().endTransaction();  
        }  
    }  
    
    public void add(CollectionBean c)
    {
    	getDB().beginTransaction();
    	try{
    		getDB().execSQL("INSERT INTO collection(pid,tid,type,title,content,cover,create_by,cid,is_sync) VALUES(?, ?, ?, ?, ?, ?, ?, ?,?)",
    				new Object[]{c.pid, c.tid,c.type,c.title,c.content,c.cover,c.createBy,c.cid,c.isSync});
    		getDB().setTransactionSuccessful();
    	}finally{
    		getDB().endTransaction();
    	}
    }
    
    public int getlastkey()
    {
    	int key=0; 
    	Cursor cursor = getDB().rawQuery("select last_insert_rowid() from collection ",null);
    	if(cursor.moveToFirst()){
    		key = cursor.getInt(0);
    	}
    	cursor.close();
    	return key ; 
    }
    
    /**
     * @param cons eg title = ? ,content = ?
     * @param args 
     */
    public void update(String fields, String where ,String[] args)
    {
    	getDB().beginTransaction();
    	try{
    		StringBuffer SQL = new StringBuffer("update collection set ");
    		SQL.append(fields);
    		SQL.append(" where ").append(where);
    		getDB().execSQL(SQL.toString(),args);
    		getDB().setTransactionSuccessful(); 
    	}finally{
    		getDB().endTransaction();
    	}
    }
    
    /** 
     * delete old collection 
     * @param collection 
     */  
    public void delete(CollectionBean c) {  
    	getDB().delete("collection", "_id = ?", new String[]{String.valueOf(c._id)});
    	
    }  
    
    public void delete(int pid)
    {
    	getDB().delete("collection", "pid = ?", new String[]{String.valueOf(pid)});
    }
    
    public void delete (String cons,String []args)
    {
    	getDB().beginTransaction();
    	try{
        	StringBuffer SQL = new StringBuffer("delete from collection where "); 	
        	SQL.append(cons);
        	getDB().execSQL(SQL.toString(),args);
        	getDB().setTransactionSuccessful();
    	}finally{
    		getDB().endTransaction();
    	} 	
    }
    
    public void delete(ArrayList<String> ids)
    {
    	if(ids==null || ids.size()<=0)
    		return ;
		StringBuffer cons = new StringBuffer(" _id " ); 
		StringBuffer buffer = new StringBuffer( " in ( ");
		for(int i =0;i<ids.size();i++){
			buffer.append("?,");			            			
		}          		
		buffer = new StringBuffer(buffer.subSequence(0, buffer.length()-1));
		buffer.append(" ) ");
		delete(cons.append(buffer).toString(),(String[])ids.toArray(new String[ids.size()]));
    }
    
    public List<CollectionBean> queryAll() {  
        ArrayList<CollectionBean> collections = new ArrayList<CollectionBean>();  
        Cursor c = getDB().rawQuery("SELECT _id,cid,pid,tid,type,title,content,cover,is_pub,is_sync,last_modify FROM collection order by last_modify desc ", null);  
        while (c.moveToNext()) {  
        	CollectionBean collection = new CollectionBean();  
        	collection._id = c.getString(c.getColumnIndex("_id")); 
        	collection.cid = c.getString(c.getColumnIndex("cid"));
        	collection.pid = c.getString(c.getColumnIndex("pid"));  
            collection.tid = c.getString(c.getColumnIndex("tid"));
            collection.type= c.getString(c.getColumnIndex("type"));
            collection.title = c.getString(c.getColumnIndex("title"));
            collection.content = c.getString(c.getColumnIndex("content"));
            collection.cover = c.getString(c.getColumnIndex("cover"));
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
    	Cursor c = getDB().rawQuery("SELECT _id,cid,pid,tid,type,title,content,cover,is_pub,is_sync,last_modify FROM collection where _id = ? ",
    			new String[]{id});
        while (c.moveToNext()) {    
        	collection._id = c.getString(c.getColumnIndex("_id"));  
        	collection.cid = c.getString(c.getColumnIndex("cid"));
        	collection.pid = c.getString(c.getColumnIndex("pid"));  
            collection.tid = c.getString(c.getColumnIndex("tid"));
            collection.type= c.getString(c.getColumnIndex("type"));
            collection.title = c.getString(c.getColumnIndex("title"));
            collection.cover = c.getString(c.getColumnIndex("cover"));
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
    	SQL.append(" order by last_modify desc");
        Cursor c = getDB().rawQuery(SQL.toString(),args);
        
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
            collection.cover = c.getString(c.getColumnIndex("cover"));
            collection.createDate = c.getString(c.getColumnIndex("last_modify"));
            collections.add(collection);  
        }  
        c.close();  
        return collections; 
    }
}