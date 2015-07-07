package com.kuibu.data.global;


/**
 * @class 常量
 * @author ThinkPad
 */
public final class Constants {
	
	public final static String VERISION_CODE =  "version_code";
	
	public static class Config {
		public static final boolean DEBUG_MODE = true ; 
		public static final String USER_PHOTO_TMP  = "pictmp";//用户头像裁剪的缓存文件
		public static final String CAMERA_TMP="userphoto.jpg";
		public static final String CAMERA_IMG_DIR = "/kuibu/tmp/";
	}
	
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
    public static final String[] KEYBOARD_SHORTCUTS = {"*", "-", "_", "#", "!", ":"};
    public static final String[] KEYBOARD_SHORTCUTS_BRACKETS = { "(", ")", "[", "]"};
    public static final String[] KEYBOARD_SMART_SHORTCUTS = {"()", "[]"};

    public static final int MAX_TITLE_LENGTH = 20;
    public static final String URI_PREFIX = "file://";
    public static final String WEBVIEW_DARK_CSSFILE= "file:///android_asset/markdown_css_themes/dark.css";
    public static final String WEBVIEW_LIGHT_CSSFILE= "file:///android_asset/markdown_css_themes/classic.css";
    
    public static final String GITGUB_PROJECT = "https://github.com/esligh/kuibu";
}
