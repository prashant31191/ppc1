package com.openims.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;

import com.openims.R;
import com.openims.utility.LogUtil;

public class PushActivity extends FragmentActivity {
	private static final String LOGTAG = LogUtil
    .makeLogTag(PushActivity.class);
	private static final String CLASSNAME = "PushActivity";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_manage);
        
        if (savedInstanceState == null) {
            // Do first time initialization -- add initial fragment.
            Fragment newFragment = new SettingFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.details, newFragment).commit();
        }
    }
	
	public void changeDetail(int id){
		Fragment newFragment = null;
		switch(id){
		case R.id.btnsetting:
			newFragment = new SettingFragment();
			break;
		case R.id.btnpushInfo:
			newFragment = new PushInfFragment();			
			break;
		case R.id.btnabout:
			newFragment = new AboutFragment();
			break;
		}
		if(newFragment != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		     //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		     //ft.addToBackStack(null);  //这个是做什么用的？
		     ft.replace(R.id.details, newFragment).commit();
		}
	}
}
