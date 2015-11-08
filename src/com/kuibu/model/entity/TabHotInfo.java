package com.kuibu.model.entity;

@SuppressWarnings("serial")
public class TabHotInfo extends BaseEntity{
	
	private String title = null;
	private String Tag = null;
	private String comment = null;
	
	public TabHotInfo(String title,String tag,String comment)
	{
		this.title = title ; 
		this.Tag = tag; 
		this.comment = comment; 
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTag() {
		return Tag;
	}
	public void setTag(String tag) {
		Tag = tag;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
		
}
