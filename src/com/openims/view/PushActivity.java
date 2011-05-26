package com.openims.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.openims.R;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public class PushActivity extends FragmentActivity {
	
	private static final String LOGTAG = LogUtil
    .makeLogTag(PushActivity.class);
	private static final String CLASSNAME = "PushActivity";
	private static final String tagPushInf = "PushInf";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_manage);
        
        if (savedInstanceState == null) {
            // Do first time initialization -- add initial fragment.
            Fragment newFragment = new PushContentListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.details, newFragment,tagPushInf).commit();
        }
        // register broadcast for update UI
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushServiceUtil.ACTION_UI_PUSHCONTENT);
        this.registerReceiver(new PushReceiver(), filter);
    }
	
	public void changeDetail(int id){
		Fragment newFragment = null;
		String tag = "unknown";
		switch(id){
		case R.id.btnsetting:
			newFragment = new SettingFragment();
			tag = "setting";
			break;
		case R.id.btnpushInfo:
			newFragment = new PushContentListFragment();			
			tag = tagPushInf;
			break;
		case R.id.btnabout:
			newFragment = new AboutFragment();
			tag = tagPushInf;
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
		.findFragmentByTag(tagPushInf);
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

