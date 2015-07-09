package com.kuibu.model.bean;

import java.io.Serializable;


/**主要的列表项数据定义
 * 如首页列表，本月热榜等
 * */

/**
 * @author ThinkPad
 *
 */
public class MateListItem implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int ITEM_TYPE_COUNT = 3; 
	public static class ItemType{
		public static final int TEXT_MODE=0,TEXT_PICS_MODE=1,PICS_MODE=2 ; 
	}
	public static int getTypeCount(){return ITEM_TYPE_COUNT; }
	
	private String 		_id  ; //local model internal id 
	private int 		type ; //item type 
	private String 		topId; 
	private String 		topText ;
	private String 	 	topUrl ; 
	private String  	title;
	private String  	summary ;
	private String 		itemPic; //if have   
	
	private int 		commentCount;
	private int 		voteCount;
	private int 		shareCount ; 
	private String 		packId; 
	private String      topicId; 
	private String 		createBy; 
	private String 		userSex; 
	private String   	userSignature; 
	private String 		lastModify; 
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTopId() {
		return topId;
	}
	public void setTopId(String topId) {
		this.topId = topId;
	}
	public String getTopText() {
		return topText;
	}
	public void setTopText(String topText) {
		this.topText = topText;
	}
	public String getTopUrl() {
		return topUrl;
	}
	public void setTopUrl(String topUrl) {
		this.topUrl = topUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getItemPic() {
		return itemPic;
	}
	public void setItemPic(String itemPic) {
		this.itemPic = itemPic;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}
	
	public int getShareCount() {
		return shareCount;
	}
	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}
	public String getPackId() {
		return packId;
	}
	public void setPackId(String packId) {
		this.packId = packId;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public String getUserSignature() {
		return userSignature;
	}
	public void setUserSignature(String userSignature) {
		this.userSignature = userSignature;
	}
	public String getLastModify() {
		return lastModify;
	}
	public void setLastModify(String lastModify) {
		this.lastModify = lastModify;
	}		
	
	
}
