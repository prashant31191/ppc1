package com.openims.view.chat;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.openims.R;
import com.openims.utility.LogUtil;

public class ChatAccountInfFragment extends Fragment{
	
	private static final String TAG = LogUtil
			.makeLogTag(ChatAccountInfFragment.class);
	private static final String PRE = "ChatAccountInfFragment--";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.multi_chat_account_inf, container, false);
		
		v.findViewById(R.id.header_right).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
			
		});
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		
		
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}	

	@Override
	public void onPause() {
		super.onPause();
	}

	
	@Override
	public void onStop() {
		super.onStop();
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
