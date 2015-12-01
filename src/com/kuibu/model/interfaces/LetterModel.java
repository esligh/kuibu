package com.kuibu.model.interfaces;

import java.util.Map;

public interface LetterModel {
	
	public void requestSenderList(Map<String,String> params);
	
	public void readLetter(Map<String,String> params);
}
