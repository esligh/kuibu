package com.kuibu.model.bean;

import android.graphics.drawable.Drawable;

@SuppressWarnings("serial")
public class DrawerListItem extends BaseEntity{	
	private Drawable icon;
	private String title;
	private String tag ; 	
	
	public DrawerListItem() {
		
	}	
	
	public DrawerListItem(Drawable icon, String title,String tag) {
		this.icon = icon;
		this.title = title;
		this.tag  = tag; 
	}
	
	public Drawable getIcon() {
		return icon;
	}
	public String getTitle() {
		return title;
	}
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public void setTitle(String title) {
		this.title = title;
	}	
}
