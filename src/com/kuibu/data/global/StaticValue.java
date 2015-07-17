package com.kuibu.data.global;

public class StaticValue {
	public static final String CHOOSE_PIC_OVER = "com.caddy.pickpic_complete";
	public static final String CHOOSE_PIC_CANCEL = "com.caddy.pickpic_cancel";
	public static final String USER_INFO_UPDATE = "com.caddy.update_userinfo" ; 
	public static final int TAKE_PHOTO_OK = 0x000012;
	public static final String MAINWITHDLG =  "main_with_dlg";
	public static final String HIDE_FOCUS = "hide_focus";

	public static class CommonData{
		public static final int TAKE_USER_PHOTO  = 10000 ; 
		public static final int TAKE_PIC_CROP_RESULT = 10001 ; 
		public static final int CAMERA_TAKE_PICTRUE_RESULT = 0X000001;  
	}
	
	public static class PrefKey{
		public static final String NO_PICTRUE_KEY = "no_pictrue";
		public static final String DARK_THEME_KEY = "dark_theme";
		public static final String ABOUT_ME = "about_me";
		public static final String LASTEST_VERSION = "latest_version";
		public static final String CLEAR_BUFFER = "clear_buffer";
		public static final String ACCOUNT_PROTECT = "account_protect";
	}
	
	public static class USER_ACTION{
		public static final String ACTION_VOTE_COLLECTION =  "1000";
		public static final String ACTION_COLLECT_COLLECTION  = "1100";
		public static final String ACTION_COMMENT_COLLECTION  = "1200"; 
		public static final String ACTION_OPPOSE_COLLECTION = "1300";
		public static final String ACTION_FOCUS_COLLECTION = "1400";
		public static final String ACTION_FOCUS_TOPIC = "2000";
		public static final String ACTION_FOCUS_COLLECTOR = "3000";
	}
	
	public static class RequestCode{
		public static final int  USER_INFO_RESULT_CODE = 1;
		public static final int REQ_CODE_SETTING = 2001; 
		public static final int FAVORITE_BOX_REQCODE = 8001;
	}
	public static class MENU_GROUP{
		public static final int SAVE_ACTIONBAR_GROUP = 100;
		public static final int CANCEL_ACTIONBAR_GROUP = 101;	
		public static final int IAMGE_SCAN =  102 ; 
	}
	
	public static class MENU_ITEM{
		public static final int SAVE_ID = 200; 
		public static final int CANCEL_ID = 201; 	
		public static final int EDIT_ID = 202 ; 		
		public static final int SHARE_ID = 203 ;
		public static final int SEND_ID = 204 ; 
		public static final int CLEAR_ID = 205 ; 
	}
	
	public static class MENU_ORDER{
		public static final int SAVE_ORDER_ID = 1; 
		public static final int CANCEL_ORDER_ID = 2; 
	}
	
	public static class IMAGE_SCAN{
		public static final int IMAGE_TEXT_MAX =  8;
		public static final int IMAGE_MAX = 100; 
	}
	
	public static class TAG_VLAUE
	{
		public static final String ARG_SECTION_TAG = "section_tag";
	}
	
	public static class CONTENT_ACTIVITY_INFO{
		public static final String ARG_CONTENT_TYPE="CONTENT_TYPE";
		public enum CONTENT_TYPE{
			TEXT,TEXT_WITH_PIC,PICS
		}	
	}	
		
	public static class EDITOR_VALUE
	{
		public static final String FROM_WHO="preview_from_who";
		public static final int LIST_TO_PREVIEW = 1;
		public static final int EDITOR_TO_PREVIEW = 2;		
		
		public static final String COLLECT_PACK_ID = "collect_pack_id";
		public static final String COLLECT_PACK_NAME= "collect_pack_name";
		public static final String COLLECTION_ID  = "collection_id";
		public static final String COLLECTION_COUNT = "collection_count";
		public static final String COLLECTION_ENTITY = "collection_entity";
		
		public static final String COLLECTION_TEXT  = "0" ;  //文字
		public static final String COLLECTION_TEXTIMAGE  = "1" ;//图文
		public static final String COLLECTION_IMAGE  = "2" ;//图片
	}
	
	public static class RESPONSE_STATUS 
	{
		//server response status 
		//server response status  
		public static final String OPER_SUCCESS = "1000";
		public static final String OPER_EXCEPTION = "2000";
		public static final String OPER_INVALID = "-1" ; 

		//register STATUS 
		public static final String REG_SUCCESS = "1101";
		public static final String REG_WAITACTIVITY = "1102";
		public static final String REG_INVALIDEMAIL = "1110";
		public static final String REG_SENDEMAIL = "1103";
		
