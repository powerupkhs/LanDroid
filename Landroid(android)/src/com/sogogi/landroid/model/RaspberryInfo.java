package com.sogogi.landroid.model;

public class RaspberryInfo {
	private int icon;
	private String name;
	private String description;
	private int progressBarProgress;

	public RaspberryInfo() {

	}

	public RaspberryInfo(int Icon, String Name, String Description, int ProgressBarProgress) {
		super();
		this.icon = Icon;
		this.name = Name;
		this.description = Description;
		this.progressBarProgress = ProgressBarProgress;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getProgressBarProgress() {
		return progressBarProgress;
	}

	public void setProgressBarProgress(int progressBarProgress) {
		this.progressBarProgress = progressBarProgress;
	}
}