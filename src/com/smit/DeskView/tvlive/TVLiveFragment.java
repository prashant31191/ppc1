package com.smit.DeskView.tvlive;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;

import com.openims.downloader.DownloadInf;
import com.openims.utility.PushServiceUtil;
import com.smit.DeskView.commonclass.CommonDataFun;
import com.smit.DeskView.commonclass.RequestXml;
import com.smit.DeskView.commonclass.VodVideoMoveParse;
import com.smit.DeskView.commonclass.TvLiveChannelParse;
import com.smit.DeskView.vodvideo.VODVideoListFragment.VodVideoAdapter;
import com.smit.EasyLauncher.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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

public class TVLiveFragment extends Fragment {
	private LayoutInflater mInflater = null;
	private final static int GET_VOD_VIDEO_XML = 0x800;
	private final static String Tag = "VODVideoFragment";
	public TvLiveChannelParse mtvParse = null;
	private static String TVLIVE_ITEM_FILE_DIR = "data/data/com.smit.EasyLauncher/files";// 视屏文件
	private static String TVLIVE_ITEM_FILE = "data/data/com.smit.EasyLauncher/files/tvlive.xml";// 视屏文件

	private FrameLayout listFrame;
	FrameLayout tvlive_flash, tvlive_loading;
	private ImageView tvlive_image_loading;
	private TVLiveListFragment tvListFragment;
	private Button tvlive_button_flash;
	private ImageView moreImage;
	public static boolean existInstance = false;

	RequestXml mThread = null;

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
		setupView();

		if (existInstance) {
			if (checkWifiIscon()) {
				requestXml();
				mHandler.postDelayed(mRunnable, 700);

				SetCurShow(SHOW_LAODING);
			} else {
				String str = ReadVodVideoItemXML();
				if (str != null) {
					mtvParse = new TvLiveChannelParse(str);
					mtvParse.parseDataStr();
				}
				if (IsExistvodMove(mtvParse)) {
					SetCurShow(SHOW_LIST);
					// showVodVideoList();
				} else {
					SetCurShow(SHOW_FLASH);
				}
			}
		} else {
			if (curMyStatus==SHOW_LAODING) {
				
			}else{
				SetCurShow(curMyStatus);
			}
		}
		
		existInstance =false;

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

		return inflater.inflate(R.layout.tvlive_widget_home_page, container,
				false);

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
		existInstance=false;
	}

	public void setupView() {

		tvlive_loading = (FrameLayout) getView().findViewById(
				R.id.tvlive_loading);
		tvlive_image_loading = (ImageView) (getView()
				.findViewById(R.id.tvlive_image_loading));
		tvlive_flash = (FrameLayout) getView().findViewById(R.id.tvlive_flash);
		tvlive_button_flash = (Button) getView().findViewById(
				R.id.tvlive_button_flash);
		tvlive_button_flash.setOnClickListener(new OnClickListener() {
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
						mtvParse = new TvLiveChannelParse(str);
						mtvParse.parseDataStr();
					}
					if (IsExistvodMove(mtvParse)) {
						SetCurShow(SHOW_LIST);
						// showVodVideoList();
					} else {
						SetCurShow(SHOW_FLASH);
					}
				}
			}
		});

		listFrame = (FrameLayout) getView().findViewById(
				R.id.tvlive_listdragment);

		moreImage = (ImageView) getView().findViewById(R.id.tvlive_more);
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
			tvlive_flash.setVisibility(View.GONE);
			tvlive_loading.setVisibility(View.VISIBLE);

			break;
		}
		case SHOW_FLASH: {
			listFrame.setVisibility(View.GONE);
			tvlive_flash.setVisibility(View.VISIBLE);
			tvlive_loading.setVisibility(View.GONE);
			break;
		}
		case SHOW_LIST: {
			listFrame.setVisibility(View.VISIBLE);
			tvlive_flash.setVisibility(View.GONE);
			tvlive_loading.setVisibility(View.GONE);
			if(!showTvList()){
				SetCurShow(SHOW_FLASH);
			}
			break;
		}
		default:
			break;
		}
		
		curMyStatus=curStatus;
	}

	private boolean showTvList() {
		FragmentManager mFragmentManager=getFragmentManager();
		if (mFragmentManager!=null) {
			tvListFragment=(TVLiveListFragment)mFragmentManager.findFragmentById(R.id.tvlive_listdragment_fragment);
			if (tvListFragment == null) {
				return false;
			}else {
				return tvListFragment.ShowCurList();
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
			if (GobalFunVar.CUR_PIC < GobalFunVar.LOAD_COUNT - 1) {
				GobalFunVar.CUR_PIC++;
			} else {
				GobalFunVar.CUR_PIC = 0;
			}
			mHandler.postDelayed(mRunnable, 700);
			tvlive_image_loading
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
					mtvParse = new TvLiveChannelParse(str);
					mtvParse.parseDataStr();
					if (mtvParse != null && mtvParse.getItemCount() > 0) {
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
		String Url = CommonDataFun.myServerAddr + "channel.do";
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

		File TestItemFile = new File(TVLIVE_ITEM_FILE);
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

		CreateDataPath(TVLIVE_ITEM_FILE_DIR);
		File existFile = new File(TVLIVE_ITEM_FILE);
		if (existFile.exists() && existFile.length() > 0) {
			existFile.delete();
		}

		File TestItemFile = new File(TVLIVE_ITEM_FILE);
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

	public boolean IsExistvodMove(TvLiveChannelParse mMovieParse) {
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