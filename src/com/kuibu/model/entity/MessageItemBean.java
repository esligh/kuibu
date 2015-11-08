package com.kuibu.model.entity;

@SuppressWarnings("serial")
public class MessageItemBean extends BaseEntity{

	private String msgId;
	private String message;
	private String creatorPic; 
	private String creatorName ; 
	private String createTime ;
	
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getCreateTime() {
		return createTime;
	}
	
	public String getCreatorPic() {
		return creatorPic;
	}
	public void setCreatorPic(String creatorPic) {
		this.creatorPic = creatorPic;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	} 
}
