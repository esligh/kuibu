package com.kuibu.model.bean;

@SuppressWarnings("serial")
public class LImageLib extends BaseEntity{
	public int _id ; 
	public int cid ; 
	public String img_url ; 
	
	public LImageLib()
	{}

	public LImageLib(int _id ,int cid,String img_url)	
	{
		this._id = _id ; 
		this.cid = cid ; 
		this.img_url = img_url ; 
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getImg_url() {
		return "file://"+img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	
}
