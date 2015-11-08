package com.kuibu.model.entity;


/**
 * @author ThinkPad
 * @category local model 
 * map to local table collectpack .
 */

@SuppressWarnings("serial")
public class CollectPackBean extends BaseEntity{

	public String _id ; 
	public String pack_type ; 
	public String pack_id ; //server side 
	public String pack_name ; 
	public String pack_desc ;
	public String topic_id ; 
	public String collect_count ; 
	public String focus_count ; 
	public String create_by ; 
	public int is_private ; 
	public int is_sync ; 
	
	public CollectPackBean()
	{}
	public CollectPackBean(String pack_name,String pack_desc,String count)
	{
		this.pack_name = pack_name ; 
		this.pack_desc = pack_desc ; 
		this.focus_count = count ;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
	public String getPack_id() {
		return pack_id;
	}
	public void setPack_id(String pack_id) {
		this.pack_id = pack_id;
	}
	public String getPack_name() {
		return pack_name;
	}
	public void setPack_name(String pack_name) {
		this.pack_name = pack_name;
	}
	
	public String getPack_type() {
		return pack_type;
	}
	public void setPack_type(String pack_type) {
		this.pack_type = pack_type;
	}
	public String getPack_desc() {
		return pack_desc;
	}
	public void setPack_desc(String pack_desc) {
		this.pack_desc = pack_desc;
	}
	
	public String getCollect_count() {
		return collect_count;
	}
	public void setCollect_count(String collect_count) {
		this.collect_count = collect_count;
	}
	public String getFocus_count() {
		return focus_count;
	}
	public void setFocus_count(String focus_count) {
		this.focus_count = focus_count;
	}
	public String getCreate_by() {
		return create_by;
	}
	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}
	public int get_private() {
		return is_private;
	}
	public void set_private(int is_private) {
		this.is_private = is_private;
	}
	
	public int get_sync()
	{
		return is_sync ; 
	}
	
	public void set_sync(int sync)
	{
		this.is_sync = sync ; 
	}
	
	public String getTopic_id() {
		return topic_id;
	}
	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}	
	
}
