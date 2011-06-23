package com.smit.DeskView.vodvideo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smit.DeskView.commonclass.*;
import com.smit.DeskView.commonclass.VodVideoMoveParse.ItemVideoInfo;
import com.smit.EasyLauncher.R;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VODVideoListFragment extends ListFragment {
	private LayoutInflater mInflater = null;
	int vodvideocount;
	private final static int GET_VOD_VIDEO_XML = 0x800;
	private final static String Tag = "VODVideoListFragment";
	public VodVideoMoveParse mMovieParse = null;
	private AlertDialog.Builder mBuilderpass;
	private AlertDialog mAlertpass;
	
	private static String VIDEO_ITEM_FILE_DIR = "data/data/com.smit.EasyLauncher/files";// 视屏文件
	private static String VIDEO_ITEM_FILE = "data/data/com.smit.EasyLauncher/files/vodvideo.xml";// 视屏文件

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(checkWifiIscon()){
			setListAdapter(new VodVideoAdapter());
		}else {
			String str=ReadVodVideoItemXML();
			WriteVodVideoItemXML(str);
			mMovieParse = new VodVideoMoveParse(str);
			mMovieParse.parseDataStr();
			setListAdapter(new VodVideoAdapter());
		}		
		
		//setListAdapter(new VodVideoAdapter());
		
		getListView().setCacheColorHint(0);
		requestXml();
		
		
		//onCreateDialog(1);
		

	}
	
/*	//设置加载图片
	public void setLoadView(){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View password = inflater.inflate(R.layout.vodvideo_widget_load_progess, null);
		getListView().setEmptyView(password);
	}*/
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VOD_VIDEO_XML: {
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					WriteVodVideoItemXML(str);
					mMovieParse = new VodVideoMoveParse(str);
					mMovieParse.parseDataStr();
					setListAdapter(new VodVideoAdapter());
				}
				break;

			}
			case 2:
				break;
			}
		}
	};

	public void ParseXml(String str) {

	}
	
	public void onCreateDialog(int id) {
		switch (id) {
		case 1: {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View password = inflater.inflate(R.layout.vodvideo_widget_load_progess, null);
			mBuilderpass = new AlertDialog.Builder(getActivity());
			mBuilderpass.setView(password);
			mAlertpass = mBuilderpass.create();
			mAlertpass.show();
			break;
		}
		default:
			break;
		}

	}

	private String checkNetworkInfo()
	   {
	       ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
	       NetworkInfo.State wifi = (conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState();
	       String string=wifi.toString();
	       return string;
	   }
	private boolean checkWifiIscon(){
		   String str=checkNetworkInfo();
		   if (str.equals("CONNECTED")) {
				return true;
			}else {
				return false;
			}
	   }
	
	public void requestXml() {
		String Url = "http://192.168.0.195:8080/pring/video.do?columnKey=102";
		try {
			URL url = new URL(Url);
			Thread mThread = new RequestXml(url, mHandler, GET_VOD_VIDEO_XML,
					null);

			mThread.start();
		} catch (Exception e) {
		}

	}

	// 创建sd卡路径
	public void CreateDataPath(String str) {
		File file = new File(str);
		if (!file.exists()) {
			file.mkdirs();
		}
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

	// 存不存
	public boolean isExistFile(String str) {
		if (str==null) {
			return false;
		}
		File TestItemFile = new File(str);
		if (TestItemFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public class VodVideoAdapter extends BaseAdapter {

		public VodVideoAdapter() {

		}

		public int getCount() {
			if (mMovieParse==null) {
				return 0;
			}else {
				return mMovieParse.getItemCount();
			}
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		// linearlayout里有两个TextView
		public View getView(int position, View convertView, ViewGroup parent) {
			AlwaysMarqueeTextView vodvideo_title;
			ImageView vodvideo_cover;
			TextView vodvideo_time, vodvideo_descrpition;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.vodvideo_widget_item,
						null);
			}
			
			ItemVideoInfo curItem=null;
			if (mMovieParse!=null) {
				curItem=mMovieParse.getCurInfo(position);
			}

			vodvideo_cover = (ImageView) convertView.findViewById(R.id.vodvideo_cover);
			
			
			if (curItem!=null&&isExistFile(curItem.getPicPath(position))) {
				Bitmap bm = BitmapFactory.decodeFile(curItem.getPicPath(0));
				Drawable drawable = new BitmapDrawable(bm);	
				vodvideo_cover.setBackgroundDrawable(drawable);	
			}else {
				vodvideo_cover.setBackgroundResource(R.drawable.video_load);
			}
			
			vodvideo_title = (AlwaysMarqueeTextView) convertView.findViewById(R.id.vodvideo_title);
			if (curItem!=null && curItem.getVideoName()!=null) {
				vodvideo_title.setText(curItem.getVideoName());
			}else {
				vodvideo_title.setText(R.string.vodvideo_widget_defvideo);
			}
		
			vodvideo_time = (TextView) convertView.findViewById(R.id.vodvideo_time);
			if (curItem!=null && curItem.movie_time!=null) {
				vodvideo_time.setText(curItem.movie_time);
			}else {
				vodvideo_time.setText(R.string.vodvideo_widget_deftime);		
			}
			
			vodvideo_descrpition = (TextView) convertView.findViewById(R.id.vodvideo_descrpition);
			if (curItem!=null && curItem.getVideoDes()!=null) {
				vodvideo_descrpition.setText(curItem.getVideoDes());
			}else {
				vodvideo_descrpition.setText(R.string.vodvideo_widget_decript);
			}
			
			return convertView;

		}
		
	}

}