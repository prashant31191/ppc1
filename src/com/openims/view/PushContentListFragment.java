package com.openims.view;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.openims.R;
import com.openims.downloader.DownloadTask;
import com.openims.downloader.DownloadTaskListener;
import com.openims.downloader.DownloadThread;
import com.openims.downloader.DownloadThreadFactory;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.FileOperation;
import com.openims.utility.LogUtil;

/**
 * 这是用来显示推送过来的所有的用户内容，用list来显示
 * @author ANDREW CHAN
 * 
 */
public class PushContentListFragment extends ListFragment implements OnClickListener  {

	private static final String TAG = LogUtil
			.makeLogTag(PushContentListFragment.class);
	private static final String PRE = "PushContentListFragment:";

	private String read;
	private Cursor cursor;
	private int nContent;
	private String download;
	
	private PushCursorAdapter adapter;
	private DownloadThreadFactory downloadFactory = new DownloadThreadFactory();
	private DownloadThread thread = downloadFactory.getThread(null);
	
	 private String picPath = "sdcard/pushFile/picture/";
	    private String videoPath = "sdcard/pushFile/video/";
	    private String audioPath = "sdcard/pushFile/audio/";
	    
	private Handler mainHandler = new Handler();

	private PushContentDB pushContentDB;
	@Override
	public void onAttach(Activity activity) {
		Log.e(TAG, PRE + "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thread.start();
		
		pushContentDB = new PushContentDB(this.getActivity());
		read = getActivity().getResources().getString(R.string.pushcontent_read);
		cursor = pushContentDB.queryItems();

		adapter = new PushCursorAdapter(this.getActivity(),
				cursor,true,this);
		this.setListAdapter(adapter);
		
		nContent = cursor.getColumnIndex(PushContentDB.CONTENT);
		download = this.getActivity().getResources().getString(R.string.download);
		
	}
	
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {		
		
		TextView tv = (TextView)v.findViewById(R.id.tv_pushcontent_status);
		if(read.endsWith(tv.getText().toString()) == false){
			pushContentDB.updateStatus(id, read);			
			cursor.requery();	//FIXME update cursor 但是不是最好的方法，可以自己同步数据吧
			
		}
		
	}
	
	public void updateList(){
		cursor.requery();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, PRE + "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e(TAG, PRE + "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.e(TAG, PRE + "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.e(TAG, PRE + "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.e(TAG, PRE + "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e(TAG, PRE + "onStop");		
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.e(TAG, PRE + "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, PRE + "onDestroy");
		if(adapter != null){
			adapter.cleanUp();
		}
		cleanUp();
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.e(TAG, PRE + "onDetach");
		super.onDetach();
	}
	
	// 清理一些垃圾
	public void cleanUp(){
		Log.i(TAG,TAG + "cleanUp");
		if(thread != null){
			thread.requestStop();
			thread = null;
		}
	}
	@Override
	public void onClick(final View v) {		
		Integer coursor_position = (Integer)v.getTag();
		
		Log.e(TAG, PRE + "onClick = " + coursor_position);
		String text = ((Button)v).getText().toString();			
		cursor.moveToPosition(coursor_position);
		String url = cursor.getString(nContent);
		if(download.endsWith(text)){
			// down load file
			FileOperation.makedir(picPath);
			String fileName = FileOperation.getFileName(url);
			TaskListener listener = new TaskListener();
			listener.setId(cursor.getInt(cursor.getColumnIndex(PushContentDB.INDEX)));
			Integer position = (Integer)v.getTag(R.string.position);
			listener.setPosition(position);
			listener.setDownloadPath(picPath + fileName);
			adapter.getTaskListenerMap().put(coursor_position, listener);
			DownloadTask task = new DownloadTask(url,picPath + fileName,
					mainHandler,listener);
			
			thread.enqueueDownload(task);
			((Button)v).setText(R.string.beginDownload);
			
		} else {
			// view information
		}
	}

	public class TaskListener implements DownloadTaskListener {

		private int id = 0; // database key column
		private int position = 0;
		private int nFinishSize;
		private int nTotalSize;
		private boolean bFinish = false;
		private String downloadPath = null;
		
		@Override
		public void finish() {
			setPath();
			bFinish = true;
			updateItem();
		}

		@Override
		public void finish(int nFinishSize, int nTotalSize) {
			this.nFinishSize = nFinishSize;
			updateItem();
		}
		
		private void setPath(){
			pushContentDB.updatePath(id, downloadPath);
		}
		private void updateItem(){
			View view = getListView().getChildAt(
					position - getListView().getFirstVisiblePosition());
			if(view != null){
				Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);
				if(bFinish==true){
					btn.setText(R.string.view);
				}else{
					btn.setText(download + ":" + nFinishSize);
				}
					
			}else{
				Log.e(TAG, PRE + "can't get view");
			}
		}
		
		public void setId(int id) {
			this.id = id;
		}
		public boolean getbFinish(){
			return bFinish;
		}
		public int getnFinishSize() {
			return nFinishSize;
		}
		public void setnFinishSize(int nFinishSize) {
			this.nFinishSize = nFinishSize;
		}
		public void setPosition(int position) {
			this.position = position;
		}

		public void setDownloadPath(String downloadPath) {
			this.downloadPath = downloadPath;
		}	
		
		
		
	}

	

}
