package com.openims.view.chat.widget;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.openims.utility.LogUtil;

public class RecentChatListFragment extends ListFragment {

	private static final String TAG = LogUtil
		.makeLogTag(RecentChatListFragment.class);
	private static final String PRE = "RecentChatListFragment:";

	private BaseAdapter mListAdapter;
	@Override
	public void onAttach(Activity activity) {
		Log.e(TAG, PRE + "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		this.setListAdapter(mListAdapter);	
		
		
	}
	@Override
	public void onListItemClick(ListView l, View view, int position, final long id) {	
		Log.i(TAG,PRE + "onListItemClick ID : " + id);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e(TAG, PRE + "onActivityCreated");		
		super.onActivityCreated(savedInstanceState);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		this.setEmptyText("NO DATA");
	}

	@Override
	public void onStart() {
		Log.e(TAG, PRE + "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.e(TAG, PRE + "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.e(TAG, PRE + "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e(TAG, PRE + "onStop");		
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.e(TAG, PRE + "onDestroyView");
		
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, PRE + "onDestroy");		
		
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.e(TAG, PRE + "onDetach");
		super.onDetach();
	}
}
