package com.sogogi.landroid.model;

public class LauncherIcon {
	private String text;
	private int imgId;
	private String map;

	public LauncherIcon(int imgId, String text, String map) {
		this.imgId = imgId;
		this.text = text;
		this.map = map;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}
}
