package com.smit.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RSSReceiver extends BroadcastReceiver {

	private CustomerDialogListener listener ;
	private Context context;
	private RSSOpenHelper rssOpenHelper;
	
		public RSSReceiver(CustomerDialogListener lis,Context context, RSSOpenHelper help){
		this.listener = lis;
		this.context = context;
		this.rssOpenHelper = help; 
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(RssReaderConstant.READED_BROADCAST)){
			listener.onOkClick();
		}else if(action.equals(RssReaderConstant.IQRESPONSEYES)){
			String message = intent.getExtras().getString("FEEDURL");
			String subFlag = intent.getExtras().getString("SUBUNSUB");
			if(subFlag.equals("unsub")){
				new PopupDialog("��ܰ��ʾ��","�Ѿ�ȡ����Ƶ���Ķ��ģ�",context).show();
				rssOpenHelper.deleteChannel(message);
				listener.onOkClick();
			}
		}else if(action.equals("com.smit.rssreader.action.IQ_NO_BROADCAST")){
			String message = intent.getExtras().getString("FEEDURL");
			String subFlag = intent.getExtras().getString("SUBUNSUB");
			if(subFlag.equals("unsub")){
				new PopupDialog("��ܰ��ʾ��","ȡ����Ƶ���Ķ��Ĳ��ɹ���",context).show();
			}
		}
	}

}
