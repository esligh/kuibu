package com.kuibu.model.vo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.kuibu.model.bean.CollectPackBean;

public class CollectPackVo extends BaseDbVo{
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
        getDB().beginTransaction();   
        try {  
            for (CollectPackBean p : packs) {  
            	getDB().execSQL("INSERT INTO "+table_name+"( pack_name,pack_desc,pack_type,create_by,is_private,topic_id,pack_id,is_sync)"
                		+ " VALUES(?, ?, ?, ?, ?, ?, ? ,?)", 
                		new Object[]{p.pack_name, p.pack_desc,p.pack_type,p.create_by,
                		p.is_private,p.topic_id,p.pack_id,p.is_sync});  
            }  
            getDB().setTransactionSuccessful();
        } finally {  
        	getDB().endTransaction();  
        }  
    }  
    
    public void add(CollectPackBean p)
    {
    	getDB().beginTransaction();
    	try{
    		getDB().execSQL("INSERT INTO "+table_name+"( pack_name,pack_desc,pack_type,create_by,is_private,topic_id,pack_id,is_sync )"
            		+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?)", 
            		new Object[]{p.pack_name, p.pack_desc,p.pack_type,p.create_by,
    				p.is_private,p.topic_id,p.pack_id,p.is_sync});
    		getDB().setTransactionSuccessful();
    	}finally{
    		getDB().endTransaction();
    	}
    }
    
    public int getlastkey()
    {
    	int key=0; 
    	Cursor cursor = getDB().rawQuery("select last_insert_rowid() from collectpack ",null);
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
    	getDB().delete(table_name, "_id = ?", new String[]{String.valueOf(c._id)});         
    }  
    
    public List<CollectPackBean> queryAll(String uid) {  
        ArrayList<CollectPackBean> packs = new ArrayList<CollectPackBean>();  
        Cursor c = getDB().rawQuery("SELECT _id,pack_id,pack_name,pack_desc,pack_type,collect_count FROM "+table_name + 
        		" where create_by = ? ", new String[]{uid});  
        while (c.moveToNext()) {  
        	CollectPackBean pack = new CollectPackBean();  
        	pack._id = c.getString(c.getColumnIndex("_id"));
        	pack.pack_id = c.getString(c.getColumnIndex("pack_id"));
        	pack.pack_name = c.getString(c.getColumnIndex("pack_name"));  
        	pack.pack_desc = c.getString(c.getColumnIndex("pack_desc"));
        	pack.pack_type = c.getString(c.getColumnIndex("pack_type"));
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
    	Cursor c = getDB().rawQuery("SELECT _id,pack_id,pack_name,pack_desc,pack_type,collect_count,last_modify FROM collectpack where _id = ? ",
    			new String[]{_id});
        while (c.moveToNext()) {
        	bean.pack_id = c.getString(c.getColumnIndex("pack_id"));
        	bean.pack_name= c.getString(c.getColumnIndex("pack_name"));
            bean.pack_desc = c.getString(c.getColumnIndex("pack_desc"));
            bean.pack_type = c.getString(c.getColumnIndex("pack_type"));
            bean.collect_count = c.getString(c.getColumnIndex("collect_count"));
            break;
        }
    	return bean ; 
    }
    public void update(CollectPackBean item)
    {
    	getDB().beginTransaction(); 
    	try{
    		StringBuffer SQL  =new StringBuffer("update collectpack set ");
    		SQL.append(" pack_name = ? , pack_desc = ? , pack_type = ? ,topic_id = ? , is_private = ? ");
    		SQL.append(" where _id = ?");
    		getDB().execSQL(SQL.toString(), new Object[]{item.pack_name,item.pack_desc,item.pack_type,item.topic_id,item.is_private,item._id});
    		getDB().setTransactionSuccessful(); 
    	}finally{
    		getDB().endTransaction();
    	}
    }
    
    public void update(String fields, String where ,String[] args)
    {
    	getDB().beginTransaction();
    	try{
    		StringBuffer SQL = new StringBuffer("update collectpack set ");
    		SQL.append(fields);
    		SQL.append(" where ").append(where);
    		getDB().execSQL(SQL.toString(),args);
    		getDB().setTransactionSuccessful(); 
    	}finally{
    		getDB().endTransaction();
    	}
    }
}
