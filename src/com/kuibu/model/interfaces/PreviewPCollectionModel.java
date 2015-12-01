package com.kuibu.model.interfaces;

import java.util.Map;

import net.tsz.afinal.http.AjaxParams;

public interface PreviewPCollectionModel {

	public void upLoadImgs(AjaxParams params,boolean bfirst);
	
	public void doPublish(Map<String,Object> params);

	
}
