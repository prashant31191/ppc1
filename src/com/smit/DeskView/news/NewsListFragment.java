package com.smit.DeskView.news;

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
import com.smit.DeskView.commonclass.NewsMoveParse.ItemNewsInfo;
import com.smit.DeskView.commonclass.VodVideoMoveParse.ItemVideoInfo;
import com.smit.DeskView.vodvideo.VODVideoListFragment.VodVideoAdapter;
import com.smit.EasyLauncher.R;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListFragment extends ListFragment {
	private LayoutInflater mInflater = null;
	int vodvideocount;
	private final static int GET_VOD_VIDEO_XML = 0x800;
	private final static String Tag = "VODVideoListFragment";
	public NewsMoveParse mMovieParse = null;
	private AlertDialog.Builder mBuilderpass;
	private AlertDialog mAlertpass;
	WebView mWebView;
	
	private static String VIDEO_ITEM_FILE_DIR = "data/data/com.smit.EasyLauncher/files";// 视屏文件
	private static String VIDEO_ITEM_FILE = "data/data/com.smit.EasyLauncher/files/news.xml";// 视屏文件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
	}
	
	  private void setWebViewClient()
	    {
		  
	    	WebViewClient wvc = new WebViewClient() {
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                mWebView.loadUrl(url);
	                // 记得消耗掉这个事件。给不知道的朋友再解释一下，Android中返回True的意思就是到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
	                return true;
	            }

	            @Override
	            public void onPageStarted(WebView view, String url, Bitmap favicon){
	                //Toast.makeText(getApplicationContext(), "WebViewClient.onPageStarted", Toast.LENGTH_SHORT).show();
	                super.onPageStarted(view, url, favicon);
	            }

	            @Override
	            public void onPageFinished(WebView view, String url) {
	                //Toast.makeText(getApplicationContext(), "WebViewClient.onPageFinished", Toast.LENGTH_SHORT).show();
	                super.onPageFinished(view, url);
	            }

	            @Override
	            public void onLoadResource(WebView view, String url) {
	                //Toast.makeText(getApplicationContext(), "WebViewClient.onLoadResource", Toast.LENGTH_SHORT).show();
	                super.onLoadResource(view, url);
	            }
	        };
	       
	        mWebView.setWebViewClient(wvc);
	    }

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

/*			{
			String str=ReadVodVideoItemXML();
			if (str!=null) {
				mMovieParse = new NewsMoveParse(str);
				if (mMovieParse!=null) {
					mMovieParse.parseDataStr();
					setListAdapter(new VodVideoAdapter());
				}		
			}
			
			
		}*/		
			
		getListView().setCacheColorHint(0);		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ItemNewsInfo curItem=null;
				if (mMovieParse!=null) {
					curItem=mMovieParse.getCurInfo(arg2);
				}
				
				if (curItem!=null && curItem.getNewsLink()!=null
						&&curItem.getNewsLink().length()>0) {
					
			/*		mWebView=new WebView(getActivity());
					LayoutParams myParams=new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
					mWebView.setLayoutParams(myParams); 
					setWebViewClient();
					mWebView.loadUrl(curItem.getNewsLink());*/
					
					
					Intent intent = new Intent();
					intent.setClass(getActivity(), FlashPlayerActivity.class);
					Bundle myBund = new Bundle();// 创建Bundle，用于保存要传送的数据
					String mystr = curItem.getNewsLink();
					myBund.putString("media", mystr);// KEY-VALUE保存起来
					intent.putExtras(myBund);// 设置Intent要传送的包
					getActivity().startActivity(intent);
					
				}
				
			};
		});
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

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VOD_VIDEO_XML: {
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					WriteVodVideoItemXML(str);
					mMovieParse = new NewsMoveParse(str);
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

	public boolean ShowCurList(){
		String str=ReadVodVideoItemXML();
		if (str!=null) {
			mMovieParse = new NewsMoveParse(str);
			if (mMovieParse!=null) {
				mMovieParse.parseDataStr();
				setListAdapter(new VodVideoAdapter());	
			}else {
				return false;
			}		
		}else {
			return false;
		}	
		return true;
	}
	
	public void ParseXml(String str) {

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
		String Url = CommonDataFun.myServerAddr+"video.do?columnKey=316";
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
			AlwaysMarqueeTextView news_title;
			TextView news_descrpition;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.news_widget_item,
						null);
			}
			
			ItemNewsInfo curItem=null;
			if (mMovieParse!=null) {
				curItem=mMovieParse.getCurInfo(position);
			}

		
			
			news_title = (AlwaysMarqueeTextView) convertView.findViewById(R.id.news_title);
			if (curItem!=null && curItem.getNewsTitle()!=null&&curItem.getNewsTitle().length()>0) {
				news_title.setText(curItem.getNewsTitle());
			}else {
				news_title.setText(R.string.vodvideo_widget_defvideo);
			}
			
			news_descrpition = (TextView) convertView.findViewById(R.id.news_descript);
			if (curItem!=null && curItem.getNewsDes()!=null&&curItem.getNewsDes().length()>0) {
				news_descrpition.setText(curItem.getNewsDes());
			}else {
				news_descrpition.setText(R.string.vodvideo_widget_decript);
			}
			
			return convertView;

		}
		
	}
    
}