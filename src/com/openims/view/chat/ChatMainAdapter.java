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

public class ChatMainAdapter extends BaseAdapter {

	
	private static final String TAG = LogUtil.makeLogTag(ChatMainAdapter.class);
    private static final String PRE = "ChatMainAdapter--";
    
	//private Context context;
	private LayoutInflater mInflater;
	private String mMyName;
	private int blackColor;
	private int blueColor;
	
	LinkedList<String> messageList = new LinkedList<String>();
	LinkedList<String> nameList = new LinkedList<String>();
	LinkedList<String> portraitList = new LinkedList<String>();
	LinkedList<Boolean> presentList = new LinkedList<Boolean>();
	LinkedList<Integer> idList = new LinkedList<Integer>();
	
	public ChatMainAdapter(Context context, String myName){
		mMyName = myName;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
		blackColor = context.getResources().getColor(R.color.light_black);
		blueColor = context.getResources().getColor(R.color.main_blue);
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
			idList.addLast(id);
			nameList.addLast(name);
			messageList.addLast(message);
			
		} else {			
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
		Log.d(TAG, PRE + "getCount--" + n);
		return n;
	}

	/**
	 * Masks ViewType so the AdapterView replaces the "Pending" row when new
	 * data is loaded.
	 */
	@Override
	public int getItemViewType(int position) {
		
		Log.d(TAG, PRE + "getItemViewType");
		return 1;
	}
	@Override
	public int getViewTypeCount() {
		Log.d(TAG, PRE + "getViewTypeCount");
		return 2;
	}
	@Override
	public boolean areAllItemsEnabled() {
		Log.d(TAG, PRE + "areAllItemsEnabled");
		return true;
	}
	
	@Override
	public boolean isEnabled(int position) {
		//Log.d(TAG, PRE + "isEnabled");
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
		if(idList.isEmpty() || position > idList.size()){
			return 0;
		}
		return idList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, PRE + "getView position=" + position);
		
		View v;
		if (convertView == null) {
            v = mInflater.inflate(R.layout.multi_chat_message_item, null);
        } else {
            v = convertView;
        }
		String name = nameList.get(position);
		TextView tvName = (TextView)v.findViewById(R.id.tv_chat_item_name_time);
		tvName.setText(name);
		if(name.startsWith(mMyName)){
			tvName.setTextColor(blueColor);
		}else{
			tvName.setTextColor(blackColor);
		}
		TextView content = (TextView)v.findViewById(R.id.tv_chat_item_content);
		content.setText(messageList.get(position));
		return v;
	}

}
