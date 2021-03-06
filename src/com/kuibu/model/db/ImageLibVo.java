package com.kuibu.model.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.kuibu.model.entity.LImageLib;

public class ImageLibVo extends BaseDbVo{
	
	public static final String BASE_QUERY_STR = "select * from imglib ";
	
	public ImageLibVo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
    @SuppressLint("SimpleDateFormat")
	public void add(List<LImageLib> imgs) {  
        getDB().beginTransaction();   
        try {  
            for (LImageLib img : imgs) {  
            	getDB().execSQL("INSERT INTO imglib VALUES(null, ?, ?, ?)", 
                		new Object[]{img.cid, img.img_url, 
                		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});  
            }  
            getDB().setTransactionSuccessful();  
        } finally {  
        	getDB().endTransaction();    
        }  
    }  
    
    @SuppressLint("SimpleDateFormat")
	public void add(String cid , List<String> urls)
    {
    	getDB().beginTransaction();   
        try {  
            for (String url : urls) {  
            	getDB().execSQL("INSERT INTO imglib VALUES(null, ?, ?, ?)", 
                		new Object[]{cid, url, 
                		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});  
            }  
            getDB().setTransactionSuccessful();   
        } finally {  
        	getDB().endTransaction();      
        }  
    }
    
    public void add(String cid , String url)
    {
    	getDB().beginTransaction();   
        try {  
            getDB().execSQL("INSERT INTO imglib VALUES(null, ?, ?, ?)", 
                		new Object[]{cid, url, 
                		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});   
            getDB().setTransactionSuccessful();   
        } finally {  
        	getDB().endTransaction();      
        }  
    }
      
    public void delete(String cid) {  
    	getDB().delete("imglib", "cid = ?", new String[]{String.valueOf(cid)});  
    }  
      
    public void delete(String cons , String[] args)
    {    	
    	getDB().beginTransaction();
    	try{
    		getDB().execSQL("delete from imglib where " + cons ,args);
    		getDB().setTransactionSuccessful();
    	}finally{
    		getDB().endTransaction();
    	}
    }
    
    public void deleteBycids(ArrayList<String> cids)
    {
    	if(cids==null || cids.size()<=0)
    		return ;
		StringBuffer cons = new StringBuffer(" cid " ); 
		StringBuffer buffer = new StringBuffer( " in ( ");
		for(int i =0;i<cids.size();i++){
			buffer.append("?,");			            			
		}          		
		buffer = new StringBuffer(buffer.subSequence(0, buffer.length()-1));
		buffer.append(" ) ");
		delete(cons.append(buffer).toString(),(String[])cids.toArray(new String[cids.size()]));
    }
    
    public List<LImageLib> queryAll() {  
        ArrayList<LImageLib> imgs = new ArrayList<LImageLib>();  
        Cursor c = getDB().rawQuery("SELECT * FROM imglib", null);  
        while (c.moveToNext()) {  
        	LImageLib img = new LImageLib();  
        	img._id = c.getInt(c.getColumnIndex("_id"));  
        	img.cid = c.getInt(c.getColumnIndex("cid"));
        	img.img_url=c.getString(c.getColumnIndex("img_url"));  
            imgs.add(img);  
        }  
        c.close();  
        return imgs;  
    }  
    
    public List<LImageLib> queryWithcons(String cons ,String[] args)
    {
    	ArrayList<LImageLib> images = new ArrayList<LImageLib>();
    	StringBuffer SQL = new StringBuffer(BASE_QUERY_STR);
    	SQL.append(" where ").append(cons);
        Cursor c = getDB().rawQuery(SQL.toString(),args);
        
        while (c.moveToNext()) {  
        	LImageLib img = new LImageLib();  
        	img._id = c.getInt(c.getColumnIndex("_id"));  
        	img.cid = c.getInt(c.getColumnIndex("cid"));
        	img.img_url = c.getString(c.getColumnIndex("img_url"));      
            images.add(img);  
        }  
        c.close();  
        return images; 
    }
    
    public List<String> getImgList(String cid)
    {
    	List<String> images = new ArrayList<String>();
    	StringBuffer SQL = new StringBuffer(BASE_QUERY_STR);
    	SQL.append(" where cid = ? ");
        Cursor c = getDB().rawQuery(SQL.toString(),new String[]{cid});        
        while (c.moveToNext()) {          	  
        	String url = c.getString(c.getColumnIndex("img_url"));      
            images.add(url);  
        }  
        c.close();  
        return images;
    }
}
