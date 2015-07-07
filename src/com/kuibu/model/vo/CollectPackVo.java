package com.kuibu.model.vo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.kuibu.model.bean.CollectPackBean;
import com.kuibu.model.db.SqLiteManager;

public class CollectPackVo extends SqLiteManager{
	public static final String table_name = " collectpack ";
    public CollectPackVo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/** 
     * add Collection 
     * @param LCollection 
     */  
    public void add(List<CollectPackBean> packs) {  
        db.beginTransaction();   
        try {  
            for (CollectPackBean p : packs) {  
                db.execSQL("INSERT INTO "+table_name+"( pack_name,pack_desc,create_by,is_private,topic_id,pack_id,is_sync)"
                		+ " VALUES(?, ?, ?, ?, ?, ? ,?)", 
                		new Object[]{p.pack_name, p.pack_desc,p.create_by,
                		p.is_private,p.topic_id,p.pack_id,p.is_sync});  
            }  
            db.setTransactionSuccessful();
        } finally {  
            db.endTransaction();  
        }  
    }  
    
    public void add(CollectPackBean p)
    {
    	db.beginTransaction();
    	try{
    		db.execSQL("INSERT INTO "+table_name+"( pack_name,pack_desc,create_by,is_private,topic_id,pack_id,is_sync )"
            		+ " VALUES(?, ?, ?, ?, ?, ?, ?)", 
            		new Object[]{p.pack_name, p.pack_desc,p.create_by,
    				p.is_private,p.topic_id,p.pack_id,p.is_sync});
    		db.setTransactionSuccessful();
    	}finally{
    		db.endTransaction();
    	}
    }
    
    public int getlastkey()
    {
    	int key=0; 
    	Cursor cursor = db.rawQuery("select last_insert_rowid() from collectpack ",null);
    	if(cursor.moveToFirst()){
    		key = cursor.getInt(0);
    	}
    	return key ; 
    }
    
    /** 
     * delete old collection 
     * @param collection 
     */  
    public void delete(CollectPackBean c) {  
        db.delete(table_name, "_id = ?", new String[]{String.valueOf(c._id)});         
    }  
    
    public List<CollectPackBean> queryAll(String uid) {  
        ArrayList<CollectPackBean> packs = new ArrayList<CollectPackBean>();  
        Cursor c = db.rawQuery("SELECT _id,pack_id,pack_name,pack_desc,collect_count FROM "+table_name + 
        		" where create_by = ? ", new String[]{uid});  
        while (c.moveToNext()) {  
        	CollectPackBean pack = new CollectPackBean();  
        	pack._id = c.getString(c.getColumnIndex("_id"));
        	pack.pack_id = c.getString(c.getColumnIndex("pack_id"));
        	pack.pack_name = c.getString(c.getColumnIndex("pack_name"));  
        	pack.pack_desc = c.getString(c.getColumnIndex("pack_desc"));
        	int count = c.getInt(c.getColumnIndex("collect_count"));
        	pack.collect_count = String.valueOf(count);             	
            packs.add(pack);  
        }  
        c.close();  
        return packs;  
    }  
    
    public CollectPackBean queryWithkey(String _id)
    {
    	CollectPackBean bean = new CollectPackBean();
    	Cursor c = db.rawQuery("SELECT _id,pack_id,pack_name,pack_desc,collect_count,last_modify FROM collectpack where _id = ? ",
    			new String[]{_id});
        while (c.moveToNext()) {
        	bean.pack_id = c.getString(c.getColumnIndex("pack_id"));
        	bean.pack_name= c.getString(c.getColumnIndex("pack_name"));
            bean.pack_desc = c.getString(c.getColumnIndex("pack_desc"));
            bean.collect_count = c.getString(c.getColumnIndex("collect_count"));
            break;
        }
    	return bean ; 
    }
    public void update(CollectPackBean item)
    {
    	db.beginTransaction(); 
    	try{
    		StringBuffer SQL  =new StringBuffer("update collectpack set ");
    		SQL.append(" pack_name = ? , pack_desc = ? , topic_id = ? , is_private = ? ");
    		SQL.append(" where _id = ?");
    		db.execSQL(SQL.toString(), new Object[]{item.pack_name,item.pack_desc,item.topic_id,item.is_private,item._id});
    		db.setTransactionSuccessful(); 
    	}finally{
    		db.endTransaction();
    	}
    }
    
    public void update(String fields, String where ,String[] args)
    {
    	db.beginTransaction();
    	try{
    		StringBuffer SQL = new StringBuffer("update collectpack set ");
    		SQL.append(fields);
    		SQL.append(" where ").append(where);
    		db.execSQL(SQL.toString(),args);
    		db.setTransactionSuccessful(); 
    	}finally{
    		db.endTransaction();
    	}
    }
}
