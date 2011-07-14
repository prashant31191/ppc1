package com.openims.view.pushContent;


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

import com.smit.EasyLauncher.R;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.pushContent.PushContentListFragment.DownloadAsyncTask;

public class PushCursorAdapter extends CursorAdapter{

	private static final String TAG = LogUtil.makeLogTag(PushServiceReceiver.class);
    private static final String PRE = PushServiceReceiver.class.getSimpleName()+"--";
       
	private LayoutInflater mInflater;
	
	private String uread = null;
	private String read = null;
	private String downloadFail = null;
	private String downloadFinish = null;
	private String downloadStop = null;
	private String download = null;
	private String checkout = null;	
	
	private int nColTitle = 0;
	private int nColContent = 0;
	private int nColTime = 0;
	private int nColType = 0;
	private int nColSize = 0;
	private int nColStatus = 0;
	private int nColPath = 0;
	
	private OnClickListener clickListener = null;	
	private Context context;
	private Map<Integer,DownloadAsyncTask> taskMap = null;
	
	// the status of action button
	public static final int STATUS_VIEW = 0;
	public static final int STATUS_DOWNLOAD = 1;
	public static final int STATUS_DOWNLOAD_CANCEL = 2;
	
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
		this.context = context;;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);		
		Log.i(TAG, PRE + "getView");		
		return view;
	}
	/**
	 * 数据的加载
	 */
	@Override
	public void bindView(View view, Context content, Cursor cursor) {
		long startTime = System.currentTimeMillis();
		
		boolean bDownLoad = false;
		String type = cursor.getString(getnColType());
		ImageView imageView = (ImageView)view.findViewById(R.id.iv_pushcontent_type);
		
		if(PushServiceUtil.DEFAULTID_TEXT.equalsIgnoreCase(type)){
			imageView.setImageResource(R.drawable.text);
		}else if(PushServiceUtil.DEFAULTID_URL.equalsIgnoreCase(type)){
			imageView.setImageResource(R.drawable.www);
		}else if(PushServiceUtil.DEFAULTID_PICTURE.equalsIgnoreCase(type)){
			imageView.setImageResource(R.drawable.picture);
			bDownLoad = true;
		}else if(PushServiceUtil.DEFAULTID_VIDEO.equalsIgnoreCase(type)){
			imageView.setImageResource(R.drawable.video);
			bDownLoad = true;
		}else if(PushServiceUtil.DEFAULTID_AUDIO.equalsIgnoreCase(type)){
			imageView.setImageResource(R.drawable.music);
			bDownLoad = true;
		}else if(PushServiceUtil.DEFAULTID_STORY.equalsIgnoreCase(type)){
			imageView.setImageResource(R.drawable.story);
		}else{
			imageView.setImageResource(R.drawable.icon);
		}
		
		TextView tvTitle = (TextView)view.findViewById(
				R.id.tv_pushcontent_title);
		tvTitle.setText(cursor.getString(getnColTitle())) ;
		
		TextView tvContent = (TextView)view.findViewById(
				R.id.tv_pushcontent_content);
		tvContent.setText(cursor.getString(getnColContent())) ;
		
		TextView  tvStatus = (TextView)view.findViewById(
				R.id.tv_pushcontent_status);
		String status = cursor.getString(getnColStatus());
		tvStatus.setText(status); // TODO 增加下载中会是一个bug
		
		if(status!=null && getUread().endsWith(status)){
			tvStatus.setTextColor(0xFFFF0000);
		}else{
			tvStatus.setTextColor(0xFF0000FF);
		}
		
		TextView tvSize = (TextView)view.findViewById(
				R.id.tv_pushcontent_size);
		tvSize.setText(cursor.getString(getnColSize()));
		
		TextView tvTime = (TextView)view.findViewById(
				R.id.tv_pushcontent_time);
		tvTime.setText(cursor.getString(getnColTime()));		
		
		int id = (int)getItemId(cursor.getPosition());
		Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);
		btn.setOnClickListener(clickListener);
		btn.setTag(cursor.getPosition()); // 记录当前cursor的位置		
		btn.setTag(R.string.position, id); // 响应消息是可以知道那里过来的		
		btn.setTag(R.string.btn_type, STATUS_VIEW);
		Log.i(TAG,PRE + "cursor Position" + cursor.getPosition());
		Log.i(TAG,PRE + "item id:" + id);
		
		DownloadAsyncTask task = getTaskMap().get(id);// 以数据库的ID作为唯一的标准
		if(task != null){
			// 重新写，因为删除操作会改变position，显示不正常
			task.setPosition(cursor.getPosition()); 
			if(task.isFinish()){
				btn.setText(R.string.view);
			}else{
				tvSize.setText("总大小: " + cursor.getString(getnColSize()) +
						" 已经下载: " + task.getFinishSize());
				btn.setText(R.string.stopDownload);
				btn.setTag(R.string.btn_type, STATUS_DOWNLOAD_CANCEL);
			}
			
		}else if(cursor.getString(getnColPath()).endsWith("null") && bDownLoad){			
			btn.setText(getDownload());
			btn.setTag(R.string.btn_type, STATUS_DOWNLOAD);
		}else {
			btn.setText(R.string.view);
		}
		 Log.d(TAG, "bind view time："
                 + (System.currentTimeMillis() - startTime)
                 + " ms");
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

	public Map<Integer, DownloadAsyncTask> getTaskMap() {
		if(taskMap == null){
			taskMap= new HashMap<Integer,DownloadAsyncTask>();
		}
		return taskMap;
	}	
	public boolean deleteTaskMap(Integer id){
		getTaskMap().remove(id);
		return true;
	}
	public String getUread() {
		if(uread == null){
			uread = context.getResources().getString(R.string.pushcontent_uread);
		}
		return uread;
	}
	
	public String getRead() {
		if(read == null){
			read = context.getResources().getString(R.string.pushcontent_read);		
		}
		return read;
	}
	
	public String getDownloadFail() {
		if(downloadFail == null){
			downloadFail = context.getResources().getString(
					R.string.pushcontent_downloadFail);		
		}
		return downloadFail;
	}

	public String getDownloadFinish() {
		if(downloadFinish == null){
			downloadFinish = context.getResources().getString(
					R.string.pushcontent_finishDownload);		
		}
		return downloadFinish;
	}	

	public String getDownload() {
		if(download == null){
			download = context.getResources().getString(R.string.download);		
		}
		return download;
	}
	
	public String getDownloadStop() {
		if(downloadStop == null){
			downloadStop = context.getResources().getString(R.string.stopDownload);		
		}
		return downloadStop;
	}	

	public String getCheckout() {
		if(checkout == null){
			checkout = context.getResources().getString(R.string.view);		
		}
		return checkout;
	}

	public int getnColTitle() {
		if(nColTitle == 0){
			nColTitle = getCursor().getColumnIndex(PushContentDB.FLAG);
		}
		return nColTitle;
	}

	public int getnColContent() {
		if(nColContent == 0){
			nColContent = getCursor().getColumnIndex(PushContentDB.CONTENT);
		}
		return nColContent;
	}
	public int getnColSize() {
		if(nColSize == 0){
			nColSize = getCursor().getColumnIndex(PushContentDB.SIZE);
		}
		return nColSize;
	}
	public int getnColTime() {
		if(nColTime == 0){
			nColTime = getCursor().getColumnIndex(PushContentDB.TIME);
		}
		return nColTime;
	}

	public int getnColType() {
		if(nColType == 0){
			nColType = getCursor().getColumnIndex(PushContentDB.TYPE);
		}
		return nColType;
	}

	public int getnColStatus() {
		if(nColStatus == 0){
			nColStatus = getCursor().getColumnIndex(PushContentDB.STATUS);
		}
		return nColStatus;
	}

	public int getnColPath() {
		if(nColPath == 0){
			nColPath = getCursor().getColumnIndex(PushContentDB.LOCAL_PATH);
		}
		return nColPath;
	}	
}
