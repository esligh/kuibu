package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.CommentItemBean;

public interface CommentPresenter {

	public void delComment(CommentItemBean item,int position);
	
	public void getCommentList();
	
	public void addComment();
	
	public int getCommentCount();
	
	public void setCurrentItem(CommentItemBean item);
	
	public CommentItemBean getCurrentItem();
	
	public void setCurretPos(int position);
	
	public int getCurrentPos();
}
