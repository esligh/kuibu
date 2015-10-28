package com.kuibu.data.global;

import android.graphics.Bitmap;

import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;


/**
 * @class 常量
 * @author ThinkPad
 */
public final class Constants {
	
	public final static String VERISION_CODE =  "version_code";
	public final static String APP_NAME = "kuibu";
	public static final String TEMPLATE_DEF_URL = "html/template.html";

	public static class Config {
//	/	public static final String SERVER_URI = "http://115.28.200.109:80/";
	//	public static final String SOCKETIO_SERVER = "http://115.28.200.109:80/socket.io";
		public static final String SERVER_URI = "http://192.168.1.6:5000/";
		public static final String SOCKETIO_SERVER = "http://192.168.1.6:5000/";
		public static final String REST_API_VERSION = "/caddy/api/v1.0";	
		public static final boolean DEBUG_MODE = true ; 
		public static final String USER_PHOTO_TMP  = "pictmp";//用户头像裁剪的缓存文件
		public static final String CAMERA_TMP="userphoto.jpg";
		public static final String CAMERA_IMG_DIR = "/kuibu/";
		public static final String CRASH_DIR = "/crash/" ;
		public static final String SAVE_IMG_DIR = "/image/";
		public static final int 	TIME_OUT_LONG = 12*1000; //12s
		public static final int 	TIME_OUT_SHORT = 8*1000; 
		public static final int 	RETRY_TIMES =   1; 
		public static final String USER_PIC_SMALL = "40_40";
		public static final String USER_PIC_BIG ="100_100";
		public static final String IMG_COMPRESS_PATH="/compress/"; 
		public static final String WEBVIEW_IMG_CACHE_DIR = "webview_tmp";
		public static final int CACHE_SAVE_TIME = 3*24*60*60;
		public static final int MAX_IMAGE_SELECT = 9 ;
		public static final int PASSWORD_MIN_LEN = 6 ; 
	}
	
	public static final int COMPRESS_WIDTH = 640 ; 
	public static final int COMPRESS_HEIGHT = 480 ; 
	public static final String THRESHOLD_INVALID = "0";
	public static final long LEAVE_PRESS_DELAY = 2000 ; 
	
	public static class Tag{
		public static final String LOGIN = "login";
		public static final String REGISTER = "register";
		public static final String SETTING = "setting";
		public static final String COLLECT = "collect";
		public static final String DRAFT = "draft";
		public static final String FOCUS ="focus";
		public static final String HOME = "home";
		public static final String EXPLORE  ="explore";
	}
	
	public static class Extra {
		public static final String FRAGMENT_INDEX = "com.nostra13.universalimageloader.FRAGMENT_INDEX";
		public static final String IMAGE_POSITION = "com.nostra13.universalimageloader.IMAGE_POSITION";
	}
	
	/*markdown 的快捷键盘*/
    public static final String[] KEYBOARD_SHORTCUTS = {"#","*", ">", "!", ":"};
    public static final String[] KEYBOARD_SHORTCUTS_BRACKETS = { "(", ")", "[", "]"};
    public static final String[] KEYBOARD_SMART_SHORTCUTS = {"( )", "[ ]"};
    public static final int KEYBOARD_DEFAULT_SIZE = 18 ; 
    public static final int MAX_TITLE_LENGTH = 20;
    public static final String URI_PREFIX = "file://";
    public static final String WEBVIEW_DARK_CSSFILE= "file:///android_asset/markdown_css_themes/dark.css";
    public static final String WEBVIEW_LIGHT_CSSFILE= "file:///android_asset/markdown_css_themes/classic.css";    
    public static final String GITGUB_PROJECT = "https://github.com/esligh/kuibu";
    
}
