package com.openims.model;

public class SharedData {
	
	private static SharedData sharedData = null;
	
	private boolean showNewMessageNotify = true;
	private SharedData(){};
	
	public static synchronized SharedData getInstance(){
		if(sharedData == null){
			sharedData = new SharedData();
		}
		return sharedData;
	}

	public boolean isShowNewMessageNotify() {
		return showNewMessageNotify;
	}

	public void setShowNewMessageNotify(boolean showNewMessageNotify) {
		this.showNewMessageNotify = showNewMessageNotify;
	}
	
}
