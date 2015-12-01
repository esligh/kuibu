package com.kuibu.common.utils;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

public class DataUtils {
	private static final int MAX_FORMAT_VAL_K = 999;
	private static final int MAX_FORMAT_VAL_W = 9999;
 	public static String formatNumber(int val) {
		if (val < 0)
			return "0";
		StringBuffer buffer = new StringBuffer();
		if(val > MAX_FORMAT_VAL_W){
			float n = val / 10000.0f;
			DecimalFormat decimalFormat = new DecimalFormat(".#");
			buffer.append(decimalFormat.format(n)).append("W");
		}else if (val > MAX_FORMAT_VAL_K) {
			float n = val / 1000.0f;
			DecimalFormat decimalFormat = new DecimalFormat(".#");
			buffer.append(decimalFormat.format(n)).append("K");
		} else {
			buffer.append(String.valueOf(val));
		}
		return buffer.toString();
	}
 	
 	public static String formatNumber(String val)
 	{
 		return formatNumber(Integer.parseInt(val));		
 	}

	public static JSONArray joinJSONArray(String jsonArrnew, String jsonArrold) {
		StringBuffer buffer = new StringBuffer();
		try {
			if (TextUtils.isEmpty(jsonArrnew) && TextUtils.isEmpty(jsonArrold))
				return null;
			if (TextUtils.isEmpty(jsonArrnew)) {
				return new JSONArray(jsonArrold);
			}
			if (TextUtils.isEmpty(jsonArrold)) {
				return new JSONArray(jsonArrnew);
			}
			buffer.append(jsonArrnew.substring(0, jsonArrnew.length() - 1));
			buffer.append(jsonArrold.substring(1, jsonArrold.length()));
			return new JSONArray(buffer.toString());
		} catch (Exception e) {
		}
		return null;
	}

	public static JSONArray joinJSONArray(JSONArray oldArr, JSONArray newArr,int limit) {
		if((oldArr==null ||oldArr.length()==0) && 
				(newArr==null ||newArr.length()==0))
			return null;
		if(oldArr==null ||oldArr.length()==0) return newArr ;
		if(newArr==null ||newArr.length()==0) return oldArr ; 		
		StringBuffer buffer = new StringBuffer("[");
		try {
			int len = newArr.length();
			for (int i = 0; i < len && i<limit; i++) {
				JSONObject obj = (JSONObject) newArr.get(i);
				if (i == len - 1 || i==limit-1)
					buffer.append(obj.toString());
				else
					buffer.append(obj.toString()).append(",");
			}
			if(limit>len){
				limit = limit-len ; 
				len = oldArr.length();				
				buffer.append(",");
				for (int i = 0; i < len && i<limit; i++) {
					JSONObject obj = (JSONObject) oldArr.get(i);					
					if (i == len - 1 || i == limit-1)
						buffer.append(obj.toString());
					else
						buffer.append(obj.toString()).append(",");
				}
			}			
			buffer.append("]");
			return new JSONArray(buffer.toString());
		} catch (Exception e) {
		}
		return null;
	}
}
