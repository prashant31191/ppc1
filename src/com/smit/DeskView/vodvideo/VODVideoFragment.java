package com.smit.DeskView.vodvideo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;

import com.smit.DeskView.commonclass.CommonDataFun;
import com.smit.DeskView.commonclass.RequestXml;
import com.smit.DeskView.commonclass.VodVideoMoveParse;
import com.smit.DeskView.vodvideo.VODVideoListFragment.VodVideoAdapter;
import com.smit.EasyLauncher.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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

public class VODVideoFragment extends Fragment {
	private LayoutInflater mInflater = null;
	private ImageView moreImage;
	private final static int GET_VOD_VIDEO_XML = 0x800;
	private final static String Tag = "VODVideoFragment";
	public VodVideoMoveParse mMovieParse = null;
	private static String VIDEO_ITEM_FILE_DIR = "data/data/com.smit.EasyLauncher/files";// 视屏文件
	private static String VIDEO_ITEM_FILE = "data/data/com.smit.EasyLauncher/files/vodvideo.xml";// 视屏文件

	FrameLayout vodvideo_flash, vodvideo_loading;
	private ImageView vodvideo_image_loading;
	private VODVideoListFragment vodListFragment;
	private Button vodvideo_button_flash;
	private FrameLayout listFrame;

	RequestXml mThread = null;
	public static boolean existInstance = false;

	public final static int SHOW_LAODING = 0;
	public final static int SHOW_FLASH = 1;
	public final static int SHOW_LIST = 2;
	
	public int curMyStatus=SHOW_LAODING;

	 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		existInstance = true;
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setupView(getView());
		if (existInstance) {
			if (checkWifiIscon()) {
				requestXml();
				mHandler.postDelayed(mRunnable, 700);

				SetCurShow(SHOW_LAODING);
			} else {
				String str = ReadVodVideoItemXML();
				if (str != null) {
					mMovieParse = new VodVideoMoveParse(str);
					mMovieParse.parseDataStr();
				}
				if (IsExistvodMove(mMovieParse)) {
					SetCurShow(SHOW_LIST);
					//showVodVideoList();
				} else {
					SetCurShow(SHOW_FLASH);
				}
			}
		}else {
			
			if (curMyStatus==SHOW_LAODING) {
				
			}else{
				SetCurShow(curMyStatus);
				/*String str = ReadVodVideoItemXML();
				if (str != null) {
					mMovieParse = new VodVideoMoveParse(str);
					mMovieParse.parseDataStr();
				}
				if (IsExistvodMove(mMovieParse)) {
					SetCurShow(SHOW_LIST);
					//showVodVideoList();
				} else {
					SetCurShow(SHOW_FLASH);
				}*/
			}		
		}	
		existInstance =false;
	
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
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
		View view = inflater.inflate(R.layout.vodvideo_widget_home_page,
				container, false);

		
		return view;

		// return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mThread != null) {
			mThread.stopThread();
			mThread = null;
		}
		if (mHandler != null) {
			mHandler.removeCallbacks(mRunnable);
		}
		existInstance = false;
	}

	public void setupView(View view) {

		vodvideo_loading = (FrameLayout) view
				.findViewById(R.id.vodvideo_loading);
		vodvideo_image_loading = (ImageView) (view
				.findViewById(R.id.vodvideo_image_loading));
		vodvideo_flash = (FrameLayout) view.findViewById(R.id.vodvideo_flash);
		vodvideo_button_flash = (Button) view
				.findViewById(R.id.vodvideo_button_flash);
		vodvideo_button_flash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkWifiIscon()) {
					requestXml();
					mHandler.postDelayed(mRunnable, 1000);

					SetCurShow(SHOW_LAODING);
				} else {
					String str = ReadVodVideoItemXML();
					if (str != null) {
						mMovieParse = new VodVideoMoveParse(str);
						mMovieParse.parseDataStr();
					}
					if (IsExistvodMove(mMovieParse)) {
						SetCurShow(SHOW_LIST);
						//showVodVideoList();
					} else {
						SetCurShow(SHOW_FLASH);
					}
				}
			}
		});

		listFrame = (FrameLayout) view.findViewById(R.id.vodvideo_listdragment);

		moreImage = (ImageView) view.findViewById(R.id.vodvideo_more);
		moreImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 更多

			}
		});
	}

	public void SetCurShow(int curStatus) {
		switch (curStatus) {
		case SHOW_LAODING: {
			listFrame.setVisibility(View.GONE);
			vodvideo_flash.setVisibility(View.GONE);
			vodvideo_loading.setVisibility(View.VISIBLE);

			break;
		}
		case SHOW_FLASH: {
			listFrame.setVisibility(View.GONE);
			vodvideo_flash.setVisibility(View.VISIBLE);
			vodvideo_loading.setVisibility(View.GONE);
			break;
		}
		case SHOW_LIST: {
			listFrame.setVisibility(View.VISIBLE);
			vodvideo_flash.setVisibility(View.GONE);
			vodvideo_loading.setVisibility(View.GONE);
			if(!showVodVideoList()){
				SetCurShow(SHOW_FLASH);
			}
			break;
		}
		default:
			break;
		}
		curMyStatus=curStatus;
	}

	private boolean showVodVideoList() {

			vodListFragment = (VODVideoListFragment) getFragmentManager()
				.findFragmentById(R.id.vodvideo_listdragment_fragment);
		if (vodListFragment == null) {
			return false;
		}else {
			return vodListFragment.ShowCurList();
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
			if (GobalFunVar.CUR_PIC < GobalFunVar.LOAD_COUNT - 1) {
				GobalFunVar.CUR_PIC++;
			} else {
				GobalFunVar.CUR_PIC = 0;
			}
			mHandler.postDelayed(mRunnable, 700);
			vodvideo_image_loading
					.setBackgroundResource(GobalFunVar.LOAD_PIC[GobalFunVar.CUR_PIC]);
		}

	};

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VOD_VIDEO_XML: {
				if (mThread == null) {
					return;
				}
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					mMovieParse = new VodVideoMoveParse(str);
					mMovieParse.parseDataStr();
					if (mMovieParse != null && mMovieParse.getItemCount() > 0) {
						WriteVodVideoItemXML(str);
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

	public void requestXml() {
		String Url = CommonDataFun.myServerAddr + "video.do?columnKey=101";
		try {
			URL url = new URL(Url);
			if (mThread != null) {
				mThread.stopThread();
				mThread = null;
			}
			mThread = new RequestXml(url, mHandler, GET_VOD_VIDEO_XML, null);

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
			while (is.read(data) != -1)
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

	public boolean IsExistvodMove(VodVideoMoveParse mMovieParse) {
		String str = ReadVodVideoItemXML();
		if (str == null || mMovieParse.getItemCount() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	public static class GobalFunVar {
		public static int CUR_PIC = 0;
		public final static int LOAD_COUNT = 10;
		public final static int LOAD_PIC[] = { R.drawable.s0_login_loading_00,
				R.drawable.s0_login_loading_01, R.drawable.s0_login_loading_02,
				R.drawable.s0_login_loading_03, R.drawable.s0_login_loading_04,
				R.drawable.s0_login_loading_05, R.drawable.s0_login_loading_06,
				R.drawable.s0_login_loading_07, R.drawable.s0_login_loading_08,
				R.drawable.s0_login_loading_09, };
	}

}