package com.openims.view.chat;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.openims.R;
import com.openims.model.chat.MessageRecord;
import com.openims.utility.LogUtil;

public class ChatHistoryFragment extends Fragment implements OnClickListener{

	private static final String TAG = LogUtil
							.makeLogTag(ChatHistoryFragment.class);
	private static final String PRE = "ChatHistoryFragment--";
	

	private ListView listview;
	private DEndlessAdapter adapter;
	private int columnIndexId;
	private int columnIndexToId;
	private int columnIndexContent;
	private Activity activity;
	private String mTableName = "TB_555_77";
	private boolean isEnd = false;
	
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
		View v = inflater.inflate(R.layout.multi_chat_history, container, false);
		
		Button btnInf = (Button)v.findViewById(R.id.header_left);
		btnInf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
				adapter.getKeepOnAppendingUp().set(true);
				adapter.notifyDataSetChanged();
				listview.smoothScrollToPosition(0);*/
				
			}
		});
		Button btnHistory = (Button)v.findViewById(R.id.header_right);		
		btnHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				adapter.getKeepOnAppending().set(true);				
				adapter.notifyDataSetChanged();*/
				getFragmentManager().popBackStack();
			}
		});
		
		listview = (ListView)v.findViewById(R.id.listView);
		listview.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.d(TAG, PRE + "onScroll-- first" + firstVisibleItem 
						+ "count:" + visibleItemCount + 
						"total:" + totalItemCount);
				if(firstVisibleItem + visibleItemCount==totalItemCount && 
						totalItemCount!=0 && visibleItemCount!=0){
					/*listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
					adapter.getKeepOnAppending().set(true);				
					adapter.notifyDataSetChanged();*/
				}
				if(firstVisibleItem == 0 && 
						totalItemCount!=0 && 
						visibleItemCount!=0 &&
						isEnd == false){
					listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
					adapter.getKeepOnAppendingUp().set(true);
					adapter.notifyDataSetChanged();
					//listview.smoothScrollToPosition(0);
				}
				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		adapter = new DEndlessAdapter(getActivity());
		// init data for list
		activity = getActivity();
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, PRE + "onCreateView");
		
		MessageRecord messageRecord = new MessageRecord(activity, mTableName);		
		Cursor c = messageRecord.queryItems(20, 10, false);
		
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
			adapter.addData(id, toId, content, false);
			c.moveToNext();
		}
		messageRecord.close();
		listview.setAdapter(adapter);	
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// scroll to bottom
		listview.setSelection(2000);
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
				startId = adapter.getFirstId();
				startId--;
			}else{
				startId = adapter.getLastId();
				startId++;
			}
			// TODO maybe I should save messageRecord
			MessageRecord messageRecord = new MessageRecord(activity, mTableName);		
			Cursor c = messageRecord.queryItems(startId, 3, isUp);			
			c.moveToFirst();
			nCount = c.getCount();
			if(nCount == 0){
				isEnd = true;
			}
			for(int i=0; i<nCount; i++){
				adapter.addData(c.getInt(columnIndexId),
						c.getString(columnIndexToId), 
						c.getString(columnIndexContent), isUp);
				c.moveToNext();
			}
			messageRecord.close();
			listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
			
			
		}

		/**
		 * return true means there is more data
		 */
		@Override
		protected boolean cacheInBackground(Boolean isUp) throws Exception {
			SystemClock.sleep(2000);			
			return false;
		}

		@Override
		protected void move(Boolean isUp) {
			listview.setSelection(nCount);			
		}
		
	}
}
