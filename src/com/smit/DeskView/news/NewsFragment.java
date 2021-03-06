package com.smit.DeskView.news;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;

import com.openims.utility.PushServiceUtil;
import com.smit.DeskView.commonclass.CommonDataFun;
import com.smit.DeskView.commonclass.RequestXml;
import com.smit.DeskView.commonclass.NewsMoveParse;
import com.smit.DeskView.vodvideo.VODVideoListFragment.VodVideoAdapter;
import com.smit.EasyLauncher.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class NewsFragment extends Fragment {
	private LayoutInflater mInflater = null;
	private ImageView moreImage;
	private final static int GET_VOD_VIDEO_XML = 0x800;
	private final static int PUSH_VOD_VIDEO_XML = 0x801;
	private final static String Tag = "NewsFragment";
	private final static String categoryString="com.smit.DeskView.news.NewsFragment";
	public NewsMoveParse mMovieParse = null;
	private static String VIDEO_ITEM_FILE_DIR = "data/data/com.smit.EasyLauncher/files";
	private static String VIDEO_ITEM_FILE = "data/data/com.smit.EasyLauncher/files/news.xml";// 新闻文件

	
	FrameLayout vodvideo_flash,vodvideo_loading;
	private ImageView vodvideo_image_loading;
	private NewsListFragment newsListFragment;
	private Button vodvideo_button_flash;
	private FrameLayout listFrame;
	public static boolean existInstance = false;
	
	RequestXml mThread=null;
	
	public final static int SHOW_LAODING=0;
	public final static int SHOW_FLASH=1;
	public final static int SHOW_LIST=2;
	
	public int curMyStatus=SHOW_LAODING;
	
	private BroadcastReceiver loginReceiver,pushReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		existInstance = true;
		setRetainInstance(true);
		loginReceiver=new InnerReceiver();
		pushReceiver=new PushServiceReceiver();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupView();
		
		if (existInstance) {
			if(checkWifiIscon())
			{
				requestXml(null,GET_VOD_VIDEO_XML);
				mHandler.postDelayed(mRunnable, 700);
				
				SetCurShow(SHOW_LAODING);
			}else {
				String str=ReadVodVideoItemXML();
				if (str!=null) {
					mMovieParse = new NewsMoveParse(str);
					mMovieParse.parseDataStr();
				}		
				if (IsExistvodMove(mMovieParse)) {
					SetCurShow(SHOW_LIST);
					//showVodVideoList();
				}else {
					SetCurShow(SHOW_FLASH);
				}
			}	
		}else {
				if (curMyStatus==SHOW_LAODING) {
				
			}else{
				SetCurShow(curMyStatus);
			}		
		}
	
		existInstance = false;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;

		return inflater.inflate(R.layout.news_widget_home_page, container,
				false);

		// return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		  IntentFilter intentFilter = new IntentFilter();
	      intentFilter.addAction(PushServiceUtil.ACTION_STATUS);
	      getActivity().registerReceiver(loginReceiver, intentFilter);  
	      
			intentFilter=new IntentFilter();
			intentFilter.addAction(PushServiceUtil.ACTION_REGISTRATION);
			intentFilter.addAction(PushServiceUtil.ACTION_RECEIVE);
			intentFilter.addAction(PushServiceUtil.ACTION_STATUS);
			intentFilter.addCategory(categoryString);
		    getActivity().registerReceiver(pushReceiver, intentFilter);
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		getActivity().unregisterReceiver(loginReceiver);
		getActivity().unregisterReceiver(pushReceiver);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mThread!=null){
			mThread.stopThread();
			mThread=null;
		}
		if (mHandler!=null) {
			mHandler.removeCallbacks(mRunnable);
		}
		existInstance=false;
	}

	public void setupView() {
		
		vodvideo_loading=(FrameLayout)getView().findViewById(R.id.news_loading);
		vodvideo_image_loading=(ImageView)(getView().findViewById(R.id.news_image_loading));
		vodvideo_flash=(FrameLayout)getView().findViewById(R.id.news_flash);
		vodvideo_button_flash=(Button)getView().findViewById(R.id.news_button_flash);
		vodvideo_button_flash.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkWifiIscon())
				{
					requestXml(null,GET_VOD_VIDEO_XML);
					mHandler.postDelayed(mRunnable, 1000);
					
					SetCurShow(SHOW_LAODING);
				}else {
					String str=ReadVodVideoItemXML();
					if (str!=null) {
						mMovieParse = new NewsMoveParse(str);
						mMovieParse.parseDataStr();
					}		
					if (IsExistvodMove(mMovieParse)) {
						SetCurShow(SHOW_LIST);
						//showVodVideoList();
					}else {
						SetCurShow(SHOW_FLASH);
					}
				}	
			}
		});
		
		listFrame = (FrameLayout)getView().findViewById(R.id.news_listdragment);
		
		moreImage = (ImageView) getView().findViewById(R.id.news_more);
		moreImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//更多
				
			}
		});
	}
	
	public void SetCurShow(int curStatus){
		switch (curStatus) {
		case SHOW_LAODING:
		{
			listFrame.setVisibility(View.GONE);
			vodvideo_flash.setVisibility(View.GONE);
			vodvideo_loading.setVisibility(View.VISIBLE);
			
			break;
		}
		case SHOW_FLASH:{
			listFrame.setVisibility(View.GONE);
			vodvideo_flash.setVisibility(View.VISIBLE);
			vodvideo_loading.setVisibility(View.GONE);
			break;
		}
		case SHOW_LIST:{
			listFrame.setVisibility(View.VISIBLE);
			vodvideo_flash.setVisibility(View.GONE);
			vodvideo_loading.setVisibility(View.GONE);		
			if(!showNewsList()){
				SetCurShow(SHOW_FLASH);
			}
			break;
		}
		default:
			break;
		}
		
		curMyStatus=curStatus;
	}

	private boolean showNewsList() {
		FragmentManager mFragmentManager=getFragmentManager();
		if (mFragmentManager!=null) {
			newsListFragment = (NewsListFragment) mFragmentManager.findFragmentById(R.id.news_listdragment_fragment);
			if (newsListFragment == null) {
				return false;
			}else {
				return newsListFragment.ShowCurList();
			}
		}else {
			return false;
		}
	}

	private String checkNetworkInfo() {
		ConnectivityManager conMan = (ConnectivityManager) getActivity()
				.getSystemService(getActivity().CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = (conMan
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
		String string = wifi.toString();
		return string;
	}

	private boolean checkWifiIscon() {
		String str = checkNetworkInfo();
		if (str.equals("CONNECTED")) {
			return true;
		} else {
			return false;
		}
	}
	
	   private Runnable mRunnable = new Runnable() {	
			public void run() {
				if (GobalFunVar.CUR_PIC<GobalFunVar.LOAD_COUNT-1) {
					GobalFunVar.CUR_PIC++;
				}else {
					GobalFunVar.CUR_PIC=0;
				}
				mHandler.postDelayed(mRunnable, 700);
				vodvideo_image_loading.setBackgroundResource(GobalFunVar.LOAD_PIC[GobalFunVar.CUR_PIC]);
			}
			
		};

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VOD_VIDEO_XML: {
				if (mThread==null) {
					return;
				}
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {	
					mMovieParse = new NewsMoveParse(str); 
					mMovieParse.parseDataStr();
					if (mMovieParse!=null && mMovieParse.getItemCount()>0) {
						WriteVodVideoItemXML(str); 
						SetCurShow(SHOW_LIST);
						//showVodVideoList();
					}else {
						SetCurShow(SHOW_FLASH);
					}					 				 
				}else {
					SetCurShow(SHOW_FLASH);
				}
				break;

			}
			case PUSH_VOD_VIDEO_XML:{
				if (mThread == null) {
					return;
				}
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					NewsMoveParse mTmpMovieParse=new NewsMoveParse(str);
					mTmpMovieParse.parseDataStr();
					if (mTmpMovieParse != null && mTmpMovieParse.getItemCount() > 0) {	
						WriteVodVideoItemXML(str);
						mMovieParse=mTmpMovieParse;
						SetCurShow(SHOW_LIST);
					} else {
						SetCurShow(SHOW_FLASH);
					}
				} else {
					SetCurShow(SHOW_FLASH);
				}
				break;
			}
			case 2:
				break;
			}
		}
	};

	public void requestXml(String urlString,int Id) {
		//String Url = CommonDataFun.myServerAddr+"news.do?columnKey=316";
		
		String Url;
		if (urlString==null) {
			Url = CommonDataFun.myServerAddr + "latestNews.do";
		}else {
			Url=urlString;
		}
		
		try {
			URL url = new URL(Url);
			if (mThread!=null) {
				mThread.stopThread();
				mThread=null;
			}
			mThread = new RequestXml(url, mHandler, Id, null);

			mThread.start();
		} catch (Exception e) {
		}

	}

	
	public String ReadVodVideoItemXML() {
		InputStream is = null;
		byte[] data = null;
		String str = null;

		File TestItemFile = new File(VIDEO_ITEM_FILE);
		if (!TestItemFile.exists()) {
			return null;
		}
		try {
			int length = (int) TestItemFile.length() + 10;
			data = new byte[length];
			is = new BufferedInputStream(new FileInputStream(TestItemFile));
			while (is.read(data) != -1);
			is.close();

		} catch (Exception e) {
			// TODO: handle exception
		}

		str = new String(data);

		return str;
	}

	// 写测试项xml文件 从服务器得到的
	public void WriteVodVideoItemXML(String str) {

		CreateDataPath(VIDEO_ITEM_FILE_DIR);
		File existFile = new File(VIDEO_ITEM_FILE);
		if (existFile.exists() && existFile.length() > 0) {
			existFile.delete();
		}

		File TestItemFile = new File(VIDEO_ITEM_FILE);
		try {
			TestItemFile.createNewFile();
		} catch (IOException e) {
			// Log.e("IOException", "exception in createNewFile() method");
			Log.e(Tag, e.toString());
		}
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(TestItemFile);
		} catch (FileNotFoundException e) {
			// Log.e("FileNotFoundException", "can't create FileOutputStream");
			Log.e(Tag, e.toString());
		}

		try {
			byte buf[] = str.getBytes();
			int numread = 0;

			numread = buf.length;
			if (numread <= 0) {
				// break;
			} else {
				fileos.write(buf, 0, numread);
			}
			fileos.close();
		} catch (Exception e) {
			// Log.e("Exception","error occurred while creating xml file");
			Log.e(Tag, e.toString());
		}
	}

	// 创建sd卡路径
	public void CreateDataPath(String str) {
		File file = new File(str);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public boolean IsExistvodMove(NewsMoveParse mMovieParse){
		String str=ReadVodVideoItemXML();
		if (str==null||mMovieParse.getItemCount()<=0) {
			return false;
		}else {
			return true;
		}
	}
	
	public static class GobalFunVar {
		public static int CUR_PIC=0;
		public final static int LOAD_COUNT=10;
		public final static int LOAD_PIC[]={
			R.drawable.s0_login_loading_00,
			R.drawable.s0_login_loading_01,
			R.drawable.s0_login_loading_02,
			R.drawable.s0_login_loading_03,
			R.drawable.s0_login_loading_04,
			R.drawable.s0_login_loading_05,
			R.drawable.s0_login_loading_06,
			R.drawable.s0_login_loading_07,
			R.drawable.s0_login_loading_08,
			R.drawable.s0_login_loading_09,};
	}
	
	//开启服务
	  void regPushService(boolean bReg){
	    	Intent regIntent = new Intent(PushServiceUtil.ACTION_SERVICE_REGISTER);	

			if(bReg){
				regIntent.putExtra(PushServiceUtil.PUSH_TYPE,PushServiceUtil.PUSH_TYPE_REG);
			}else{
				regIntent.putExtra(PushServiceUtil.PUSH_TYPE,PushServiceUtil.PUSH_TYPE_UNREG);
			}
					
			regIntent.putExtra(PushServiceUtil.PUSH_DEVELOPER,
					"mtv");
			regIntent.putExtra(PushServiceUtil.PUSH_NAME_KEY,
			"I59ma75nmV67rWdD275jC0SQ2bJDBW5W");
			regIntent.putExtra(PushServiceUtil.PUSH_CATEGORY,categoryString);
			
			
			getActivity().startService(regIntent);	
	    }
	    
	 
	//push 广播
	public class PushServiceReceiver extends BroadcastReceiver{
		
		private static final String LOGTAG = "PushServiceReceiver";
	    private static final String tag = "";
	    
		public PushServiceReceiver(){
			
		}
		
		@Override
	    public void onReceive(Context context, Intent intent) {
			Log.d(LOGTAG,tag+"onReceiver");
			
			
			if(intent.getAction().equals("com.openims.pushService.REGISTRATION")){			
				handleRegistration(context, intent);
			}else if(intent.getAction().equals("com.openims.pushService.RECEIVE")){
				handleMessage(context, intent);
			}else if(intent.getAction().equals(PushServiceUtil.ACTION_STATUS)){
				handleStatus(context, intent);
			}else{
				Log.e(LOGTAG,tag+"receiver error type");
			}
			//你可以在这里给的UI发内部类的广播，也可以startActivity，但是要注意activity要设置一下
			//启动的模式为singleTask和重载activity的onNewStart（Intent intent）以获得最新的
			//Intent
			
		}
		
		//注册广播
		private void handleRegistration(Context context, Intent intent) {
			
			Log.d(LOGTAG,tag+"handleRegistration");
		    String pushId = intent.getStringExtra(PushServiceUtil.PUSH_ID); 
		    String pustStatus = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
		    boolean bRegOrUnreg = true;
		    if(PushServiceUtil.PUSH_TYPE_UNREG.equals(intent.getStringArrayExtra(PushServiceUtil.PUSH_TYPE))){
		    	bRegOrUnreg = false;
		    }
		   
		    Log.d(LOGTAG,tag+"Registration succuss and Id = " + pushId);
		    Log.d(LOGTAG,tag+"Registration status = " + pustStatus);
		    Log.d(LOGTAG,tag+"Registration Reg or Unreg = " + String.valueOf(bRegOrUnreg));
		
		        
		}
		
		//收到广播消息
		protected void handleMessage(Context context, Intent intent) {
			
			Log.d(LOGTAG,tag+"handleMessage");
			
			Log.d("handleMessage","===========1111111111111111111111============");
			Log.d("handleMessage","===========2222222222222222222222============");
			Log.d("handleMessage","===========3333333333333333333333============");
			Log.d("handleMessage","===========4444444444444444444444============");
			Log.d("handleMessage","===========5555555555555555555555============");
			String title = intent.getStringExtra(PushServiceUtil.NTFY_TITLE);
			String ticker = intent.getStringExtra(PushServiceUtil.NTFY_TICKER);
			String uriString = intent.getStringExtra(PushServiceUtil.NTFY_URI);
			String message = intent.getStringExtra(PushServiceUtil.NTFY_MESSAGE);
			
			if (checkWifiIscon()){
				requestXml(uriString,PUSH_VOD_VIDEO_XML);
			}
		/*	StringBuilder all = new StringBuilder();
			all.append("收到的push内容：ticker").append(ticker).
			append("\nTitle:").append(title).append("\nUri:").
			append(uriString).append("\nMessage:").append(message);
			Log.d(LOGTAG,tag+"message:"+all);
			Toast.makeText(context, all, Toast.LENGTH_LONG);*/
			
		}
		
		private void handleStatus(Context context, Intent intent) {
			String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);
			Log.d(LOGTAG,tag+"status:"+status);
		    
			//Intent intentBroadcast = new Intent(Setting.InnerReceiver.ACTION);
			//intentBroadcast.putExtra(PushServiceUtil.PUSH_STATUS, status);
			//context.sendBroadcast(intentBroadcast);
		}
	}
	
	
	//登陆广播
	 public class InnerReceiver extends BroadcastReceiver{
	        
	    	@Override
	    	public void onReceive(Context context,Intent intent){

	    		if(intent.getAction().equals(PushServiceUtil.ACTION_STATUS)){
		    		String status = intent.getStringExtra(PushServiceUtil.PUSH_STATUS);

		    		if(status.equals(PushServiceUtil.PUSH_STATUS_LOGIN_SUC)){
		    			regPushService(true);	
		    		}else if(status.equals(PushServiceUtil.PUSH_STATUS_LOGIN_FAIL)
		    				||status.equals(PushServiceUtil.PUSH_STATUS_CONNECTION_FAIL)){
		    		}
	    		}
	    	}
	    }
}