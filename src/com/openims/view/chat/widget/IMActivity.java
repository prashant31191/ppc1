package com.openims.view.chat.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;

import com.smit.EasyLauncher.R;
import com.openims.model.MyApplication;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public class IMActivity extends FragmentActivity {

	private static final String TAG = LogUtil.makeLogTag(IMActivity.class);
	private static final String PRE = "Class IMActivity--";
	
/*	private IMWidgetFragment mIMWidgetFragment;
	private final static String TAG_IM_MAIN = "TAG_IM_MAIN";*/
	
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.im_widget);
		
		startService(new Intent(PushServiceUtil.ACTION_SERVICE_CONNECT));
		
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, PRE + "onStart");
		// connect to service
		// doBindService();
		super.onStart();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, PRE + "onPause");
		
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, PRE + "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, PRE + "onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, PRE + "onStop");
		//doUnbindService();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, PRE + "onDestroy");
		//stopService(new Intent(PushServiceUtil.ACTION_SERVICE_REGISTER_USER));
		super.onDestroy();
	}
	
}
