package com.openims.view.setting;

import com.smit.EasyLauncher.R;
import com.openims.service.IMService;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Setting extends Activity {
    /** Called when the activity is first created. */
	private static final String LOGTAG = LogUtil.makeLogTag(Setting.class);
	private static final String TAG = LogUtil.makeTag(Setting.class);
	
	private String connectState = "Î´µÇÂ¼";
	private BroadcastReceiver receiver;
	private TextView infView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicesetting);
        receiver = new InnerReceiver();
        
        infView = (TextView)findViewById(R.id.settingInf);
        // get current status
        
        sendStateReq();
        getUserInf();
    }
    @Override  
    protected void onResume() {  
        super.onResume();  
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InnerReceiver.ACTION);
        registerReceiver(receiver, intentFilter);       
    }  
      
    @Override  
    protected void onPause() {  
        super.onPause();  
        unregisterReceiver(receiver);  
    };
    private void sendStateReq(){
    	Intent intent = new Intent();
		intent.putExtra(PushServiceUtil.XMPP_USERNAME, "test2");	
		intent.putExtra(PushServiceUtil.XMPP_PASSWORD, "123456");	
		intent.setAction(PushServiceUtil.ACTION_SERVICE_LOGIN);
    	startService(intent);		
    }
    private void getUserInf(){
    	String userId = null;
    	String passWord = null;
    	
    	SharedPreferences sharedPrefs = this.getSharedPreferences(
                PushServiceUtil.SHARED_PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
        String xmppHost = sharedPrefs.getString(PushServiceUtil.XMPP_HOST, "localhost");
        int xmppPort = sharedPrefs.getInt(PushServiceUtil.XMPP_PORT, 5222);
        String username = sharedPrefs.getString(PushServiceUtil.XMPP_USERNAME, "");
        String password = sharedPrefs.getString(PushServiceUtil.XMPP_PASSWORD, "");
        sharedPrefs = null;
        
    	StringBuilder inf = new StringBuilder();
    	inf.append("µ±Ç°×´Ì¬£º" + connectState);
    	inf.append("\nHost:"+xmppHost);
    	inf.append("\nPort:"+Integer.toString(xmppPort));
    	inf.append("\nUserID:"+username);
    	inf.append("\nUserPassword:"+password);
    	infView.setText(inf);
    }
    private void setConnectState(String connectState){
    	this.connectState = connectState;
    	getUserInf();
    }
    
    public class InnerReceiver extends BroadcastReceiver{
    
    	public final static String ACTION = "com.openims.setting.Receiver"; 
    	@Override
    	public void onReceive(Context context,Intent intent){
    		String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
    		Log.d(LOGTAG,TAG+"STATUSE:"+status);
    		if(status.equals(PushServiceUtil.PUSH_STATUS_LOGIN_SUC)){
    			setConnectState("suc" + status);
    		}else{
    			setConnectState(status);
    		}
    	}
    }
}