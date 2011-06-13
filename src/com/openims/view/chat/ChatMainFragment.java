package com.openims.view.chat;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.utility.LogUtil;

public class ChatMainFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
				.makeLogTag(ChatMainFragment.class);
	private static final String PRE = "ChatMainFragment--";

	private OnClickListener onClickAccountInf = null;
	private OnClickListener onClickHistory = null;
	
	private ListView mListView;
	private EditText mInput;
	private ChatMainAdapter mListAdapter;
	private String mTableName = "TB_555_77";
	private Activity mActivity;
	private int columnIndexId;
	private int columnIndexToId;
	private int columnIndexContent;
	
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
		View v = inflater.inflate(R.layout.multi_chat_main, container, false);
		
		v.findViewById(R.id.header_left).setOnClickListener(this);
		v.findViewById(R.id.header_right).setOnClickListener(this);
		v.findViewById(R.id.mchat_send).setOnClickListener(this);
		
		// list view
		mListView = (ListView)v.findViewById(R.id.listView);
		mInput = (EditText)v.findViewById(R.id.mchat_input);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		
		mListAdapter = new ChatMainAdapter(mActivity);
		
		MessageRecord messageRecord = new MessageRecord(mActivity, mTableName);		
		Cursor c = messageRecord.queryItems(0, -1, false);
		
		columnIndexToId = c.getColumnIndex(MessageRecord.TO);
		columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
		columnIndexId = c.getColumnIndex(MessageRecord.ID);
		int n = c.getCount();
		int id;
		String toId;
		String content;
		c.moveToFirst();
		for(int i=0; i<n; i++){
			toId = c.getString(columnIndexToId);
			content = c.getString(columnIndexContent);
			id = c.getInt(columnIndexId);			
			mListAdapter.addData(id, toId, content, false);
			c.moveToNext();
		}
		mListView.setAdapter(mListAdapter);
		mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, PRE + "onStart");
		mListView.setSelection(1000);
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
			if(onClickAccountInf != null)
				onClickAccountInf.onClick(v);
			break;
		case R.id.header_right:
			if(onClickHistory != null)
				onClickHistory.onClick(v);
			break;
		case R.id.mchat_send:
			mListAdapter.addData(11, "wo send", mInput.getText().toString(), false);
			mListAdapter.notifyDataSetChanged();
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
