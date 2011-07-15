package com.openims.view.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.smit.EasyLauncher.R;
import com.openims.model.chat.MessageRecord;
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.OnAvater.OnAvaterListener;

public class ChatHistoryFragment extends Fragment
			implements OnAvaterListener{

	private static final String TAG = LogUtil
							.makeLogTag(ChatHistoryFragment.class);
	private static final String PRE = "ChatHistoryFragment--";
	
	private static String SEPARATOR = "  ";
	private java.text.DateFormat mDateFormat;
	
	private Activity mActivity;
	
	private ListView mListview;
	private ChatHistoryAdapter mListAdapter;
	
	private int columnIndexId;
	private int columnIndexFromId;
	private int columnIndexContent;
	private int columnIndexDate;
	
	MessageRecord mMessageRecord;
	private String mTableName;
	
	private String mYourJid;
	private String mMyJid;
	
	private Drawable mMyAvater;
	private Drawable mYourAvater;
	private Boolean mIsPresence = true;
	
	private boolean isEnd = false;  // 用来指示向上滑动终止
	
	private int mItemNumPerTime = 5;
	private int mHistoryItemInitNum = 20;
	
	private OnAvater mOnAvater;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}	
	public void setDataTableName(String tableName, String myJid, String yourJid){
		mTableName = tableName;
		mMyJid = myJid;
		mYourJid = yourJid;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mItemNumPerTime = getResources().getInteger(R.integer.load_item_per_time);
		mHistoryItemInitNum = getResources().getInteger(R.integer.history_init_item_num);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.multi_chat_history, container, false);		
		addListener(v);
		return v;
	}	
	
	private void addListener(View v){
		
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
				// listener scroll to the top
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
	
	@Override
	public void onStart() {
		super.onStart();		
		mListview.setSelection(2000);	// scroll to bottom
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
	
	private void initAdapter(){
		if(mActivity == null){
			return;
		}		
		
		mMessageRecord = new MessageRecord(mActivity, mTableName);		
		Cursor c = mMessageRecord.queryItems(-1, mHistoryItemInitNum, true);
		
		if(mListAdapter == null){
			mListAdapter = new ChatHistoryAdapter(mActivity);
			columnIndexFromId = c.getColumnIndex(MessageRecord.FROM);
			columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
			columnIndexId = c.getColumnIndex(MessageRecord.ID);
			columnIndexDate = c.getColumnIndex(MessageRecord.DATE);
			mListview.setAdapter(mListAdapter);	
		}

		int n = c.getCount();	
		Long date;		
		c.moveToLast();
		for(int i=0; i<n; i++){
			ChatMessage msg = new ChatMessage();
			msg.jid = c.getString(columnIndexFromId);
			msg.content = c.getString(columnIndexContent);
			msg.id = c.getInt(columnIndexId);	
			
			date = Long.valueOf(c.getString(columnIndexDate));
			msg.nickName = msg.jid + SEPARATOR + mDateFormat.format(new Date(date));
			
			mListAdapter.addData(msg, false);
			c.moveToPrevious();
		}
		updateState();
		updatePresence();
	}
	
	public void notifyDataSetChanged(){
		mListAdapter.notifyDataSetChanged();
	}
	private void updateState(){
		if(mOnAvater != null){
			mYourAvater = mOnAvater.getAvater(mYourJid, this);
			mMyAvater = mOnAvater.getAvater(mMyJid, this);
		}
	}
	public void updatePresence(){
		RosterDataBase roster = new RosterDataBase(mActivity,
        		mMyJid);
        String presence = roster.getPresence(mYourJid);
        roster.close();
        if(presence.equals(Presence.Type.available.name())){
        	mIsPresence = true;
    	}else{
    		mIsPresence = false;
    	}
	}
	//---------------------setter------------------------------------
	public void setOnAvater(OnAvater onAvater){
		mOnAvater = onAvater;
	}

	private class ChatMessage{
		public Integer id;
		public String jid;
		public String nickName;
		public String content;
	}
	
	public class ChatHistoryAdapter extends BaseAdapter {		
		

		private static final String PRE = "ChatHistoryAdapter--";	       
	    
		private AtomicBoolean keepOnAppendingFooter=new AtomicBoolean(false);
		private AtomicBoolean keepOnAppendingHeader=new AtomicBoolean(false);
		
		//private Context context;
		private LayoutInflater mInflater;
		private View pendingView=null;
		private View pendingViewUp=null;
		
		private RotateAnimation rotate=null;
		
		private final static int MAX_SIZE = 20;
		
		LinkedList<ChatMessage> messages = new LinkedList<ChatMessage>();
		
		private int nCount = 0;		
		
		public ChatHistoryAdapter(Context context){		
			
			rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			rotate.setDuration(1000);
			rotate.setRepeatMode(Animation.RESTART);
			rotate.setRepeatCount(Animation.INFINITE);
			
			mInflater = (LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}	
		@Override
		public int getCount() {
			int n = messages.size();
			if (keepOnAppendingFooter.get()) {
				n = n + 1;						// one more for "pending"
			}
			if (keepOnAppendingHeader.get()) {
				n = n + 1;						// one more for "pending"
			}
			Log.d(TAG, PRE + "getCount--" + n);
			return n;
		}

		/**
		 * Masks ViewType so the AdapterView replaces the "Pending" row when new
		 * data is loaded.
		 */
		@Override
		public int getItemViewType(int position) {
			if (position== (getCount()-1) ) {
				return(IGNORE_ITEM_VIEW_TYPE);
			}
			if (position== 0 ) {
				return(IGNORE_ITEM_VIEW_TYPE);
			}
			ChatMessage msg = messages.get(position);
			if(msg.jid.startsWith(mMyJid)){
				return 0;
			}
			return 1;
		}
		@Override
		public int getViewTypeCount() {
			return 3;
		}
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}
		
		@Override
		public boolean isEnabled(int position) {
			return false;
		}
		@Override
		public Object getItem(int position) {		
			return null;
		}

		@Override
		public long getItemId(int position) {	
			if(messages.isEmpty() || position >= messages.size()){
				return 0;
			}
			return messages.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, PRE + "getView position=" + position);
			
			// show footer waiting
			if (position==(getCount()-1) &&
					keepOnAppendingFooter.get()) {
				
				if (pendingView==null) {				
					pendingView=getPendingView(parent);
					new AppendTask().execute(false);
					Log.d(TAG, PRE + "start to load data");
				}
				return(pendingView);
			}
			// show header waiting
			if(position==0 && keepOnAppendingHeader.get()){
				if(pendingViewUp == null){
					pendingViewUp=getPendingView(parent);
					new AppendTask().execute(true);
					Log.d(TAG, PRE + "start to load back up data");				
				}
				return pendingViewUp;
			}
			if(keepOnAppendingHeader.get()){
				position = position - 1; 	//被提示占了一个位置，数据往后退
				Log.d(TAG, PRE + "减少一个  position=" + position);
			}
			// normal situation
			boolean isMe = false;
			ChatMessage msg = messages.get(position);
			if(msg.jid.startsWith(mMyJid)){	
				isMe = true;
			}
			View v = null;
			if (convertView == null) {
	            v = mInflater.inflate(
	            		isMe?R.layout.multi_chat_message_item_right:
	            			R.layout.multi_chat_message_item, null);
	        } else {
	            v = convertView;
	        }			
			
			TextView name = (TextView)v.findViewById(R.id.tv_chat_item_name_time);
			name.setText(msg.nickName);
			TextView content = (TextView)v.findViewById(R.id.tv_chat_item_content);
			content.setText(msg.content);
			
			ImageView avater = (ImageView)v.findViewById(R.id.avater);
			if(isMe){
				avater.setImageDrawable(mMyAvater);
				avater.setColorFilter(null);
			}else{				
				avater.setImageDrawable(mYourAvater);
				if(mIsPresence){
					avater.setColorFilter(null);
				}else{
					avater.setColorFilter(PushServiceUtil.GREY_COLOR_FILTER);
				}
			}
			return v;
		}
		
		protected View getPendingView(ViewGroup parent) {
			
			View row=mInflater.inflate(R.layout.pending, null);
			
			View child = row.findViewById(R.id.throbber);
			child.setVisibility(View.VISIBLE);
			child.startAnimation(rotate);
			
			return(row);
		}
		
		/**
		 * A background task that will be run when there is a need
		 * to append more data. Mostly, this code delegates to the
		 * subclass, to append the data in the background thread and
		 * rebind the pending view once that is done.
		 */
		class AppendTask extends AsyncTask<Boolean, Void, Exception> {
			Boolean mIsUp = false;
			@Override
			protected Exception doInBackground(Boolean... params) {
				Exception result=null;
				mIsUp = params[0];
				try {
					SystemClock.sleep(1000);
					if(mIsUp){
						keepOnAppendingHeader.set(false); // 通过isEnd来判断是还有更多
					}else{
						keepOnAppendingFooter.set(true);
					}					
				}catch (Exception e) {
					result=e;
				}			
				return(result);
			}

			@Override
			protected void onPostExecute(Exception e) {
				if (e!=null) {
					return;
				}
				
				int startId;
				if(mIsUp){
					startId = getFirstId();
					startId--;
				}else{
					startId = getLastId();
					startId++;
				}				
						
				Cursor c = mMessageRecord.queryItems(startId, mItemNumPerTime, mIsUp);			
				c.moveToFirst();
				nCount = c.getCount();
				if(nCount == 0 || mItemNumPerTime != nCount){
					if(mIsUp){
						isEnd = true;					
					}else{
						getKeepOnAppending().set(false);
					}
				}
				for(int i=0; i<nCount; i++){
					ChatMessage msg = new ChatMessage();
					long date = Long.valueOf(c.getString(columnIndexDate));
					msg.id = c.getInt(columnIndexId);
					msg.jid = c.getString(columnIndexFromId);
					msg.nickName = msg.jid + SEPARATOR + mDateFormat.format(new Date(date)); 
					msg.content = c.getString(columnIndexContent);
					addData(msg, mIsUp);
					c.moveToNext();
				}
				
				if(mIsUp){
					pendingViewUp=null;
				}else{
					pendingView=null;
				}			
				notifyDataSetChanged();

				if(mIsUp){				
					mListview.setSelection(nCount);				
				}else{
					int n = mListview.getLastVisiblePosition() - mListview.getFirstVisiblePosition();
					mListview.setSelection(getCount() - nCount - n);
				}
				
			}
		}


		public AtomicBoolean getKeepOnAppending() {
			return keepOnAppendingFooter;
		}
		public AtomicBoolean getKeepOnAppendingUp() {
			return keepOnAppendingHeader;
		}
		public void removeAll(){
			messages = new LinkedList<ChatMessage>();
		}
		public void addData(ChatMessage msg,boolean isFirst){
			
			if(isFirst == false){
				if(messages.size() > MAX_SIZE){
					messages.removeFirst();
				}
				messages.addLast(msg);
			} else {
				if(messages.size() > MAX_SIZE){
					messages.removeLast();
					keepOnAppendingFooter.set(true);
				}
				messages.addFirst(msg);
			}
				
		}
		public int getFirstId(){
			if(messages.isEmpty())
				return 0;
			return messages.getFirst().id;
		}
		public int getLastId(){
			if(messages.isEmpty())
				return 0;
			return messages.getLast().id;
		}
	}

	@Override
	public void avater(String avaterJid, Drawable avater) {
				
	}
}
