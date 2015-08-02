package com.kuibu.model.vo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kuibu.data.global.KuibuApplication;

public class BaseDbVo {

	private Context  mContext ; 
	
	public BaseDbVo(Context context ) {
		mContext =context; 
	}
		
	protected SQLiteDatabase getDB()
	{
		return KuibuApplication.getSqLiteHelper(mContext).getWritableDatabase();
	}

	public void closeDB()
	{
		KuibuApplication.getSqLiteHelper(mContext).getWritableDatabase().close();
	}
}
