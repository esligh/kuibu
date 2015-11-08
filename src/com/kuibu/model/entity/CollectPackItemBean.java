package com.kuibu.model.bean;

@SuppressWarnings("serial")
public class CollectPackItemBean extends BaseEntity{
	private String id ; 
	private String packType ; 
	private String packName;
	private String packDesc;
	private String collectCount ; 
	private String followCount ; 
	private String createBy; 
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPackType() {
		return packType;
	}
	public void setPackType(String packType) {
		this.packType = packType;
	}
	public String getPackDesc() {
		return packDesc;
	}
	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	public String getCollectCount() {
		return collectCount;
	}
	public void setCollectCount(String collectCount) {
		this.collectCount = collectCount;
	}
	public String getFollowCount() {
		return followCount;
	}
	public void setFollowCount(String followCount) {
		this.followCount = followCount;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	
}
