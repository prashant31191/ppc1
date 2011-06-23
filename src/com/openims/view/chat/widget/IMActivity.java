package com.openims.view.chat.widget;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import com.openims.R;
import com.openims.utility.LogUtil;

public class IMActivity extends FragmentActivity {

	private static final String TAG = LogUtil.makeLogTag(IMActivity.class);
	private static final String PRE = "Class IMActivity--";
	
	private IMWidgetFragment mIMWidgetFragment;
	private final static String TAG_IM_MAIN = "TAG_IM_MAIN";
	
	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		Log.i(TAG, PRE + "onCreate");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.im_widget);
		
		if(bundle == null){
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			mIMWidgetFragment = new IMWidgetFragment();			
	        ft.add(R.id.widget_container_im, mIMWidgetFragment,TAG_IM_MAIN).commit();
			
		}else{			
			mIMWidgetFragment = (IMWidgetFragment)getSupportFragmentManager()
					.findFragmentByTag(TAG_IM_MAIN);
		}
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		metrics.getClass();
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
		super.onDestroy();
	}
	
}
