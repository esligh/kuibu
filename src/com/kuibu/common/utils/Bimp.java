package com.kuibu.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bimp
{
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();
	public static List<String> drr = new ArrayList<String>();
	public static void clear()
	{
		max=0;
		for(int i=0;i<bmp.size();i++){
			Bitmap bitmap = bmp.get(i);
			if(bitmap!=null && !bitmap.isRecycled()){
				bitmap.recycle();
			}
		}
		bmp.clear();
		drr.clear();
	}
	
	public static Bitmap revitionImageSize(String path) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true)
		{
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000))
			{
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}
