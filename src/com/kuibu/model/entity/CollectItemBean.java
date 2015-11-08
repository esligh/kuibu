package com.kuibu.model.bean;

@SuppressWarnings("serial")
public class CollectItemBean extends BaseEntity{
	public String topic; 
	public String id ;
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	} 
	
}
