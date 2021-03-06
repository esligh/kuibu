package com.kuibu.model.entity;

/**
 * server bean 
 * base model : collection */

public class CollectionItemBean extends BaseEntity{

	private static final long serialVersionUID = 1L;
	private String id ;
	private String cisn ; 
	private String pid ; 
	private String tid ; 
	private String type ;
	private String title; 
	private String content ;
	private String cover ; 
	private String summary ; 
	private String createDate ;
	private String createBy ; 
	private String shareCount ;
	private String visitCount ; 
	private String voteCount ;
	private String commentCount ; 
	private String creatorName ;
	private String creatorSex ; 
	private String creatorPic; 
	private String creatorSignature; 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getCisn() {
		return cisn;
	}
	public void setCisn(String cisn) {
		this.cisn = cisn;
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
		
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
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
	
	
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
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
	public String getShareCount() {
		return shareCount;
	}
	public void setShareCount(String shareCount) {
		this.shareCount = shareCount;
	}
	public String getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(String visitCount) {
		this.visitCount = visitCount;
	}
	public String getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(String voteCount) {
		this.voteCount = voteCount;
	}
	public String getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getCreatorPic() {
		return creatorPic;
	}
	public void setCreatorPic(String creatorPic) {
		this.creatorPic = creatorPic;
	}
	public String getCreatorSignature() {
		return creatorSignature;
	}
	public void setCreatorSignature(String creatorSignature) {
		this.creatorSignature = creatorSignature;
	}
	public String getCreatorSex() {
		return creatorSex;
	}
	public void setCreatorSex(String creatorSex) {
		this.creatorSex = creatorSex;
	} 
	
	
}
