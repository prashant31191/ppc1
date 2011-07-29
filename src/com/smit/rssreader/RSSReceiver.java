package com.smit.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RSSReceiver extends BroadcastReceiver {

	private CustomerDialogListener listener ;
	
	public RSSReceiver(CustomerDialogListener lis){
		this.listener = lis;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals("com.smit.rssreader.action.READED_BROADCAST")){
			listener.onOkClick();
		}
	}

}
