package com.kuibu.model.entity;

@SuppressWarnings("serial")
public class TopicItemBean extends BaseEntity{
	
	String id ; 
	String topic ; 
	String introduce ; 
	String topicPicUrl;
	String focusCount ; 
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
	public String getTopicPicUrl() {
		return topicPicUrl;
	}
	public void setTopicPicUrl(String topicPicUrl) {
		this.topicPicUrl = topicPicUrl;
	}
	public String getFocusCount() {
		return focusCount;
	}
	public void setFocusCount(String focusCount) {
		this.focusCount = focusCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		 if (o instanceof TopicItemBean) {   
			 return this.id.equals(((TopicItemBean) o).getId());
		 }
		return super.equals(o);
	}
	
}
