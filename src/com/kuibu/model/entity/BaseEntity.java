package com.kuibu.model.entity;

import java.io.Serializable;


/**
 * @author
 * class BaseBean 
 */

@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {		
	
	protected String mCacheKey;
	
	public String getCacheKey() {
		return mCacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.mCacheKey = cacheKey;
	}
	
}
