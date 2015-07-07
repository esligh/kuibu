package com.kuibu.model.bean;

public class TopicInfoBean {
	private String topicId ;
	private String topicName; 
	private String topicDesc ; 
	private byte[] topicPic;
	
	
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getTopicDesc() {
		return topicDesc;
	}
	public void setTopicDesc(String topicDesc) {
		this.topicDesc = topicDesc;
	}
	public byte[] getTopicPic() {
		return topicPic;
	}
	public void setTopicPic(byte[] topicPic) {
		this.topicPic = topicPic;
	}
	
}
