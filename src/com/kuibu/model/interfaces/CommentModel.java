package com.kuibu.model.interfaces;

import java.util.Map;

public interface CommentModel {
	
	public void requestAdd(Map<String,String> params);
	
	public void requestDel(Map<String,String> params);
	
	public void requestCommentList(Map<String,String> params);
}
