package com.kuibu.model.bean;

@SuppressWarnings("serial")
public class FocusCollectItemBean extends BaseEntity{
	private String collect_package;
	private String sub_desc ;
	public String getCollect_package() {
		return collect_package;
	}
	public void setCollect_package(String collect_package) {
		this.collect_package = collect_package;
	}
	public String getSub_desc() {
		return sub_desc;
	}
	public void setSub_desc(String sub_desc) {
		this.sub_desc = sub_desc;
	}	
}
