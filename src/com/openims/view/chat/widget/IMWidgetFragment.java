package com.openims.view.chat.widget;

import com.openims.R;
import com.openims.utility.LogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class IMWidgetFragment extends Fragment implements OnClickListener {

	private static final String TAG = LogUtil
					.makeLogTag(IMWidgetFragment.class);
	private static final String PRE = "IMWidgetFragment--";

	private Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
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
		View v = inflater.inflate(R.layout.im_main_fragment, container, false);
		
		v.findViewById(R.id.btn_friend).setOnClickListener(this);
		v.findViewById(R.id.btn_group).setOnClickListener(this);
		v.findViewById(R.id.btn_recent).setOnClickListener(this);
		
				
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
