package com.openims.demo;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.openims.utility.PushServiceUtil;
import com.openims.view.PushActivity;
import com.openims.view.chat.MultiChatActivity;
import com.openims.view.chat.widget.IMActivity;
import com.openims.view.setting.Setting;
import com.smit.EasyLauncher.R;

public class MainActivity extends Activity {

	
	private final static String Tag = "chenyz";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DemoAppActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
        		WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        

        // Settings
        Button okButton = (Button) findViewById(R.id.btn_settings);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
				Intent intent = new Intent(MainActivity.this,IMActivity.class);
				intent.putExtra(MultiChatActivity.ACCOUNT_JID, 66);
				
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);			
			}
		});
        Button btnTopic  = (Button) findViewById(R.id.topic);
        btnTopic.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,PushActivity.class);				
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
   
   
}