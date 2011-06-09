package com.openims.demo;



import com.openims.R;
import com.openims.utility.PushServiceUtil;
import com.openims.view.onlineHelper.ChatActivity;
import com.openims.view.setting.Setting;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	//ServiceManager serviceManager;
	private final static String Tag = "chenyz";
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DemoAppActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

        // Settings
        Button okButton = (Button) findViewById(R.id.btn_settings);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //ServiceManager.viewNotificationSettings(MainActivity.this);
            	//serviceManager.stopService();
            	startActivity(new Intent(MainActivity.this,Setting.class));
}
        });
        // start service
        Button startService = (Button) findViewById(R.id.startService);
        startService.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.i(Tag, "start service");				
				startService(new Intent(PushServiceUtil.ACTION_IMSERVICE));
				//bindService(new Intent(serviceName),);
			}
		});
        //end service
        Button endService = (Button) findViewById(R.id.EndService);
        endService.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.i(Tag, "start service");
				stopService(new Intent(PushServiceUtil.ACTION_IMSERVICE));
			}
		});
        // register push
        Button regPush = (Button) findViewById(R.id.RegPush);
        regPush.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				regPushService(true);							
			}
		});
        // unregister push
        Button unregPush = (Button) findViewById(R.id.UnregPush);
        unregPush.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				regPushService(false);							
			}
		});
        
        Button startChat = (Button) findViewById(R.id.chat);
        startChat.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setClassName("com.openims","com.openims.view.onlineHelper.ChatActivity");		       
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);			
			}
		});
        Button btnTopic  = (Button) findViewById(R.id.topic);
        btnTopic.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setClassName("com.openims","com.openims.view.PushActivity");		       
		        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
		        
		    	
			}
		});
    }
    
    void regPushService(boolean bReg){
    	Intent regIntent = new Intent(PushServiceUtil.ACTION_SERVICE_REGISTER);	

		if(bReg){
			regIntent.putExtra(PushServiceUtil.PUSH_TYPE,
					PushServiceUtil.PUSH_TYPE_REG);
		}else{
			regIntent.putExtra(PushServiceUtil.PUSH_TYPE,
					PushServiceUtil.PUSH_TYPE_UNREG);
		}
				
		regIntent.putExtra(PushServiceUtil.PUSH_DEVELOPER,
				"mtv");
		regIntent.putExtra(PushServiceUtil.PUSH_NAME_KEY,
		"V1p0Ue5W3zpFqUmzd1W988N0Ci7aPMV3");
		regIntent.putExtra(PushServiceUtil.PUSH_PACKAGENAME, 
				"com.openims");
		regIntent.putExtra(PushServiceUtil.PUSH_CLASSNAME, 
				"com.openims.demo.PushServiceReceiver");
		
		startService(regIntent);	
    }

    private void pendingIntent(String uriString,String title,String text){
  	  	Context serviceContext = this;
    	NotificationManager mNotificationManager = 
    		(NotificationManager) serviceContext.
    		getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	int icon = R.drawable.icon;
    	CharSequence tickerText = "Hello";
    	long when = System.currentTimeMillis();

    	Notification notification = new Notification(icon, tickerText, when);
    	
    	Context context = serviceContext.getApplicationContext();  
    	CharSequence contentTitle = title;  
    	CharSequence contentText = text;  
    	
    	Uri uri = Uri.parse(uriString);
    	Intent intent = new Intent(Intent.ACTION_VIEW,uri);
    	PendingIntent contentIntent = 
    		PendingIntent.getActivity(serviceContext, 
    		0, intent, 0);  
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    	mNotificationManager.notify(1, notification);
    }
   
}