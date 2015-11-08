package com.kuibu.model.entity;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public class CollectionBean extends BaseEntity{
	
	@Expose
	public String _id ;
	
	@Expose 	
	public String pid ;
	
	public String tid ;
	public String cid ;
	public String type ;
	public String title ; 
	public String content ; 
	public String cover ; 
	public int height; 
	public int width ; 
	public String createDate ; 
	public String createBy ; 
	public int isPublish ;
	public int isSync ; 
	public boolean isCheck = false ; 
	
	public CollectionBean(){
		
	}

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	
	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public int getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(int isPublish) {
		this.isPublish = isPublish;
	}

	public int getIsSync() {
		return isSync;
	}

	public void setIsSync(int isSync) {
		this.isSync = isSync;
	}

	
	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this){
			return true ; 
		}
		if(o instanceof CollectionBean){
			CollectionBean obj = (CollectionBean)o ;
			if(obj != null){
				return this. _id.equals(obj._id) ; 
			}
		}
		return false ; 
	}
	
	
	
}
