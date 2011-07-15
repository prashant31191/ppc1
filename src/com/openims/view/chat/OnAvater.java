package com.openims.view.chat;

import android.graphics.drawable.Drawable;

public interface OnAvater {
	
	public Drawable getAvater(String avaterJid, OnAvaterListener listener);
	
	public interface OnAvaterListener {
		public void avater(String avaterJid, Drawable avater);
	}

}
