package com.openims.demo;



import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.openims.model.MyApplication;
import com.openims.utility.PushServiceUtil;
import com.openims.view.PushActivity;
import com.openims.view.chat.MultiChatActivity;
import com.openims.view.chat.widget.IMActivity;
import com.openims.view.setting.Setting;
import com.smit.EasyLauncher.R;

public class MainActivity extends Activity {

	
	private final static String Tag = "chenyz";
	private XMPPConnection connection;
	
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
				//regPushService(true);		
				MyApplication app = (MyApplication)MainActivity.this.getApplication();
				connection = app.getConnection();
				userSearch();
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
    public void userSearch(){
    	UserSearchManager search = new UserSearchManager(connection);
    	
    	try {
    		List<String> list = (List<String>)search.getSearchServices();
			String searchService = list.get(0);
			Form from = search.getSearchForm(searchService);
			String title = from.getTitle();
			String type = from.getType();
			Iterator<FormField> it = from.getFields();
			while(it.hasNext()){
				FormField fromField = it.next();
				String fieldType = fromField.getType();
				String v = fromField.getVariable();
				String label = fromField.getLabel();
			}
			
			Form answerForm = from.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "test*");
			ReportedData data =search.getSearchResults(answerForm, searchService);
			Iterator<Row> itRow = data.getRows();
			while(itRow.hasNext()){
				Row row = itRow.next();
				Iterator<String> i = row.getValues(null);
			}
			title = data.getTitle();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
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