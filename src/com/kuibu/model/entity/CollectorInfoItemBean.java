package com.kuibu.model.entity;

@SuppressWarnings("serial")
public class CollectorInfoItemBean extends BaseEntity{
	
	private String name ;
	private String introduce ;
	private String pic;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	
	
	
}
