package com.openims.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.openims.R;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public class PushActivity extends FragmentActivity {
	
	private static final String TAG = LogUtil.makeLogTag(PushActivity.class);
	private static final String PRE = "PushActivity:";
	private static final String TAG_PUSH = "PushInf";
	private static final String TAG_SETTING = "setting";
	private static final String TAG_ABOUT = "about";
	
	private Fragment settingFragment = null;
	private Fragment pushContentFragment = null;
	private Fragment aboutFragment = null;
	
	
	
	public Fragment getSettingFragment() {
		if(settingFragment == null){
			settingFragment = new SettingFragment();
		}
		return settingFragment;
	}

	public Fragment getPushContentFragment() {
		if(pushContentFragment == null){
			pushContentFragment = new PushContentListFragment();
		}
		return pushContentFragment;
	}

	public Fragment getAboutFragment() {
		if(aboutFragment == null){
			aboutFragment = new AboutFragment();
		}
		return aboutFragment;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_manage);
        
        if (savedInstanceState == null) {
            // Do first time initialization -- add initial fragment.
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.details, getPushContentFragment(),TAG_PUSH).commit();
        }
        // register broadcast for update UI
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushServiceUtil.ACTION_UI_PUSHCONTENT);
        this.registerReceiver(new PushReceiver(), filter);
        
        Button showMenu = (Button)findViewById(R.id.show_menu);
        if(showMenu != null){
        	showMenu.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					MenuPopupWindow menu = new MenuPopupWindow(v,PushActivity.this);
					menu.showLikeQuickAction();
				}
        		
        	});
        }
    }
	
	public void changeDetail(int id){
		Fragment newFragment = null;
		String tag = "unknown";
		switch(id){
		case R.id.btnsetting:
			newFragment = getSettingFragment();
			tag = TAG_SETTING;
			break;
		case R.id.btnpushInfo:
			newFragment = getPushContentFragment();			
			tag = TAG_PUSH;
			break;
		case R.id.btnabout:
			newFragment = getAboutFragment();
			tag = TAG_ABOUT;
			break;
		}
		if(newFragment != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		     //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		     //ft.addToBackStack(null);  //这个是做什么用的？
		     ft.replace(R.id.details, newFragment,tag).commit();
		}
	}
	
	private void updateUI(){
		PushContentListFragment push = (PushContentListFragment)getSupportFragmentManager()
		.findFragmentByTag(TAG_PUSH);
		if(push != null)
			push.updateList();
		
	}
	 
	public class PushReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			updateUI();
		}		
	}
}

