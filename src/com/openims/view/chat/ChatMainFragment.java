package com.openims.view.chat;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public class ChatMainFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
				.makeLogTag(ChatMainFragment.class);
	private static final String PRE = "ChatMainFragment--";

	private OnClickListener onClickAccountInf = null;
	private OnClickListener onClickHistory = null;
	
	private ListView mListView;
	private EditText mInput;
	private TextView mTitleTextView;
	private ChatMainAdapter mListAdapter = null;
	private String mTableName = null;
	private Activity mActivity = null;
	private int columnIndexId;
	private int columnIndexFromId;
	private int columnIndexContent;
	
	private int mMessageNum;
	private String mToAccount = "test@smit";
	private String mMyAccount = "test2@smit";
	
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
		
		mTitleTextView = (TextView)v.findViewById(R.id.header_title);
		mTitleTextView.setText(mToAccount);
		// list view
		mListView = (ListView)v.findViewById(R.id.listView);
		
		//mListView.setDivider(new ColorDrawable(0xffff0000));
		//mListView.setDividerHeight(50);
		mInput = (EditText)v.findViewById(R.id.mchat_input);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
		
		initAdapter();
		
		if(mTableName == null || mListAdapter == null){
			throw new RuntimeException("call setTableName method first");
		}		
		mListView.setAdapter(mListAdapter);
		mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}
	public void initAdapter(){
		if(mActivity == null){
			return;
		}
		MessageRecord messageRecord = new MessageRecord(mActivity, mTableName);		
		Cursor c = messageRecord.queryItems(-1, mMessageNum, true);
		
		if(mListAdapter == null){
			mListAdapter = new ChatMainAdapter(mActivity,mMyAccount);
			columnIndexFromId = c.getColumnIndex(MessageRecord.FROM);
			columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
			columnIndexId = c.getColumnIndex(MessageRecord.ID);
		}

		int n = c.getCount();
		int id;
		String fromJid;
		String content;
		c.moveToLast();
		for(int i=0; i<n; i++){
			fromJid = c.getString(columnIndexFromId);
			content = c.getString(columnIndexContent);
			id = c.getInt(columnIndexId);			
			mListAdapter.addData(id, fromJid, content, false);
			c.moveToPrevious();
		}
		messageRecord.close();
		mListAdapter.notifyDataSetChanged();
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
	
	public void setTableName(String tableName, int messageNum, 
			String toAccount, String myAccount){
		mTableName = tableName;
		mMessageNum = messageNum;
		mMyAccount = myAccount;
		mToAccount = toAccount;
		if(mListAdapter != null){
			mListAdapter.removeAll();
		}
		if(mTitleTextView != null){
			mTitleTextView.setText(mToAccount);
		}
		initAdapter();
	}

	public void updateList(){
		if(mListAdapter.getLastId() == 0){
			mMessageNum = 1;
			initAdapter();
			return;
		}
		MessageRecord messageRecord = new MessageRecord(mActivity, mTableName);	
		
		Cursor c = messageRecord.queryItems(mListAdapter.getLastId()+1, -1, false);			
		c.moveToFirst();
		int nCount = c.getCount();		
		for(int i=0; i<nCount; i++){
			mListAdapter.addData(c.getInt(columnIndexId),
					c.getString(columnIndexFromId), 
					c.getString(columnIndexContent), false);
			c.moveToNext();
		}
		messageRecord.close();
		mListAdapter.notifyDataSetChanged();
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
			sendMessage();
			break;
		}
		
	}
	private void sendMessage(){
		if(mInput.getText().toString().isEmpty()){
			return;
		}
		MessageRecord messageRecord = new MessageRecord(mActivity, mTableName);	
		Date date = new Date(System.currentTimeMillis());
		int newId = messageRecord.insert(mMyAccount, mToAccount, mInput.getText().toString(), date.toLocaleString());
		Log.d(TAG, PRE + "the row ID of the newly inserted row, or -1 if an error occurred" + newId);
		mListAdapter.addData(newId, mMyAccount, mInput.getText().toString(), false);
		mListAdapter.notifyDataSetChanged();
		
		Intent intent = new Intent(PushServiceUtil.ACTION_SERVICE_MESSAGE);
        intent.putExtra(PushServiceUtil.MESSAGE_TYPE, "chat");
        intent.putExtra(PushServiceUtil.MESSAGE_TOWHOS, mToAccount);
        intent.putExtra(PushServiceUtil.MESSAGE_CONTENT, mInput.getText().toString());
        mActivity.startService(intent);
        messageRecord.close();
        mInput.setText("");
	
	}

	public void setOnClickAccountInf(OnClickListener onClickAccountInf) {
		this.onClickAccountInf = onClickAccountInf;
	}

	public void setOnClickHistory(OnClickListener onClickHistory) {
		this.onClickHistory = onClickHistory;
	}
	
	
	
}
