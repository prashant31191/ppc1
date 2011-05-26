package com.openims.view;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.openims.R;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.PushContentListFragment.TaskListener;

public class PushCursorAdapter extends CursorAdapter{

	private static final String TAG = LogUtil.makeLogTag(PushServiceReceiver.class);
    private static final String PRE = PushServiceReceiver.class.getSimpleName()+"--";
    

       
	private LayoutInflater mInflater;
	private String uread;
	private String download;
	

	private int nTitle;
	private int nContent;
	private int nTime;
	private int nType;
	private int nSize;
	private int nStatus;
	private int nPath;
	
	private OnClickListener clickListener;	
	
	private Map<Integer,TaskListener> taskListenerMap = null;
	
	/**
	 * 初始化
	 * @param context
	 * @param c
	 * @param autoRequery
	 * @param clickListener 响应button的消息
	 */
	public PushCursorAdapter(Context context, Cursor c, boolean autoRequery,
			OnClickListener clickListener) {
		super(context, c, autoRequery);
		this.clickListener = clickListener;
		mInflater = LayoutInflater.from(context);
		uread = context.getResources().getString(R.string.pushcontent_uread);
		download = context.getResources().getString(R.string.download);		
		nTitle = c.getColumnIndex(PushContentDB.FLAG);
		nContent = c.getColumnIndex(PushContentDB.CONTENT);
		nTime = c.getColumnIndex(PushContentDB.TIME);
		nType = c.getColumnIndex(PushContentDB.TYPE);
		nSize = c.getColumnIndex(PushContentDB.SIZE);
		nStatus = c.getColumnIndex(PushContentDB.STATUS);
		nPath = c.getColumnIndex(PushContentDB.LOCAL_PATH);		
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);		
		Log.i(TAG, PRE + "getView");
		Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);		
		btn.setTag(R.string.position, position);
		return view;
	}
	/**
	 * 数据的加载
	 */
	@Override
	public void bindView(View view, Context content, Cursor cursor) {
		boolean bDownLoad = false;
		String type = cursor.getString(nType);
		ImageView imageView = (ImageView)view.findViewById(R.id.iv_pushcontent_type);
		if(PushServiceUtil.DEFAULTID_TEXT.endsWith(type)){
			imageView.setImageResource(R.drawable.text);
		}else if(PushServiceUtil.DEFAULTID_URL.endsWith(type)){
			imageView.setImageResource(R.drawable.www);
		}else if(PushServiceUtil.DEFAULTID_PICTURE.endsWith(type)){
			imageView.setImageResource(R.drawable.picture);
			bDownLoad = true;
		}else if(PushServiceUtil.DEFAULTID_VIDEO.endsWith(type)){
			imageView.setImageResource(R.drawable.video);
			bDownLoad = true;
		}else if(PushServiceUtil.DEFAULTID_STORY.endsWith(type)){
			imageView.setImageResource(R.drawable.story);
		}else{
			imageView.setImageResource(R.drawable.icon);
		}
		
		TextView tvTitle = (TextView)view.findViewById(
				R.id.tv_pushcontent_title);
		tvTitle.setText(cursor.getString(nTitle)) ;
		
		TextView tvContent = (TextView)view.findViewById(
				R.id.tv_pushcontent_content);
		tvContent.setText(cursor.getString(nContent)) ;
		
		TextView  tvStatus = (TextView)view.findViewById(
				R.id.tv_pushcontent_status);
		String status = cursor.getString(nStatus);
		tvStatus.setText(status);
		
		if(status!=null && uread.endsWith(status)){
			tvStatus.setTextColor(0xFFFF0000);
		}else{
			tvStatus.setTextColor(0xFF0000FF);
		}
		
		TextView tvSize = (TextView)view.findViewById(
				R.id.tv_pushcontent_size);
		tvSize.setText(cursor.getString(nSize));
		
		TextView tvTime = (TextView)view.findViewById(
				R.id.tv_pushcontent_time);
		tvTime.setText(cursor.getString(nTime));		
		
		Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);
		btn.setOnClickListener(clickListener);
		btn.setTag(cursor.getPosition());
		
		Log.i(TAG,PRE + "cursor Position" + cursor.getPosition());

		TaskListener listener = getTaskListenerMap().get(cursor.getPosition());
		if(listener != null){
			if(listener.getbFinish()){
				btn.setText(R.string.view);
			}else{
				btn.setText(download + ":" + listener.getnFinishSize());
			}
			
		}else if(cursor.getString(nPath).endsWith("null") && bDownLoad){			
			btn.setText(download);
		}else {
			btn.setText(R.string.view);
		}
		
		Log.i(TAG, PRE + "bindView");	

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup root) {
		View view = mInflater.inflate(R.layout.pushcontent_list_item, root, false);				
		Log.i(TAG, PRE + "newView");
		return view;
	}
	
	// 清理一些垃圾
	public void cleanUp(){
		Log.i(TAG,TAG + "cleanUp");		
	}
	
	protected void finalize() throws Throwable {
		Log.i(TAG,TAG + "finalize");
	     try {
	         cleanUp();       
	     } finally {
	         super.finalize();
	     }
	 }

	public Map<Integer, TaskListener> getTaskListenerMap() {
		if(taskListenerMap == null){
			taskListenerMap= new HashMap<Integer,TaskListener>();
		}
		return taskListenerMap;
	}	
	
}
