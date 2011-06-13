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

public class AccountInfFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
				.makeLogTag(AccountInfFragment.class);
	private static final String PRE = "ChatMainFragment--";

	private OnClickListener onClickAccountInf = null;
	private OnClickListener onClickHistory = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, PRE + "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, PRE + "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.multi_chat_account_inf, container, false);
		
		View btn = v.findViewById(R.id.header_left);
		btn.setOnClickListener(this);
		v.findViewById(R.id.header_right).setOnClickListener(this);
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, PRE + "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, PRE + "onResume");
	}	

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, PRE + "onPause");
	}

	
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, PRE + "onStop");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, PRE + "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, PRE + "onDetach");
	}

	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.header_left:
			getFragmentManager().popBackStack();
			break;
		case R.id.header_right:
			getFragmentManager().popBackStack();
			break;
		}
		
	}

	public void setOnClickAccountInf(OnClickListener onClickAccountInf) {
		this.onClickAccountInf = onClickAccountInf;
	}

	public void setOnClickHistory(OnClickListener onClickHistory) {
		this.onClickHistory = onClickHistory;
	}
	
	
	
}
