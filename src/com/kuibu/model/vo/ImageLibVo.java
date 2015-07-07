package com.kuibu.model.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.kuibu.model.bean.LImageLib;
import com.kuibu.model.db.SqLiteManager;

public class ImageLibVo extends SqLiteManager{
	public static final String BASE_QUERY_STR = "select * from imglib ";
	public ImageLibVo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
    @SuppressLint("SimpleDateFormat")
	public void add(List<LImageLib> imgs) {  
        db.beginTransaction();   
        try {  
            for (LImageLib img : imgs) {  
                db.execSQL("INSERT INTO imglib VALUES(null, ?, ?, ?)", 
                		new Object[]{img.cid, img.img_url, 
                		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});  
            }  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();    
        }  
    }  
    
    @SuppressLint("SimpleDateFormat")
	public void add(String cid , List<String> urls)
    {
    	db.beginTransaction();   
        try {  
            for (String url : urls) {  
                db.execSQL("INSERT INTO imglib VALUES(null, ?, ?, ?)", 
                		new Object[]{cid, url, 
                		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});  
            }  
            db.setTransactionSuccessful();   
        } finally {  
            db.endTransaction();      
        }  
    }
      
    public void delete(String cid) {  
        db.delete("imglib", "cid = ?", new String[]{String.valueOf(cid)});  
    }  
      
    public void delete(String cons , String[] args)
    {    	
    	db.beginTransaction();
    	try{
    		db.execSQL("delete from imglib where " + cons ,args);
        	db.setTransactionSuccessful();
    	}finally{
    		db.endTransaction();
    	}
    }
    
    public List<LImageLib> queryAll() {  
        ArrayList<LImageLib> imgs = new ArrayList<LImageLib>();  
        Cursor c = db.rawQuery("SELECT * FROM imglib", null);  
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
        Cursor c = db.rawQuery(SQL.toString(),args);
        
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
        Cursor c = db.rawQuery(SQL.toString(),new String[]{cid});        
        while (c.moveToNext()) {          	  
        	String url = c.getString(c.getColumnIndex("img_url"));      
            images.add(url);  
        }  
        c.close();  
        return images;
    }
}
