package com.kuibu.model.bean;

public class ImageBean {
	private String topImagePath ;  //文件夹第一张图路径
	private String folderName;  //文件夹
	private int imageCounts ; //文件夹内的图片数
	public String getTopImagePath() {
		return topImagePath;
	}
	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getImageCounts() {
		return imageCounts;
	}
	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	} 

}
