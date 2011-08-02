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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.openims.model.MyApplication;
import com.openims.utility.DeviceFun;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.MultiChatActivity;
import com.openims.view.chat.widget.IMActivity;
import com.openims.view.setting.Setting;
import com.smit.EasyLauncher.LoginActivity;
import com.smit.EasyLauncher.R;
import com.smit.rssreader.RSSReaderActivity;

public class MainActivity extends Activity {

	private final static String Tag = "chenyz";
	private XMPPConnection connection;
	private boolean isLogin = false;
    private LoginOrOutReceiver loginOrOutReceiver;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("DemoAppActivity", "onCreate()...");

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);
		DeviceFun.printDeviceInf("OpenIMS");
		
		//注册广播，接收Login与Logout的广播
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushServiceUtil.ACTION_STATUS);
        MainActivity.this.registerReceiver(loginOrOutReceiver, intentFilter);
		
        /*
		 * Animation hyperspaceJumpAnimation =
		 * AnimationUtils.loadAnimation(MainActivity.this,
		 * R.anim.my_rotate_action); View v = this.findViewById(R.id.main);
		 * v.startAnimation(hyperspaceJumpAnimation);
		 */

		// Settings
		Button okButton = (Button) findViewById(R.id.btn_settings);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Animation hyperspaceJumpAnimation = AnimationUtils
						.loadAnimation(MainActivity.this,
								R.anim.my_rotate_action);
				view.startAnimation(hyperspaceJumpAnimation);
				startActivity(new Intent(MainActivity.this, Setting.class));
				overridePendingTransition(R.anim.grow_from_bottom,
						R.anim.grow_from_bottomleft_to_topright);
			}
		});
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.my_rotate_action);
		okButton.startAnimation(animation);
		// start service
		Button startService = (Button) findViewById(R.id.startService);
		startService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(Tag, "start service");
				startService(new Intent(PushServiceUtil.ACTION_SERVICE_CONNECT));
				// bindService(new Intent(serviceName),);
			}
		});
		// end service
		Button endService = (Button) findViewById(R.id.EndService);
		endService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// startService(new
				// Intent(PushServiceUtil.ACTION_SERVICE_REGISTER_USER));
				stopService(new Intent(
						PushServiceUtil.ACTION_SERVICE_REGISTER_USER));
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

		// RSS Reader
		Button rssReader = (Button) findViewById(R.id.start_rss);
		rssReader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isLogin == true){
//					Intent intent = new Intent();
//					MyApplication myApp = (MyApplication)MainActivity.this.getApplication();
//	    	        XMPPConnection connection = myApp.getConnection();
//	    	        String jid = myApp.getAdminJid();
//	    	        String server = myApp.getServeName();
//	    	        interactive = new InteractiveServer(connection, jid, server, RSSReaderActivity.this);
					startActivity(new Intent(MainActivity.this,
							RSSReaderActivity.class));
				}else{
					//调用登录对话框
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, LoginActivity.class);
					startActivity(intent);	
				}
				
			}
		});

		Button startChat = (Button) findViewById(R.id.chat);
		startChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, IMActivity.class);
				intent.putExtra(MultiChatActivity.ACCOUNT_JID, 66);

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		Button btnTopic = (Button) findViewById(R.id.topic);
		btnTopic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new
				// Intent(MainActivity.this,PushActivity.class);
				// startActivity(intent);
			}
		});
	}

	public void userSearch() {
		UserSearchManager search = new UserSearchManager(connection);

		try {
			List<String> list = (List<String>) search.getSearchServices();
			String searchService = list.get(0);
			Form from = search.getSearchForm(searchService);
			String title = from.getTitle();
			String type = from.getType();
			Iterator<FormField> it = from.getFields();
			while (it.hasNext()) {
				FormField fromField = it.next();
				String fieldType = fromField.getType();
				String v = fromField.getVariable();
				String label = fromField.getLabel();
			}

			Form answerForm = from.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", "test*");
			ReportedData data = search.getSearchResults(answerForm,
					searchService);
			Iterator<Row> itRow = data.getRows();
			while (itRow.hasNext()) {
				Row row = itRow.next();
				Iterator<String> i = row.getValues(null);
			}
			title = data.getTitle();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void regPushService(boolean bReg) {
		Intent regIntent = new Intent(PushServiceUtil.ACTION_SERVICE_REGISTER);

		if (bReg) {
			regIntent.putExtra(PushServiceUtil.PUSH_TYPE,
					PushServiceUtil.PUSH_TYPE_REG);
		} else {
			regIntent.putExtra(PushServiceUtil.PUSH_TYPE,
					PushServiceUtil.PUSH_TYPE_UNREG);
		}

		regIntent.putExtra(PushServiceUtil.PUSH_DEVELOPER, "mtv");
		regIntent.putExtra(PushServiceUtil.PUSH_NAME_KEY,
				"T3aXoTF0oz8nIbqCBdEq34a00O67rblh");
		regIntent.putExtra(PushServiceUtil.PUSH_CATEGORY, "com.openims.demo");

		startService(regIntent);
	}

	private class LoginOrOutReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
			Log.i("STATUSE:", "-------" + status);
			if (PushServiceUtil.PUSH_STATUS_LOGIN_SUC.equals(status)) {
				isLogin = true;
			}else if(PushServiceUtil.PUSH_STATUS_LOGOUT.equals(status)){
				isLogin = false;
			}
		}
	}
	
}