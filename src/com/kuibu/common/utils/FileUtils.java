package com.kuibu.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

/**
 * @class 文件工具类
 * @author ThinkPad
 *
 */
public class FileUtils
{
	public static void saveBitmap(String path, String picName,Bitmap bm)
	{
		try{
			File f = new File(path, picName + ".JPEG");
			if (f.exists()){
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static boolean isFileExist(String pathName)
	{
		File file = new File(pathName);
		file.isFile();
		return file.exists();
	}

	public static void delFile(String pathName)
	{
		File file = new File(pathName);
		if (file.isFile()){
			file.delete();
		}
		file.exists();
	}

	public static boolean fileIsExists(String path)
	{
		try{
			File f = new File(path);
			if (!f.exists()){
				return false;
			}
		} catch (Exception e){
			return false;
		}
		return true;
	}
	
}
