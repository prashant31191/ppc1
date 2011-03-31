package com.openims.view;

import com.openims.utility.Constants;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.setting.Setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PushServiceReceiver extends BroadcastReceiver{
	
	private static final String LOGTAG = LogUtil.makeLogTag(PushServiceReceiver.class);
    private static final String tag = PushServiceReceiver.class.getSimpleName()+"--";
    
	public PushServiceReceiver(){
		
	}
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		Log.d(LOGTAG,tag+"onReceiver");
		
		if(intent.getAction().equals(PushServiceUtil.ACTION_REGISTRATION)){			
			handleRegistration(context, intent);
		}else if(intent.getAction().equals(PushServiceUtil.ACTION_RECEIVE)){
			handleMessage(context, intent);
		}else if(intent.getAction().equals(PushServiceUtil.ACTION_STATUS)){
			handleStatus(context, intent);
		}else{
			Log.e(LOGTAG,tag+"receiver error type");
		}
	}
	/**
	 * ����ע�����עPUSH�ķ�����Ϣ
	 * @param context
	 * @param intent
	 */
	private void handleRegistration(Context context, Intent intent) {
		
		Log.d(LOGTAG,tag+"handleRegistration");
		
	    String pushId = intent.getStringExtra(PushServiceUtil.PUSH_ID); 
	    String pustStatus = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
	   
	    Log.d(LOGTAG,tag+"Registration succuss and Id = " + pushId);
	    Log.d(LOGTAG,tag+"Registration status = " + pustStatus);
	    
	    // ͨ�����µķ�ʽ������Ϣ��activity
	    Intent intent1 = new Intent();
        intent1.setClassName("com.openims","com.openims.onlineHelper.ChatActivity");
        intent1.putExtra(PushServiceUtil.MESSAGE_FROM, "dd");
        intent1.putExtra(PushServiceUtil.MESSAGE_CONTENT, "dd");
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // ����Ҫ
        //context.startActivity(intent1);
	}
	/**
	 * ����push��������Ϣ
	 * @param context
	 * @param intent
	 */
	private void handleMessage(Context context, Intent intent) {
		
		Log.d(LOGTAG,tag+"handleMessage");
	}
	/**
	 * ��������״̬�ķ�����Ϣ
	 * @param context
	 * @param intent
	 */
	private void handleStatus(Context context, Intent intent) {
		String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
	    
		// ͨ�����µķ�ʽ������Ϣ��activity
	    /*Intent intent1 = new Intent();
        intent1.setClassName("com.openims","com.openims.onlineHelper.ChatActivity");
        intent1.putExtra(PushServiceUtil.PUSH_STATUS, status);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // ����Ҫ
        context.startActivity(intent1);*/
		Intent intentBroadcast = new Intent(Setting.InnerReceiver.ACTION);
		intentBroadcast.putExtra(PushServiceUtil.PUSH_STATUS, status);
		context.sendBroadcast(intentBroadcast);
	}
}