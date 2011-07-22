package com.openims.view.pushContent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.openims.downloader.DownloadInf;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;
import com.openims.view.BaseServiceFragment;
import com.openims.widgets.ActionItem;
import com.openims.widgets.BigToast;
import com.openims.widgets.QuickAction;
import com.smit.EasyLauncher.R;


public class PushContentListFragment extends BaseServiceFragment{

	private static final String TAG = LogUtil.makeLogTag(PushContentListFragment.class);
	private static final String PRE = "PushContentListFragment:";
	
	// the status of action button
	public static final int STATUS_VIEW = 1;
	public static final int STATUS_DOWNLOAD = 2;
	public static final int STATUS_DOWNLOAD_CANCEL = 3;
	
	private String uread = null;
	private String read = null;
	private String downloadFail = null;
	private String downloadFinish = null;
	private String downloadStop = null;
	private String download = null;
	private String checkout = null;	
	
	private int nColIndex = -1;
	private int nColTitle = -1;
	private int nColContent = -1;
	private int nColTime = -1;
	private int nColType = -1;
	private int nColSize = -1;
	private int nColStatus = -1;
	private int nColPath = -1;
	private int nColFlag = -1;


	private PushAdapter pushAdapter = null;
	private PushContentDB pushContentDB = null;

	
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
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	
		initData();		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.pc_custom_list_empty, container);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);		
	}	

	@Override
	public void onDestroy() {
		Log.e(TAG, PRE + "onDestroy");		
		if(pushContentDB != null){
			pushContentDB.close();
		}	
		super.onDestroy();
	}
	
	private void initData(){
		if(pushContentDB == null){
			pushContentDB = new PushContentDB(this.getActivity());
		}
				
		if(pushAdapter == null){
			pushAdapter = new PushAdapter(this.getActivity(),R.layout.pc_list_item);
		}
		
		setListAdapter(pushAdapter);
		
		uread = getResources().getString(R.string.pushcontent_uread);
		read = getResources().getString(R.string.pushcontent_read);	
		downloadFail = getResources().getString(R.string.pushcontent_downloadFail);	
		downloadFinish = getResources().getString(R.string.pushcontent_finishDownload);	
		
		download = getResources().getString(R.string.download);	
		downloadStop = getResources().getString(R.string.stopDownload);	
		checkout = getResources().getString(R.string.view);	
		
		initAdapter();
		
	}
	private void initAdapter(){

		Cursor cursor = pushContentDB.queryItems();
		
		if(nColTitle == -1){
			nColIndex = cursor.getColumnIndex(PushContentDB.INDEX);
			nColTitle = cursor.getColumnIndex(PushContentDB.TITLE);
			nColStatus = cursor.getColumnIndex(PushContentDB.STATUS);
			nColPath = cursor.getColumnIndex(PushContentDB.LOCAL_PATH);
			nColType = cursor.getColumnIndex(PushContentDB.TYPE);
			nColTime = cursor.getColumnIndex(PushContentDB.TIME);
			nColSize = cursor.getColumnIndex(PushContentDB.SIZE);
			nColContent = cursor.getColumnIndex(PushContentDB.CONTENT);
			nColFlag = cursor.getColumnIndex(PushContentDB.FLAG);
		}
		pushAdapter.initData();
		if(cursor.moveToFirst() == false){
			return;
		}
		while(cursor.isAfterLast() == false){
			PushData pd = new PushData();
			pd.id = cursor.getInt(nColIndex);
			pd.type = cursor.getString(nColType);
			boolean bDownload = false;
			if(PushServiceUtil.DEFAULTID_TEXT.equalsIgnoreCase(pd.type)){
    			pd.typeDrawable = R.drawable.text;
    		}else if(PushServiceUtil.DEFAULTID_URL.equalsIgnoreCase(pd.type)){
    			pd.typeDrawable = R.drawable.www;
    		}else if(PushServiceUtil.DEFAULTID_PICTURE.equalsIgnoreCase(pd.type)){
    			pd.typeDrawable = R.drawable.picture;
    			bDownload = true;
    		}else if(PushServiceUtil.DEFAULTID_VIDEO.equalsIgnoreCase(pd.type)){
    			pd.typeDrawable = R.drawable.picture;
    			bDownload = true;
    		}else if(PushServiceUtil.DEFAULTID_AUDIO.equalsIgnoreCase(pd.type)){
    			pd.typeDrawable = R.drawable.music;
    			bDownload = true;
    		}else if(PushServiceUtil.DEFAULTID_STORY.equalsIgnoreCase(pd.type)){
    			pd.typeDrawable = R.drawable.story;
    		}else{
    			pd.typeDrawable = R.drawable.icon;
    		}
			
			pd.title = cursor.getString(nColTitle);
			pd.content = cursor.getString(nColContent);
			pd.size = cursor.getString(nColSize);
			pd.status = cursor.getString(nColStatus);
			pd.time = cursor.getString(nColTime);
			pd.path = cursor.getString(nColPath);
			pd.flag = cursor.getInt(nColFlag);
			if(pd.flag == 0){
				if(bDownload){
					pd.flag = STATUS_DOWNLOAD;
				}else{
					pd.flag = STATUS_VIEW;
				}
			}
			cursor.moveToNext();
			pushAdapter.addItem(pd);
		}
		cursor.close();
		pushAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	public void onListItemClick(ListView l, final View view, int position, final long id) {	
		Log.i(TAG,PRE + "onListItemClick ID : " + id);
		
		// set unread to read 
		final PushData pd = pushAdapter.getData(position);
		if(pd == null){
			return;			
		}
		
		if(uread.endsWith(pd.status)){
			pushContentDB.updateStatus(pd.id, read);
			pd.status = read;
			TextView st = (TextView)view.findViewById(R.id.tv_pushcontent_status);
			st.setText(read);
			
			showToast(R.string.pushcontent_read,Toast.LENGTH_SHORT);	
			return;
		}
		
		// Pop up menu
		final QuickAction mQuickAction 	= new QuickAction(view);
		final Button actionBtn = (Button)view.findViewById(R.id.btn_pushcontent_action);
		
		
		getDeleteAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mQuickAction.dismiss();
				pushContentDB.deleteItem(String.valueOf(pd.id));
				// TODO can optimize 
				initAdapter();
				showToast(R.string.delsuccess,Toast.LENGTH_LONG);				
				
			}
		});
		mQuickAction.addActionItem(getDeleteAction());
		
		// add difference action in difference situation
		if(pd.flag == STATUS_DOWNLOAD){			
			getDownloadAction().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mQuickAction.dismiss();
					pd.flag = STATUS_DOWNLOAD_CANCEL;
					pushContentDB.updateItem(pd.id, PushContentDB.FLAG, 
							String.valueOf(STATUS_DOWNLOAD_CANCEL));
					// start down load
					showToast(R.string.pushcontent_beginDownload,Toast.LENGTH_SHORT);
					startDownload(pd);
				}
			});
			mQuickAction.addActionItem(getDownloadAction());
			
		} else if(pd.flag == STATUS_VIEW) {
			getViewAction().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mQuickAction.dismiss();
					btnView(pd,view);
					
				}
			});
			mQuickAction.addActionItem(getViewAction());
		} else if(pd.flag == STATUS_DOWNLOAD_CANCEL){
			getStopDownloadAction().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mQuickAction.dismiss();
					pd.flag = STATUS_DOWNLOAD;
					pushContentDB.updateItem(pd.id, PushContentDB.FLAG, 
							String.valueOf(STATUS_DOWNLOAD));
					showToast(R.string.stopDownload,Toast.LENGTH_SHORT);
					//int id = (Integer)actionBtn.getTag(R.string.position);
					//btnStopDownload(id);					
				}
			});
			mQuickAction.addActionItem(getStopDownloadAction());
			
		}
		getCancelAction().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mQuickAction.dismiss();
				showToast(R.string.cancel,Toast.LENGTH_SHORT);
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
		Toast t = BigToast.makeText(getActivity(),
				getActivity().getResources().getString(text), 
				duration);						
		t.setGravity(Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
		t.setMargin(PushServiceUtil.HORIZONTAL_MARGIN, PushServiceUtil.VERTICAL_MARGIN);
		t.show();
	}
	private void startDownload(PushData pd){
		try {
			DownloadInf dl = new DownloadInf();
			dl.desPath = videoPath + "aa.apk";
			dl.id = pd.id;
			dl.url = "http://cloud.github.com/downloads/buddycloud/android-client/buddycloud-debug.apk";
			sendMsgService(PushServiceUtil.MSG_DOWNLOAD,1, 2, (Object)dl);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * show the detail information in this position
	 * @param position
	 */
	private void btnView(PushData pd,View view){		
		
		String url = pd.content;
		String type = pd.type;		
		String localPath = pd.path;
		
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
    public void viewText(String path){
    	Intent intent = new Intent(Intent.ACTION_EDIT); 
    	Uri uri = Uri.parse(path); 
    	intent.setDataAndType(uri, "text/plain"); 
    	startActivity(intent);
    }
	public void btnStopDownload(int id){		
	}
	private void btnDownload(int position, final Button v){
			
	}
	
	//============== below is getter and setter =============================
	private ActionItem getDeleteAction() {
		if(deleteAction == null){
			deleteAction = new ActionItem();
			deleteAction.setTitle(getActivity().getResources().getString(R.string.delete));
			deleteAction.setIcon(getResources().getDrawable(R.drawable.pc_delete));			
		}
		return deleteAction;
	}
	private ActionItem getViewAction() {
		if(viewAction == null){
			viewAction = new ActionItem();
			viewAction.setTitle(getActivity().getResources().getString(R.string.view));
			viewAction.setIcon(getResources().getDrawable(R.drawable.pc_view));			
		}
		return viewAction;
	}
	private ActionItem getCancelAction() {
		if(cancelAction == null){
			cancelAction = new ActionItem();
			cancelAction.setTitle(getActivity().getResources()
					.getString(R.string.cancel));
			cancelAction.setIcon(getResources().getDrawable(R.drawable.pc_cancel));					
		}
		return cancelAction;
	}
	private ActionItem getDownloadAction() {
		if(downloadAction == null){
			downloadAction = new ActionItem();
			downloadAction.setTitle(getActivity().getResources()
					.getString(R.string.download));
			downloadAction.setIcon(getResources().getDrawable(R.drawable.pc_download));
		}
		return downloadAction;
	}
	private ActionItem getStopDownloadAction() {
		if(stopDownloadAction == null){
			stopDownloadAction = new ActionItem();
			stopDownloadAction.setTitle(getActivity().getResources()
					.getString(R.string.stopDownload));
			stopDownloadAction.setIcon(getResources().getDrawable(R.drawable.pc_stopdown));
		}
		return stopDownloadAction;
	}
	
	private class PushData{
		public int id;
		public String type;
		public int typeDrawable;
		public String title;		
		public String content;
		public String status;
		public String time;
		public String size;
		public String path;
		public int flag;
	}
	public class PushAdapter extends BaseAdapter {
        
        private List<PushData> mData = new ArrayList<PushData>();

        private int mResource;
        private int mDropDownResource;
        private LayoutInflater mInflater;
        
        private final WeakHashMap<View, View[]> mHolders = new WeakHashMap<View, View[]>();

        public PushAdapter(Context context,int resource) {           
            mResource = mDropDownResource = resource; 
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }        
        @Override
		public boolean areAllItemsEnabled() {
			return super.areAllItemsEnabled();
		}
		@Override
		public boolean isEnabled(int position) {
			return super.isEnabled(position);
		}
		public int getCount() {
            return mData.size();
        }
        public Object getItem(int position) {
        	return mData.get(position);
        }
        public long getItemId(int position) {
        	return mData.get(position).id;
        }
        public int idToPosition(int id){
        	// TODO need to optimize
        	int position = -1;
        	Iterator<PushData> it = mData.iterator();
        	int i = 0;
        	while(it.hasNext()){
        		PushData pd = it.next();
        		if(pd.id == id){
        			position = i;
        			break;
        		}
        		i++;
        	}
        	return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	return createViewFromResource(position, convertView, parent, mResource);
        }
        
        private View createViewFromResource(int position, View convertView,
                ViewGroup parent, int resource) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(resource, parent, false);
            
                final View[] holder = new View[7];
               
                holder[0] = v.findViewById(R.id.iv_pushcontent_type);
                holder[1] = v.findViewById(R.id.tv_pushcontent_title);
                holder[2] = v.findViewById(R.id.tv_pushcontent_content);                
                holder[3] = v.findViewById(R.id.tv_pushcontent_size);
                holder[4] = v.findViewById(R.id.tv_pushcontent_time);
                holder[5] = v.findViewById(R.id.tv_pushcontent_status);
                holder[6] = v.findViewById(R.id.btn_pushcontent_action);
                holder[6].setVisibility(View.GONE);
                mHolders.put(v, holder);
            } else {
                v = convertView;
            }

            bindView(position, v);            
            return v;
        }
        
        public void setDropDownViewResource(int resource) {
            this.mDropDownResource = resource;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return createViewFromResource(position, convertView, parent, mDropDownResource);
        }
        public void setViewImage(ImageView v, int value) {
            v.setImageResource(value);
        }
        public void setViewImage(ImageView v, String value) {
            try {
                v.setImageResource(Integer.parseInt(value));
            } catch (NumberFormatException nfe) {
                v.setImageURI(Uri.parse(value));
            }
        }
       
        public void setViewText(TextView v, String text) {
            v.setText(text);
        }

        private void bindView(int position, View view) {
            final PushData dataSet = mData.get(position);
            if (dataSet == null) {
                return;
            }
            
            final View[] holder = mHolders.get(view);           
            if(holder == null){
            	return;
            }
            ImageView imageView = (ImageView)holder[0];
            imageView.setImageResource(dataSet.typeDrawable);
            TextView titleView = (TextView)holder[1];  
            titleView.setText(dataSet.title);
            TextView contentView = (TextView)holder[2];
            contentView.setText(dataSet.content);
            TextView sizeView = (TextView)holder[3];
            sizeView.setText(dataSet.size);
            TextView timeView = (TextView)holder[4];
            timeView.setText(dataSet.time);
            TextView statusView = (TextView)holder[5];
            statusView.setText(dataSet.status);   		
           
        }// the end of bind view function
        public void updateView(View view, int nfinish, int nTotal, int status){
        	final View[] holder = mHolders.get(view);           
            if(holder == null){
            	return;
            }
            TextView timeView = (TextView)holder[4];
            timeView.setText("nfinish="+nfinish+" status="+status);
        }
        public void addItem(PushData pd){
        	mData.add(pd);
        }
        public void initData(){
        	mData.clear();
        }
        public PushData getData(int position){
        	if(position < mData.size()){
        		return mData.get(position);
        	}
        	return null;
        }
    }
	@Override
	public void handleMessage(Message msg) {
        switch (msg.what) {                          
        case PushServiceUtil.MSG_DOWNLOAD:
        	DownloadInf downloadInf = (DownloadInf)msg.obj;
        	Log.e(TAG,PRE+ "have finish:" +downloadInf.nFinishSize);
        	int position = pushAdapter.idToPosition(downloadInf.id);    		
    		int childPos = position - getListView().getFirstVisiblePosition();
    		
    		View itemView = getListView().getChildAt(childPos);;
    		if(itemView == null){
    			return;
    		}  
    		pushAdapter.updateView(itemView,downloadInf.nFinishSize,
    				downloadInf.nTotalSize,downloadInf.status);
        	if(downloadInf.status == PushServiceUtil.DOWNLOAD_SUCCESS){
        		showToast(R.string.pushcontent_finishDownload,Toast.LENGTH_LONG);
        	}
        	break;
        }				
	}
	
}
