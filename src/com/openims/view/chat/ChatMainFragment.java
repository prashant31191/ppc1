package com.openims.view.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
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
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

public class ChatMainFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
				.makeLogTag(ChatMainFragment.class);
	private static final String PRE = "ChatMainFragment--";

	private static String SEPARATOR = "  ";
	private java.text.DateFormat mDateFormat;
	
	private OnClickListener onClickAccountInf = null;
	private OnClickListener onClickHistory = null;
	
	private ListView mListView;
	private MessageRecord mMessageRecord;
	private EditText mInput;
	private TextView mTitleTextView;
	private ChatMainAdapter mListAdapter = null;
	private String mTableName = null;
	private Activity mActivity = null;
	private int columnIndexId = -1;
	private int columnIndexFromId = -1;
	private int columnIndexContent = -1;
	private int columnIndexDate = -1;
	
	private int mMsgStartId;
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
		mTitleTextView.clearFocus();
		
		mListView = (ListView)v.findViewById(R.id.listView);
		mInput = (EditText)v.findViewById(R.id.mchat_input);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
		
		mDateFormat = new SimpleDateFormat("HH:mm:ss");
		
		
		mListAdapter = new ChatMainAdapter(mActivity,mMyAccount);		
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
		
		mMessageRecord = new MessageRecord(mActivity, mTableName);
		
		
		if(mListAdapter==null){
			return;
		}	
		int num = -1;
		if(mMsgStartId==0){
			num = 0;
		}
		Cursor c = mMessageRecord.queryItems(mMsgStartId, num, false);
		
		if(columnIndexFromId == -1){
			columnIndexFromId = c.getColumnIndex(MessageRecord.FROM);
			columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
			columnIndexId = c.getColumnIndex(MessageRecord.ID);
			columnIndexDate = c.getColumnIndex(MessageRecord.DATE);
		}

		int n = c.getCount();
		int id;
		String fromJid;
		String content;
		Long date;
		
		c.moveToFirst();
		for(int i=0; i<n; i++){
			fromJid = c.getString(columnIndexFromId);
			content = c.getString(columnIndexContent);
			id = c.getInt(columnIndexId);	
			date = Long.valueOf(c.getString(columnIndexDate));
			
			fromJid = fromJid + SEPARATOR + mDateFormat.format(new Date(date));
			mListAdapter.addData(id, fromJid, content, false);
			c.moveToNext();
		}
		mListAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getLastVisiblePosition()); // »¬¶¯µ×²¿
		
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
		mMessageRecord.close();
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
	
	public void setTableName(String tableName, int msgStartId, 
			String toAccount, String myAccount){
		mTableName = tableName;
		mMsgStartId = msgStartId;
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
		int nStartId = mListAdapter.getLastId();
		if(nStartId == 0){
			nStartId = mMsgStartId;
		}else{
			nStartId++;
		}
		
		Cursor c ;
		if(nStartId != 0){
			c = mMessageRecord.queryItems(nStartId, -1, false);
		}else{
			c = mMessageRecord.queryItems(-1, 1, true);
		}
		
		if(columnIndexFromId == -1){
			columnIndexFromId = c.getColumnIndex(MessageRecord.FROM);
			columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
			columnIndexId = c.getColumnIndex(MessageRecord.ID);
			columnIndexDate = c.getColumnIndex(MessageRecord.DATE);
		}
		
		c.moveToFirst();
		int nCount = c.getCount();		
		for(int i=0; i<nCount; i++){
			long date = Long.valueOf(c.getString(columnIndexDate));
			mListAdapter.addData(c.getInt(columnIndexId),
					c.getString(columnIndexFromId)+ SEPARATOR + mDateFormat.format(new Date(date)), 
					c.getString(columnIndexContent), false);
			c.moveToNext();
		}
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
		long date = System.currentTimeMillis();
		int newId = mMessageRecord.insert(mMyAccount, mToAccount, 
				mInput.getText().toString(), String.valueOf(date) );
		
		if(mMsgStartId == 0){
			RosterDataBase roster = new RosterDataBase(mActivity,
            		mMyAccount);
            roster.updateColumn(mToAccount, RosterDataBase.NEW_MSG_START_ID, newId);
            roster.close();
            mMsgStartId = newId;
		}
		Log.d(TAG, PRE + "the row ID of the newly inserted row, or -1 if an error occurred" + newId);
		mListAdapter.addData(newId, 
				mMyAccount+SEPARATOR+mDateFormat.format(new Date(date)), 
				mInput.getText().toString(), false);
		mListAdapter.notifyDataSetChanged();
		
		// TODO change communication way
		Intent intent = new Intent(PushServiceUtil.ACTION_SERVICE_MESSAGE);
        intent.putExtra(PushServiceUtil.MESSAGE_TYPE, "chat");
        intent.putExtra(PushServiceUtil.MESSAGE_TOWHOS, mToAccount);
        intent.putExtra(PushServiceUtil.MESSAGE_CONTENT, mInput.getText().toString());
        mActivity.startService(intent);
       
        mInput.setText("");
	
	}

	public void setOnClickAccountInf(OnClickListener onClickAccountInf) {
		this.onClickAccountInf = onClickAccountInf;
	}

	public void setOnClickHistory(OnClickListener onClickHistory) {
		this.onClickHistory = onClickHistory;
	}	
}
