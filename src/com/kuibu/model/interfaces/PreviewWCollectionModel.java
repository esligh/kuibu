package com.kuibu.model.interfaces;

import java.util.Map;

import net.tsz.afinal.http.AjaxParams;

public interface PreviewWCollectionModel {

	public void requestImgList(Map<String,String> params);
	
	public void requestDelImgs(Map<String,String> params);
	
	
	public void requestUpdateImg(Map<String,Object> params);
	
	public void upLoadImgs(AjaxParams params,boolean bfirst);
	
	public void doPublish(Map<String,Object> params);

}