		//login status 
		public static final String LOGIN_SUCCESS  = "610";
		public static final String LOGIN_NFUSER  = "621";
		public static final String LOGIN_PWDWRONG = "622";
		
		//collection status
		public static final String COLLETION_NFRECORD = "1200"; 
		public static final String DEL_COLLECTION_DENIESS = "1210";
		public static final String DEL_COLLECTION_SUCCESS = "1201";
	
		//upload status  
		public static final String UPLOAD_SUCCESS = "1400";
		public static final String UPLOAD_FAILD = "1410";
		public static final String FILE_TOOLARGE = "1414";
		public static final String FILE_NOPEMITTION = "1415";
	}
	
	public static class REGSTATE
	{
		public static final String ACCOUNT_ACTIVATED = "1";
		public static final String ACCOUNT_WAITACTIVATE = "0";
	}
	
	public static class SERMODLE
	{
		public static final String COLLECTOR_TYPE = "001";
		public static final String TOPIC_TYPE = "002";
		public static final String COLLECTION_TYPE = "003";
		public static final String FAVORITE_TYPE = "004";		
		
		public static final String COLLECTION_ID = "collection_id";
		public static final String BOX_ID = "box_id";
		public static final String BOX_NAME = "box_name";
		public static final String BOX_DESC = "box_desc";
		public static final String BOX_COUNT = "box_count";
		public static final String BOX_FOCUS_COUNT = "box_focus_count";
		
		public static final String FOCUS_TARGET_COLLECTOR= "COLLECTOR";
		public static final String FOCUS_TARGET_COLLECTPACK= "COLLECTPACK";
		public static final String FOCUS_TARGET_TOPIC= "TOPIC";
		
		public static final String USER_SEX_FEMALE = "W";
		public static final String USER_SEX_MALE = "M";
		
		
		public static final String VERSION_CODE = "version_code";
	}
	
	public static class USERINFO
	{
		public static final String SHOWLAYOUT = "showlayout";
		public static final String USERINFOENTITY = "user_info_entity";
		public static final String USER_ISFOLLOW = "user_idfollow";
		public static final String USER_NAME = "user_name";
		public static final String USER_SEX = "user_sex";
		public static final String USER_SIGNATURE = "user_signature";
		public static final String USER_PHOTO = "user_photo";
		public static final String USER_ID = "user_id";
	}
	
	public static class TOPICINFO
	{
		public static final String TOPIC_ID = "topic_id";
		public static final String TOPIC_NAME = "topic_name";
		public static final String TOPIC_EXTRA = "topic_extra";
		public static final String TOPIC_PIC = "topic_pic";
		public static final String TOPICINFOENTITY = "topic_info_entity";
	}
	
	public static class COLLECTION
	{
		public static final String IS_COLLECTED = "is_collected";
	}
	
	public static class COLLECTPACK 
	{
		public static final String PACK_ID = "pack_id";
		public static final String PACK_NAME = "pack_name";
		public static final String PACK_DESC = "pack_desc";
		public static final String IS_PRIVATE = "is_private";
	}
	
	public static class COMMENT
	{
		public static final String TYPE_REPLY = "20";
		public static final String TYPE_COMMON = "10";
	}
		
	public static class LOCALCACHE
	{
		public static final int   DEFAULT_CACHE_SIZE = 20 ;  
		public static final String HOME_LIST_CACHE = "home_list_cache";
		public static final String HOME_RANK_CACHE = "home_rank_cache";
		public static final String HOME_HOT_CACHE = "home_hot_cache";
		public static final String HOME_RECOMMAND_CACHE = "home_recommand_cache";
		public static final String FAVORITE_BOX_CACHE = "favorite_box_cache";
		public static final String FOCUS_COLLECTPACK_CACHE = "focus_collectpack_cache";
		public static final String FOCUS_TOPIC_CACHE = "focus_topic_cache";

	}
	
	public static class TAB_PAGE
	{
		public static final String TAB_PAGE_RANK= "rank";
		public static final String TAB_PAGE_HOT= "hot";
		public static final String TAB_PAGE_RECOMMEND= "recommend";
		
		public static final String TAB_PAGE_FOCUS_TOPIC = "focus_topic";
		public static final String TAB_PAGE_FOCUS_COLLECT = "focus_collect";
		
		public static final String TAB_PAGE_COMMENT = "comment";
		public static final String TAB_PAGE_MESSAGE = "message";
		
	}
	
	public static class EVENT{
		public static final String LOGIN_EVENT = "login";
		public static final String TYPE_NEWLETTERS= "NEW_LETTER";
		public static final String TYPE_NEWCOMMETN="NEW_COMMENT";
	}
}
