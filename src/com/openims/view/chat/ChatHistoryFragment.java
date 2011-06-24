package com.openims.view.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.utility.LogUtil;

public class ChatHistoryFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
							.makeLogTag(ChatHistoryFragment.class);
	private static final String PRE = "ChatHistoryFragment--";
	
	private static String SEPARATOR = "  ";
	private java.text.DateFormat mDateFormat;
	
	private Activity mActivity;
	
	private ListView mListview;
	private DEndlessAdapter mListAdapter;
	
	private int columnIndexId;
	private int columnIndexFromId;
	private int columnIndexContent;
	private int columnIndexDate;
	
	MessageRecord mMessageRecord;
	private String mTableName;
	private String mUserName;
	private boolean isEnd = false;
	
	private int mItemNumPerTime = 5;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}	
	public void setDataTableName(String tableName, String userName){
		mTableName = tableName;
		mUserName = userName;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.multi_chat_history, container, false);
		
		initListener(v);
		return v;
	}	
	
	private void initListener(View v){
		
		ImageButton btnHistory = (ImageButton)v.findViewById(R.id.header_right);		
		btnHistory.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				getFragmentManager().popBackStack();
			}
		});
		
		mListview = (ListView)v.findViewById(R.id.listView);
		mListview.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {				
				
				if(firstVisibleItem == 0 && 
						totalItemCount!=0 && 
						visibleItemCount!=0 &&
						isEnd == false){
					mListview.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
					mListAdapter.getKeepOnAppendingUp().set(true);
					mListAdapter.notifyDataSetChanged();
				}				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {				
			}			
		});		
	}	
	public void newMsgCome(){		
		int nlast = mListview.getLastVisiblePosition();
		int nCount = mListAdapter.getCount();
		mListAdapter.getKeepOnAppending().set(true);				
		mListAdapter.notifyDataSetChanged();
		
		if(nlast+2 >= nCount){  // 如果在某位，自己做滑动
			mListview.setSelection(nlast);
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, PRE + "onCreateView");	
		mDateFormat = new SimpleDateFormat("HH:mm:ss");
		initAdapter();			
	}
	private void initAdapter(){
		if(mActivity == null){
			return;
		}		
		
		mMessageRecord = new MessageRecord(mActivity, mTableName);		
		Cursor c = mMessageRecord.queryItems(-1, 20, true);
		
		if(mListAdapter == null){
			mListAdapter = new DEndlessAdapter(mActivity);
			columnIndexFromId = c.getColumnIndex(MessageRecord.FROM);
			columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
			columnIndexId = c.getColumnIndex(MessageRecord.ID);
			columnIndexDate = c.getColumnIndex(MessageRecord.DATE);
			mListview.setAdapter(mListAdapter);	
		}

		int n = c.getCount();
		int id;
		String fromJid;
		String content;
		Long date;
		
		c.moveToLast();
		for(int i=0; i<n; i++){
			fromJid = c.getString(columnIndexFromId);
			content = c.getString(columnIndexContent);
			id = c.getInt(columnIndexId);	
			
			date = Long.valueOf(c.getString(columnIndexDate));
			fromJid = fromJid + SEPARATOR + mDateFormat.format(new Date(date));
			
			mListAdapter.addData(id, fromJid, content, false);
			c.moveToPrevious();
		}
	}
	@Override
	public void onStart() {
		super.onStart();
		// scroll to bottom
		mListview.setSelection(2000);
		Log.d(TAG, PRE + "onStart");
		
	}

	@Override
	public void onResume() {
		super.onResume();
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
		mActivity = null;
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
	

	@Override
	public void onClick(View v) {
		
	}

	private class DEndlessAdapter extends ChatHistoryAdapter{
		private int nCount = 0;
		public DEndlessAdapter(Context context) {
			super(context);			
		}
		@Override
		protected void appendCachedData(Boolean isUp) {
			
			int startId;
			if(isUp){
				startId = mListAdapter.getFirstId();
				startId--;
			}else{
				startId = mListAdapter.getLastId();
				startId++;
			}
			
					
			Cursor c = mMessageRecord.queryItems(startId, mItemNumPerTime, isUp);			
			c.moveToFirst();
			nCount = c.getCount();
			if(nCount == 0 || mItemNumPerTime != nCount){
				if(isUp){
					isEnd = true;					
				}else{
					mListAdapter.getKeepOnAppending().set(false);
				}
			}
			for(int i=0; i<nCount; i++){
				long date = Long.valueOf(c.getString(columnIndexDate));
				mListAdapter.addData(c.getInt(columnIndexId),
						c.getString(columnIndexFromId)+ SEPARATOR + mDateFormat.format(new Date(date)), 
						c.getString(columnIndexContent), isUp);
				c.moveToNext();
			}
			
		}
		/**
		 * return true means there is more data
		 */
		@Override
		protected boolean cacheInBackground(Boolean isUp) throws Exception {
			SystemClock.sleep(1000);
			if(isUp){
				return false; // 通过isEnd来判断是还有更多
			}
			return true;
		}

		@Override
		protected void move(Boolean isUp) {
			
			if(isUp){				
				mListview.setSelection(nCount);				
			}else{
				int n = mListview.getLastVisiblePosition() - mListview.getFirstVisiblePosition();
				mListview.setSelection(mListAdapter.getCount() - nCount - n);
			}
		}
		
	}
}
