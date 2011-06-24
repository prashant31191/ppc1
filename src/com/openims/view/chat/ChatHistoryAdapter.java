package com.openims.view.chat;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openims.R;
import com.openims.utility.LogUtil;

abstract public class ChatHistoryAdapter extends BaseAdapter {

	abstract protected boolean cacheInBackground(Boolean isUp) throws Exception;
	abstract protected void appendCachedData(Boolean isUp);
	abstract protected void move(Boolean isUp);
	
	private static final String TAG = LogUtil.makeLogTag(ChatHistoryAdapter.class);
    private static final String PRE = "ChatHistoryAdapter--";
       
    
	private AtomicBoolean keepOnAppending=new AtomicBoolean(false);
	private AtomicBoolean keepOnAppendingUp=new AtomicBoolean(false);
	
	//private Context context;
	private LayoutInflater mInflater;
	private View pendingView=null;
	private View pendingViewUp=null;
	
	private RotateAnimation rotate=null;
	
	private final static int MAX_SIZE = 20;
	LinkedList<String> messageList = new LinkedList<String>();
	LinkedList<String> nameList = new LinkedList<String>();
	LinkedList<String> portraitList = new LinkedList<String>();
	LinkedList<Integer> idList = new LinkedList<Integer>();
	
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
	
	public void removeAll(){
		messageList = new LinkedList<String>();
		nameList = new LinkedList<String>();
		portraitList = new LinkedList<String>();
		idList = new LinkedList<Integer>();
	}
	public void addData(Integer id, String name, String message,boolean isFirst){
		Log.e(TAG,PRE + "add data id = " + id);
		if(isFirst == false){
			if(idList.size() > MAX_SIZE){
				idList.removeFirst();
				nameList.removeFirst();
				messageList.removeFirst();
			}
			idList.addLast(id);
			nameList.addLast(name);
			messageList.addLast(message);
		} else {
			if(idList.size() > MAX_SIZE){
				idList.removeLast();
				nameList.removeLast();
				messageList.removeLast();
				keepOnAppending.set(true);
			}
			idList.addFirst(id);
			nameList.addFirst(name);
			messageList.addFirst(message);
		}
			
	}
	public int getFirstId(){
		if(idList.isEmpty())
			return 0;
		return idList.getFirst();
	}
	public int getLastId(){
		if(idList.isEmpty())
			return 0;
		return idList.getLast();
	}
	
	@Override
	public int getCount() {
		int n = idList.size();
		if (keepOnAppending.get()) {
			n = n + 1;		// one more for "pending"
		}
		if (keepOnAppendingUp.get()) {
			n = n + 1;		// one more for "pending"
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
		return 1;
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public boolean areAllItemsEnabled() {
		return true;
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
		return idList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, PRE + "getView position=" + position);
		
		// show footer waiting
		if (position==(getCount()-1) &&
				keepOnAppending.get()) {
			
			if (pendingView==null) {				
				pendingView=getPendingView(parent);
				new AppendTask().execute(false);
				Log.d(TAG, PRE + "start to load data");
			}
			return(pendingView);
		}
		// show header waiting
		if(position==0 && keepOnAppendingUp.get()){
			if(pendingViewUp == null){
				pendingViewUp=getPendingView(parent);
				new AppendTask().execute(true);
				Log.d(TAG, PRE + "start to load back up data");				
			}
			return pendingViewUp;
		}
		if(keepOnAppendingUp.get()){
			position = position - 1; 	//被提示占了一个位置，数据往后退
			Log.d(TAG, PRE + "减少一个  position=" + position);
		}
		// normal situation
		View v;
		if (convertView == null) {
            v = mInflater.inflate(R.layout.multi_chat_message_item, null);
        } else {
            v = convertView;
        }
		TextView name = (TextView)v.findViewById(R.id.tv_chat_item_name_time);
		name.setText(nameList.get(position));
		TextView content = (TextView)v.findViewById(R.id.tv_chat_item_content);
		content.setText(messageList.get(position));
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
	* Called if cacheInBackground() raises a runtime exception,
	* to allow the UI to deal with the exception on the
	* main application thread.
	* @param pendingView View representing the pending row
	* @param e Exception that was raised by cacheInBackground()
	* @return true if should allow retrying appending new data, false otherwise
	*/
	protected boolean onException(View pendingView, Exception e) {
		Log.e("EndlessAdapter", "Exception in cacheInBackground()", e);
		
		return(false);
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
				
				if(mIsUp){
					keepOnAppendingUp.set(cacheInBackground(mIsUp));
				}else{
					keepOnAppending.set(cacheInBackground(mIsUp));
				}
			}
			catch (Exception e) {
				result=e;
			}
			
			return(result);
		}

		@Override
		protected void onPostExecute(Exception e) {
			if (e==null) {
				appendCachedData(mIsUp);
			}
			else {
				if(mIsUp){
					keepOnAppendingUp.set(onException(pendingViewUp, e));
				}else{
					keepOnAppending.set(onException(pendingView, e));
				}
				
			}
			if(mIsUp){
				pendingViewUp=null;
			}else{
				pendingView=null;
			}
			
			notifyDataSetChanged();
			move(mIsUp);
			
		}
	}
	public AtomicBoolean getKeepOnAppending() {
		return keepOnAppending;
	}
	public AtomicBoolean getKeepOnAppendingUp() {
		return keepOnAppendingUp;
	}
	
	
	

}
