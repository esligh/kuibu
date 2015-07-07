package com.kuibu.common.utils;

import java.text.DecimalFormat;

public class DataFormatUtil {
	private static final int MAX_FORMAT_VAL = 999 ; 
	public static String formatNumber(int val)
	{
		if(val<0)
			return "0";
		StringBuffer buffer = new StringBuffer();
		if(val>MAX_FORMAT_VAL){
			float n = val/1000.0f;
			DecimalFormat decimalFormat = new DecimalFormat(".#");
			buffer.append(decimalFormat.format(n)).append("K");
		}else{
			buffer.append(String.valueOf(val));
		}
		return buffer.toString();	
	}
}
