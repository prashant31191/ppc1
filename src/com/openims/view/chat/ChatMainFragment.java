package com.openims.view.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smit.EasyLauncher.R;
import com.openims.model.chat.MessageRecord;
import com.openims.model.chat.RosterDataBase;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.chat.OnAvater.OnAvaterListener;

public class ChatMainFragment extends Fragment 
				implements OnClickListener,OnAvaterListener{

	private static final String TAG = LogUtil
				.makeLogTag(ChatMainFragment.class);
	private static final String PRE = "ChatMainFragment--";

	private static String SEPARATOR = "  ";
	private java.text.DateFormat mDateFormat;
	
	private OnClickListener onClickAccountInf = null;
	private OnClickListener onClickHistory = null;
	
	private ListView mListView;
	
	private EditText mInput;
	private TextView mTitleTextView;
	
	private Activity mActivity = null;
	
	private ChatMainAdapter mListAdapter = null;
	
	private MessageRecord mMessageRecord;
	private String mTableName = null;
	private int columnIndexId = -1;
	private int columnIndexFromId = -1;
	private int columnIndexContent = -1;
	private int columnIndexDate = -1;
	
	private long mMsgStartId;
	
	private String mYourJid;
	private String mMyJid;
	
	private Drawable mMyAvater;
	private Drawable mYourAvater;
	private Boolean mIsPresence = true;
	
	private OnAvater mOnAvater;

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
		mTitleTextView.setText(mYourJid);
		
		mListView = (ListView)v.findViewById(R.id.listView);
		mInput = (EditText)v.findViewById(R.id.mchat_input);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
		
		mDateFormat = new SimpleDateFormat("HH:mm:ss");		
		
		mListAdapter = new ChatMainAdapter(mActivity,mMyJid);		
		initAdapter();		
		
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
		Log.d(TAG, PRE + "onDestroy");
		if(mMessageRecord != null){
			mMessageRecord.close();
		}
		super.onDestroy();
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
	// when change chat user, you should initial adapter
	private void initAdapter(){
		
		if(mActivity == null || mListAdapter==null){
			return;
		}
		
		mMessageRecord = new MessageRecord(mActivity, mTableName);	
		if(mMsgStartId == 0){
			mMsgStartId = mMessageRecord.getMaxId() + 1;
			RosterDataBase roster = new RosterDataBase(mActivity,
            		mMyJid);
            roster.updateColumn(mYourJid, RosterDataBase.NEW_MSG_START_ID, mMsgStartId);
            roster.close();
		}
		Cursor c = mMessageRecord.queryItems(mMsgStartId, -1, false);
		
		if(columnIndexFromId == -1){
			columnIndexFromId = c.getColumnIndex(MessageRecord.FROM);
			columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
			columnIndexId = c.getColumnIndex(MessageRecord.ID);
			columnIndexDate = c.getColumnIndex(MessageRecord.DATE);
			
		}

		int n = c.getCount();		
		Long date;		
		c.moveToFirst();
		for(int i=0; i<n; i++){
			ChatMessage msg = new ChatMessage();
			msg.id = c.getInt(columnIndexId);
			msg.jid = c.getString(columnIndexFromId);
			msg.content = c.getString(columnIndexContent);
			
			date = Long.valueOf(c.getString(columnIndexDate));			
			msg.nickName = msg.jid + SEPARATOR + mDateFormat.format(new Date(date));
			
			mListAdapter.addData(msg, false);
			c.moveToNext();
		}
		updateState();
		updatePresence();
		
		mListAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getLastVisiblePosition()); // »¬¶¯µ×²¿
		
		
	}
	// when a new message come, update list
	public void updateList(){
		updateState();
		
		long nStartId = mListAdapter.getLastId();
		if(nStartId == 0){
			nStartId = mMsgStartId;
		}else{
			nStartId++;
		}
		
		Cursor c= mMessageRecord.queryItems(nStartId, -1, false);
		
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
			
			ChatMessage msg = new ChatMessage();
			msg.id = c.getInt(columnIndexId);
			msg.jid = c.getString(columnIndexFromId);
			msg.content = c.getString(columnIndexContent);
			msg.nickName = msg.jid + SEPARATOR + mDateFormat.format(new Date(date));
			
			mListAdapter.addData(msg, false);
			c.moveToNext();
		}
		mListAdapter.notifyDataSetChanged();
	}
	// change chat user
	public void setTableName(String tableName, int msgStartId, 
			String toAccount, String myAccount){
		mTableName = tableName;
		mMsgStartId = msgStartId;
		mMyJid = myAccount;
		mYourJid = toAccount;
		if(mListAdapter != null){
			mListAdapter.removeAll();
		}
		if(mTitleTextView != null){
			mTitleTextView.setText(mYourJid);
		}
		initAdapter();	
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.header_left:
			if(onClickAccountInf != null){
				onClickAccountInf.onClick(v);
			}
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
		mMessageRecord.insert(mMyJid, mYourJid, 
				mInput.getText().toString(), String.valueOf(date) );		
		updateList();
		
		// TODO change communication way
		Intent intent = new Intent(PushServiceUtil.ACTION_SERVICE_MESSAGE);
        intent.putExtra(PushServiceUtil.MESSAGE_TYPE, "chat");
        intent.putExtra(PushServiceUtil.MESSAGE_TOWHOS, mYourJid);
        intent.putExtra(PushServiceUtil.MESSAGE_CONTENT, mInput.getText().toString());
        mActivity.startService(intent);
       
        mInput.setText("");	
	}

	@Override
	public void avater(String avaterJid, Drawable avater) {
		updateState();
		if(mListAdapter != null){
			mListAdapter.notifyDataSetChanged();
		}
				
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
	public void notifyDataSetChanged(){
		mListAdapter.notifyDataSetChanged();
	}
	// setter
	//-------------------the begin of setter------------------------------------
	public void setOnClickAccountInf(OnClickListener onClickAccountInf) {
		this.onClickAccountInf = onClickAccountInf;
	}

	public void setOnClickHistory(OnClickListener onClickHistory) {
		this.onClickHistory = onClickHistory;
	}
		
	public void setOnAvater(OnAvater onAvater){
		mOnAvater = onAvater;
	}
	//-------------------the end of setter------------------------------------
	private class ChatMessage{
		public Integer id;
		public String jid;
		public String nickName;
		public String content;
	}
	public class ChatMainAdapter extends BaseAdapter {

		
		private static final String PRE = "ChatMainAdapter--";
	    
		//private Context context;
		private LayoutInflater mInflater;
		private String mMyJid;	
		
		LinkedList<ChatMessage> messages = new LinkedList<ChatMessage>();
		
		public ChatMainAdapter(Context context, String myJid){
			mMyJid = myJid;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
		}
		public void removeAll(){			
			messages = new LinkedList<ChatMessage>();
		}
		public void addData(ChatMessage msg,boolean isFirst){			
			if(isFirst == false){			
				messages.addLast(msg);				
			} else {			
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
		
		@Override
		public int getCount() {
			int n = messages.size();		
			Log.d(TAG, PRE + "getCount--" + n);
			return n;
		}
		
		@Override
		public int getItemViewType(int position) {	
			ChatMessage msg = messages.get(position);
			if(msg.jid.startsWith(mMyJid)){
				return 0;
			}
			return 1;
		}
		@Override
		public int getViewTypeCount() {
			return 2;
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
			Log.d(TAG, PRE + "getItem");
			return null;
		}

		@Override
		public long getItemId(int position) {
			Log.d(TAG, PRE + "getItemId");		
			if(messages.isEmpty() || position >= messages.size()){
				return 0;
			}
			return messages.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, PRE + "getView position=" + position);
			
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
			
			
			TextView tvName = (TextView)v.findViewById(R.id.tv_chat_item_name_time);
			tvName.setText(msg.nickName);
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
			TextView content = (TextView)v.findViewById(R.id.tv_chat_item_content);
			content.setText(msg.content);
			return v;
		}		
	}	
}
