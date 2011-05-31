package com.openims.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.openims.R;
import com.openims.downloader.DownloadTask;
import com.openims.downloader.DownloadTaskListener;
import com.openims.downloader.DownloadThread;
import com.openims.downloader.DownloadThreadFactory;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.FileOperation;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.widgets.ActionItem;
import com.openims.widgets.BigToast;
import com.openims.widgets.QuickAction;

/**
 * 这是用来显示推送过来的所有的用户内容，用list来显示
 * @author ANDREW CHAN
 * 
 */
public class PushContentListFragment extends ListFragment implements OnClickListener  {

	private static final String TAG = LogUtil
			.makeLogTag(PushContentListFragment.class);
	private static final String PRE = "PushContentListFragment:";


	private PushCursorAdapter pushAdapter = null;
	private PushContentDB pushContentDB = null;
	
	private DownloadThreadFactory downloadFactory = null;
	private DownloadThread thread = null;
	private Handler mainHandler = null;
	
	private String picPath = "sdcard/pushFile/picture/";
	private String videoPath = "sdcard/pushFile/video/";
	private String audioPath = "sdcard/pushFile/audio/";
	
	// pop up menu		 	
	private ActionItem deleteAction = null;
	private ActionItem viewAction = null;		
	private ActionItem cancelAction = null;	
	
	@Override
	public void onAttach(Activity activity) {
		Log.e(TAG, PRE + "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(downloadFactory == null){
			downloadFactory = new DownloadThreadFactory();
			thread = downloadFactory.getThread(null);
			thread.start();
		}
		if(mainHandler == null){
			mainHandler = new Handler();
		}
		if(pushContentDB == null)
			pushContentDB = new PushContentDB(this.getActivity());	
		if(pushAdapter == null)
		pushAdapter = new PushCursorAdapter(this.getActivity(),
				pushContentDB.queryItems(),true,this);
		
		this.setListAdapter(pushAdapter);
		
		// delay init			
		//thread.start();	
		
	}
	
	private String getResString(int id){
		try{
			return this.getActivity().getResources().getString(id);
		}catch(NotFoundException e){
			e.printStackTrace();
		}
		return "null";
		
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, final long id) {	
					
		TextView tv = (TextView)view.findViewById(R.id.tv_pushcontent_status);
		if(pushAdapter.getRead().endsWith(tv.getText().toString()) == false){
			pushContentDB.updateStatus(id, pushAdapter.getRead());			
			pushAdapter.getCursor().requery();	//FIXME update cursor 但是不是最好的方法，可以自己同步数据吧
			return;
		}
		
		final QuickAction mQuickAction 	= new QuickAction(view);
		mQuickAction.animateTrack(false);
		final String text				= "title";		
		Log.i(TAG,PRE + "onListItemClick ID : " + id);
		final int width = l.getMeasuredWidth();
		getDeleteAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mQuickAction.dismiss();
				pushContentDB.deleteItem(String.valueOf(id));
				Toast t = BigToast.makeText(getActivity(),getResString(R.string.delsuccess), 
						Toast.LENGTH_LONG);	
				WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
				
				int n = (windowManager.getDefaultDisplay().getWidth() - width)/2;				
				t.setGravity(Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
				t.setMargin(0.35f, 0.2f);
				t.show();
				
				pushAdapter.getCursor().requery();
			}
		});

		getViewAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Toast t = BigToast.makeText(getActivity(),"view " + text, 
						Toast.LENGTH_LONG);	
				t.show();
		    	
				mQuickAction.dismiss();
			}
		});
		
		getCancelAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Cancel " + text, Toast.LENGTH_SHORT).show();
		    	
				mQuickAction.dismiss();
			}
		});
		
		mQuickAction.addActionItem(deleteAction);
		mQuickAction.addActionItem(viewAction);
		mQuickAction.addActionItem(cancelAction);
		
		mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
		
		mQuickAction.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				
			}
		});
		
		mQuickAction.show();
		
	}
	
	
	public void updateList(){
		pushAdapter.getCursor().requery();
	}
	public void viewImage(String path){
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		File file = new File(path);  
		intent.setDataAndType(Uri.fromFile(file), "image/*"); 	
		startActivity(intent); 
	}

	public void toast(){
		
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
		if(pushAdapter != null){
			pushAdapter.cleanUp();
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
		
		Integer cursor_position = (Integer)v.getTag();
		Cursor cursor = pushAdapter.getCursor();
		Log.e(TAG, PRE + "onClick = " + cursor_position);
		
		String text = ((Button)v).getText().toString();			
		cursor.moveToPosition(cursor_position);
		String url = cursor.getString(pushAdapter.getnColContent());
		
		if(pushAdapter.getDownload().endsWith(text)){
			// down load file
			FileOperation.makedir(picPath);
			String fileName = FileOperation.getFileName(url);
			
			TaskListener listener = new TaskListener();
			Integer id = (Integer)v.getTag(R.string.position);
			listener.setId(id);
			
			listener.setPosition(cursor_position);
			listener.setDownloadPath(picPath + fileName);
			pushAdapter.getTaskListenerMap().put(id, listener);
			
			DownloadTask task = new DownloadTask(url,picPath + fileName,
					mainHandler,listener);
			
			thread.enqueueDownload(task);
			((Button)v).setText(R.string.beginDownload);
			
		} else if(pushAdapter.getCheckout().endsWith(text)) {
			// view information
			if(PushServiceUtil.DEFAULTID_PICTURE.endsWith(
					cursor.getString(pushAdapter
							.getnColType() ) ) ){
				Log.i(TAG, PRE + "URL:" + cursor.getString(pushAdapter.getnColPath()));
				viewImage(cursor.getString(pushAdapter.getnColPath()));
			}
		}
	}

	public ActionItem getDeleteAction() {
		if(deleteAction == null){
			deleteAction = new ActionItem();
			deleteAction.setTitle(getActivity().getResources().getString(R.string.delete));
			deleteAction.setIcon(getResources().getDrawable(R.drawable.ic_add));			
		}
		return deleteAction;
	}
	public ActionItem getViewAction() {
		if(viewAction == null){
			viewAction = new ActionItem();
			viewAction.setTitle(getActivity().getResources().getString(R.string.view));
			viewAction.setIcon(getResources().getDrawable(R.drawable.ic_accept));			
		}
		return viewAction;
	}
	public ActionItem getCancelAction() {
		if(cancelAction == null){
			cancelAction = new ActionItem();
			cancelAction.setTitle(getActivity().getResources().getString(R.string.cancel));
			cancelAction.setIcon(getResources().getDrawable(R.drawable.ic_accept));					
		}
		return cancelAction;
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
					btn.setText(pushAdapter.getDownload() + ":" + nFinishSize);
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
