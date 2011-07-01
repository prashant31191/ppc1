package com.openims.view.chat;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.smit.EasyLauncher.R;

public class UserSearchActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle bundle) {		
		super.onCreate(bundle);
		
		setContentView(R.layout.im_user_search_activity);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
