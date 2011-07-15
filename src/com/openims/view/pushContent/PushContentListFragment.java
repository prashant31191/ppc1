package com.openims.view.pushContent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.smit.EasyLauncher.R;
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
	
	private String picPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pushFile/picture/";
	private String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pushFile/video/";
	private String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pushFile/audio/";
	private String MEDIA_PIC = "image";
	private String MEDIA_AUDIO = "audio";
	private String MEDIA_VIDEO = "video";
	
	// pop up menu		 	
	private ActionItem deleteAction = null;
	private ActionItem viewAction = null;		
	private ActionItem cancelAction = null;	
	private ActionItem downloadAction = null;
	private ActionItem stopDownloadAction = null;
	
	private boolean close = true;
	
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
		Log.i(TAG,PRE + "onListItemClick ID : " + id);
		
		// set unread to read 
		TextView tv = (TextView)view.findViewById(R.id.tv_pushcontent_status);
		if(pushAdapter.getUread().endsWith(tv.getText().toString())){
			pushContentDB.updateStatus(id, pushAdapter.getRead());			
			pushAdapter.getCursor().requery(); // TODO can be better
			showToast(R.string.pushcontent_read,Toast.LENGTH_SHORT);	
			return;
		}
		
		// Pop up menu
		final QuickAction mQuickAction 	= new QuickAction(view);
		final Button actionBtn = (Button)view.findViewById(R.id.btn_pushcontent_action);
		String text = actionBtn.getText().toString();
		
		getDeleteAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mQuickAction.dismiss();
				pushContentDB.deleteItem(String.valueOf(id));
				showToast(R.string.delsuccess,Toast.LENGTH_LONG);				
				pushAdapter.getCursor().requery();
			}
		});
		mQuickAction.addActionItem(getDeleteAction());
		
		// add difference action in difference situation
		int type = (Integer)actionBtn.getTag(R.string.btn_type);
		
		if(type == PushCursorAdapter.STATUS_DOWNLOAD){
			
			getDownloadAction().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mQuickAction.dismiss();
					Integer cursorPosition = (Integer)actionBtn.getTag();
					actionBtn.setTag(R.string.btn_type,PushCursorAdapter.STATUS_DOWNLOAD_CANCEL);
					btnDownload(cursorPosition, actionBtn);
					
				}
			});
			mQuickAction.addActionItem(getDownloadAction());
			
		} else if(type == PushCursorAdapter.STATUS_VIEW) {
			getViewAction().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mQuickAction.dismiss();
					Integer cursorPosition = (Integer)actionBtn.getTag();
					btnView(cursorPosition);
					
				}
			});
			mQuickAction.addActionItem(getViewAction());
		} else if(type == PushCursorAdapter.STATUS_DOWNLOAD_CANCEL){
			getStopDownloadAction().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int id = (Integer)actionBtn.getTag(R.string.position);
					btnStopDownload(id);					
				}
			});
			mQuickAction.addActionItem(getStopDownloadAction());
			
		}
		getCancelAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mQuickAction.dismiss();
				showToast(R.string.cancel,Toast.LENGTH_LONG);
			}
		});		
		mQuickAction.addActionItem(getCancelAction());
		
		
		
		mQuickAction.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				
			}
		});
		mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
		mQuickAction.show();
		
	}
	
	/**
	 * show notify
	 */
	private void showToast(int text,int duration){
		Toast t = BigToast.makeText(getActivity(),getResString(text), 
				duration);						
		t.setGravity(Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
		t.setMargin(PushServiceUtil.HORIZONTAL_MARGIN, PushServiceUtil.VERTICAL_MARGIN);
		t.show();
	}
	/**
	 * show the detail information in this position
	 * @param position
	 */
	private void btnView(int position){
		
		Cursor cursor = pushAdapter.getCursor();
		cursor.moveToPosition(position);
		
		String url = cursor.getString(pushAdapter.getnColContent());
		String type = cursor.getString(pushAdapter.getnColType());
		
		String localPath = cursor.getString(pushAdapter.getnColPath());
		if(PushServiceUtil.DEFAULTID_PICTURE.endsWith(type) ){
			viewMedia(localPath, MEDIA_PIC);
		} else if(PushServiceUtil.DEFAULTID_VIDEO.endsWith(type) ){				
			viewMedia(localPath, MEDIA_VIDEO);
		} else if(PushServiceUtil.DEFAULTID_AUDIO.endsWith(type) ){				
			viewMedia(localPath, MEDIA_AUDIO);
		} else if(PushServiceUtil.DEFAULTID_URL.endsWith(type) ){				
			Uri uri = Uri.parse(url);
	    	Intent intent = new Intent(Intent.ACTION_VIEW,uri);
	    	getActivity().startActivity(intent);
		} else if(PushServiceUtil.DEFAULTID_TEXT.equalsIgnoreCase(type)){
			View view = getListView().getChildAt(
					position - getListView().getFirstVisiblePosition());
			if(view != null){
				TextView c = (TextView)view.findViewById(R.id.tv_pushcontent_content);				
				if(c.getEllipsize() != null){						
					c.setSingleLine(false);
					c.setEllipsize(null);
				} else {						
					c.setSingleLine(true);
					c.setEllipsize(TruncateAt.MIDDLE);
				}
				
			}
		}
	}
	
	public void viewMedia(String path,String type){
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		File file = new File(path);  
		intent.setDataAndType(Uri.fromFile(file), type + "/*"); 	
		startActivity(intent); 
	}

	public void updateList(){
		pushAdapter.getCursor().requery();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, PRE + "onCreateView");
		View v = inflater.inflate(R.layout.pc_custom_list_empty, container);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e(TAG, PRE + "onActivityCreated");
		close = false;
		super.onActivityCreated(savedInstanceState);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		
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
		close = true;
		pushContentDB.close();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, PRE + "onDestroy");
		close = true;
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
		
		Integer cursorPosition = (Integer)v.getTag();	
		int type = (Integer)v.getTag(R.string.btn_type);
		
		if(type == PushCursorAdapter.STATUS_DOWNLOAD){
			// down load file
			v.setTag(R.string.btn_type,PushCursorAdapter.STATUS_DOWNLOAD_CANCEL);
			btnDownload(cursorPosition, (Button)v);
			
		} else if(type == PushCursorAdapter.STATUS_VIEW) {
			// view information
			btnView(cursorPosition);
		} else if(type == PushCursorAdapter.STATUS_DOWNLOAD_CANCEL){
			int id = (Integer)v.getTag(R.string.position);
			btnStopDownload(id);
		}
	}

	public void btnStopDownload(int id){
		DownloadAsyncTask task = (DownloadAsyncTask)pushAdapter.getTaskMap().get(id);
		if(task != null){
			if(task.cancel(true)){
				showToast(R.string.stopDownload,Toast.LENGTH_LONG);
			}else{
				showToast(R.string.pushcontent_stopFail,Toast.LENGTH_LONG);
			}
		}
	}
	private void btnDownload(int position, final Button v){
		Cursor cursor = pushAdapter.getCursor();
		cursor.moveToPosition(position);
		String url = cursor.getString(pushAdapter.getnColContent());
		String type = cursor.getString(pushAdapter.getnColType());
		String basePath = picPath;
		
		if(PushServiceUtil.DEFAULTID_PICTURE.endsWith(type) ){
			basePath = picPath;
		}else if(PushServiceUtil.DEFAULTID_AUDIO.endsWith(type)){
			basePath = audioPath;
		}else if(PushServiceUtil.DEFAULTID_VIDEO.endsWith(type)){
			basePath = videoPath;
		}
		boolean b = FileOperation.makedir(basePath);
		
		String fileName = FileOperation.getFileName(url);
		
		Integer id = (Integer)v.getTag(R.string.position);
		DownloadAsyncTask task = new DownloadAsyncTask();
		pushAdapter.getTaskMap().put(id, task);
		task.setId(id);
		task.setPosition(position);
		task.execute(url,basePath + fileName);			
	}
	public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer>{
		private static final String PRE = "DownloadAsyncTask:";
		private static final int FAIL = 0;
		private static final int SUCCESS = 1;
		private static final int NONETWORK = 2;
		private int id = 0; 	// database key column
		private int position = 0;
		private int finishSize = 0;
		private int totalSize = 0; // TODO 要求服务器把大小发过来
		private String downloadPath;	
		private boolean finish = false;
		
		/**
		 * params[0] down load URL
		 * params[1] local path to save file
		 */
		@Override
		protected Integer doInBackground(String... params) {
			
			Log.d(TAG, PRE + "url:" + params[0]);
            Log.d(TAG, PRE + "file name:" + params[1]);
            downloadPath = params[1];
            
            int returnCode = SUCCESS;
			FileOutputStream fos = null;
	        try {
	        	URL url = new URL(params[0]);
	            File file = new File(params[1]);
	            fos = new FileOutputStream(file);
	            
	            long startTime = System.currentTimeMillis();
                URLConnection ucon = url.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                
                byte[] data = new byte[10240]; 
                int nFinishSize = 0;
                int nread = 0;
                while( (nread = bis.read(data, 0, 10240)) != -1){
                	fos.write(data, 0, nread);                	
                	nFinishSize += 10240;
                	Thread.sleep( 1 ); // this make cancel method work
                	this.publishProgress(nFinishSize);
                }              
                data = null;    
	            Log.d(TAG, "download ready in"
	                  + ((System.currentTimeMillis() - startTime) / 1000)
	                  + " sec");
	                
	        } catch (IOException e) {
	                Log.d(TAG, PRE + "Error: " + e);
	                returnCode = FAIL;
	        } catch (Exception e){
	        		 e.printStackTrace();       	
	        } finally{
				try {
					if(fos != null)
						fos.close();
				} catch (IOException e) {
					Log.d(TAG, PRE + "Error: " + e);
					e.printStackTrace();
				}
	        }
           
			return returnCode;
		}

		@Override
		protected void onCancelled() {
			/*View view = getListView().getChildAt(
					position - getListView().getFirstVisiblePosition());
			if(view != null){
				Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);
				btn.setText(R.string.stopDownload);
				btn.setTag(R.string.btn_type,PushCursorAdapter.STATUS_DOWNLOAD);
			}*/
			pushContentDB.updateStatus(id, pushAdapter.getDownloadStop());
			pushAdapter.deleteTaskMap(id);
			pushAdapter.getCursor().requery();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			finish = true;
			if(close){
				return;
			}
			View view = getListView().getChildAt(
					position - getListView().getFirstVisiblePosition());
			if(view != null){
				//Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);
				//TextView v = (TextView)view.findViewById(R.id.tv_pushcontent_status);
				switch(result){
				case FAIL:
					pushContentDB.updateStatus(id, pushAdapter.getDownloadFail());
					pushAdapter.deleteTaskMap(id);
					//v.setText(pushAdapter.getDownloadFail());
					//btn.setText(R.string.download);
					//btn.setTag(R.string.btn_type,PushCursorAdapter.STATUS_DOWNLOAD);
					pushAdapter.getCursor().requery();
					showToast(R.string.pushcontent_downloadFail, Toast.LENGTH_SHORT);
					
					break;
				case SUCCESS:
					pushContentDB.updatePath(id, downloadPath);
					pushContentDB.updateStatus(id, pushAdapter.getDownloadFinish());
					
					//btn.setText(R.string.view);
					//btn.setTag(R.string.btn_type,PushCursorAdapter.STATUS_VIEW);					
					//v.setText(R.string.pushcontent_finishDownload);
					pushAdapter.getCursor().requery();
					showToast(R.string.pushcontent_finishDownload, Toast.LENGTH_SHORT);
					break;
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			View view = getListView().getChildAt(
					position - getListView().getFirstVisiblePosition());
			if(view != null){
				Log.i(TAG, PRE + "onPreExecute");
				TextView v = (TextView)view.findViewById(R.id.tv_pushcontent_status);
				v.setText(R.string.pushcontent_beginDownload);
				Button btn = (Button)view.findViewById(R.id.btn_pushcontent_action);
				btn.setText(R.string.stopDownload);
				btn.setTag(R.string.btn_type, PushCursorAdapter.STATUS_DOWNLOAD_CANCEL);
			}
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			finishSize = values[0];
			try{
				if(close == false){
					View view = getListView().getChildAt(
							position - getListView().getFirstVisiblePosition());
					if(view != null){
						TextView tvSize = (TextView)view.findViewById(R.id.tv_pushcontent_size);
						tvSize.setText("总大小: " + totalSize +
								" 已经下载: " + finishSize);
					}		
				}
								
			}catch(IllegalStateException e){
				e.printStackTrace();
				Log.e(TAG,PRE + "view had destory");
			}
			
			super.onProgressUpdate(values);
		}

		public int getId() {
			return id;
		}

		public int getPosition() {
			return position;
		}

		public boolean isFinish() {
			return finish;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setPosition(int position) {
			this.position = position;
		}		

		public void setFinish(boolean finish) {
			this.finish = finish;
		}

		public int getFinishSize() {
			return finishSize;
		}

		public int getTotalSize() {
			return totalSize;
		}

		public void setFinishSize(int finishSize) {
			this.finishSize = finishSize;
		}

		public void setTotalSize(int totalSize) {
			this.totalSize = totalSize;
		}
	}
	//============== below is getter and setter =============================
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
			cancelAction.setTitle(getActivity().getResources()
					.getString(R.string.cancel));
			cancelAction.setIcon(getResources().getDrawable(R.drawable.ic_accept));					
		}
		return cancelAction;
	}
	public ActionItem getDownloadAction() {
		if(downloadAction == null){
			downloadAction = new ActionItem();
			downloadAction.setTitle(getActivity().getResources()
					.getString(R.string.download));
			downloadAction.setIcon(getResources().getDrawable(R.drawable.ic_accept));
		}
		return downloadAction;
	}
	public ActionItem getStopDownloadAction() {
		if(stopDownloadAction == null){
			stopDownloadAction = new ActionItem();
			stopDownloadAction.setTitle(getActivity().getResources()
					.getString(R.string.stopDownload));
			stopDownloadAction.setIcon(getResources().getDrawable(R.drawable.ic_accept));
		}
		return stopDownloadAction;
	}
	
}
