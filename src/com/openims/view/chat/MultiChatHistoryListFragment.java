package com.openims.view.chat;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.utility.LogUtil;
import com.openims.view.PushContentListFragment;

public class MultiChatHistoryListFragment extends ListFragment{

	private static final String TAG = LogUtil
	.makeLogTag(MultiChatHistoryListFragment.class);
	private static final String PRE = "MultiChatHistoryListFragment--";

	private CursorAdapter cursorAdapter;
	private View mheaderView;
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		Activity activity = this.getActivity();
		
		MessageRecord messageRecord = new MessageRecord(activity, "TB_555_66");
		cursorAdapter = new SimpleCursorAdapter(activity,
				R.layout.multi_chat_message_item,
				messageRecord.queryAll(),
				new String[]{MessageRecord.TO,MessageRecord.CONTENT},
				new int[]{R.id.tv_chat_item_name_time,R.id.tv_chat_item_content});		
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View v = super.onCreateView(inflater, container, savedInstanceState);	
		mheaderView = inflater.inflate(R.layout.multi_chat_header, null);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		
		//LayoutInflater inflater = this.getActivity().getLayoutInflater();
		//View view = inflater.inflate(R.layout.multi_chat_header, null);		
		//getListView().addHeaderView(mheaderView);
		
		setEmptyText("nodata");
		setListAdapter(cursorAdapter);
		
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
