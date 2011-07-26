package com.smit.DeskView.tvlive;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.smit.DeskView.commonclass.*;
import com.smit.DeskView.commonclass.TvLiveChannelParse.ItemTvInfo;
import com.smit.DeskView.tvlive.TVLiveFragment.GobalFunVar;
import com.smit.DeskView.vodvideo.VODVideoListFragment.VodVideoAdapter;
import com.smit.EasyLauncher.R;
import com.smit.EasyLauncher.R.string;

import android.R.integer;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TVLiveListFragment extends ListFragment {
	private LayoutInflater mInflater = null;
	int vodvideocount;
	private final static int GET_VOD_VIDEO_XML = 0x800;
	private final static String Tag = "TVLiveListFragment";
	public TvLiveChannelParse mtvParse = null;
	private AlertDialog.Builder mBuilderpass;
	private AlertDialog mAlertpass;
	private VodVideoAdapter listAdapter; 
	private boolean downProgramList=false;

	private static String TVLIVE_ITEM_FILE_DIR = "data/data/com.smit.EasyLauncher/files";// 视屏文件
	private static String TVLIVE_ITEM_FILE = "data/data/com.smit.EasyLauncher/files/tvlive.xml";// 视屏文件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mHandler.postDelayed(mRunnable, 5000);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		/*{

			String str = ReadVodVideoItemXML();
			if (str != null) {
				mtvParse = new TvLiveChannelParse(str);
				if (mtvParse != null) {
					mtvParse.parseDataStr();
					mtvParse.downloadMoviePic();
					mtvParse.downloadChannelProgramList();
					listAdapter= new VodVideoAdapter();
					
					setListAdapter(listAdapter);
					
				}

			}

		}*/

		getListView().setCacheColorHint(0);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(android.widget.AdapterView<?> arg0,
					View arg1, int arg2, long arg3) {
				ItemTvInfo curItem = null;
				if (mtvParse != null) {
					curItem = mtvParse.getCurInfo(arg2);
				}
				
				 if (curItem!=null) 
				 { 
					 Intent intent = new Intent();
					 intent.setClass(getActivity(),TvProgramListActivity.class);
					 Bundle myBund = new Bundle();// 创建Bundle，用于保存要传送的数据 String 
					 myBund.putString("tvname",curItem.tv_name);// KEY-VALUE保存起来 intent.putExtras(myBund);//
					 myBund.putString("tvprogramfilepath", curItem.channelPath);
					 intent.putExtras(myBund);
					 startActivity(intent); 
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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHandler!=null) {
			mHandler.removeCallbacks(mRunnable);
		}
	}
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VOD_VIDEO_XML: {
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					WriteVodVideoItemXML(str);
					mtvParse = new TvLiveChannelParse(str);
					mtvParse.parseDataStr();
					
					listAdapter= new VodVideoAdapter();			
					setListAdapter(listAdapter);
				}
				break;

			}
			case 2:
				break;
			}
		}
	};

	public boolean ShowCurList() {
		String str = ReadVodVideoItemXML();
		if (str != null) {
			mtvParse = new TvLiveChannelParse(str);
			if (mtvParse != null) {
				mtvParse.parseDataStr();
				mtvParse.downloadMoviePic();
				if (!downProgramList) {
					mtvParse.downloadChannelProgramList();
					downProgramList=true;
				}	
				listAdapter=new VodVideoAdapter();
				setListAdapter(listAdapter);
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public void ParseXml(String str) {

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

	public void requestXml() {
		String Url = CommonDataFun.myServerAddr + "video.do?columnKey=102";
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
		if (str == null) {
			return false;
		}
		File TestItemFile = new File(str);
		if (TestItemFile.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	private Runnable mRunnable = new Runnable() {
		public void run() {
			if(listAdapter!=null&&!listAdapter.isEmpty())
			{
				listAdapter.notifyDataSetChanged();		
			}
			mHandler.postDelayed(mRunnable, 5000);
		}

	};

	public class VodVideoAdapter extends BaseAdapter {

		public String GetCurData() {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String string = null;
			string = String.format("%d/%02d/%02d", (int) (year % 100), month,
					day);
			return string;
		}

		public int GetCurTime() {
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR);
			int minute = calendar.get(Calendar.MINUTE);
			Time m_time=new Time();
			m_time.setToNow();		
			return m_time.hour * 60 + m_time.minute;
		}

		// 得到当前播放节目
		public String getCurPlayProgram(ItemTvInfo curItem) {
			String strCurProgram = null;
			do {
				try {
					if (curItem == null) {
						break;
					}
					
					
					InputStream is = null;
					byte[] data = null;
					String str = null;
					File file = new File(curItem.channelPath);
					if (!file.exists()) {
						break;
					}
					int length = (int) file.length() + 10;
					data = new byte[length];
					is = new BufferedInputStream(new FileInputStream(file));
					while (is.read(data) != -1);
					is.close();

					str = new String(data);

					ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
					InputSource mInputSource = new InputSource(stream);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder dbuilder = dbf.newDocumentBuilder();
					Document doc = dbuilder.parse(mInputSource);

					String SystemData = GetCurData();
					int SystemTime = GetCurTime();

					NodeList n = doc.getElementsByTagName("entry");
					int itemcount = n.getLength();
					if (itemcount <= 0) {
						return null;
					}
					for (int i = 0; i < itemcount; i++) {// 所有entry
						Node item = n.item(i);
						if (!item.hasChildNodes()) {
							continue;
						}

						NamedNodeMap Attributes = item.getAttributes();
						Node timeNode = Attributes.getNamedItem("date");
						String timeValue = timeNode.getNodeValue();
						if (timeValue.equals(SystemData)) { // data相同
							NodeList nodeList = item.getChildNodes();
							int len = nodeList.getLength();
							if (len <= 0) {
								continue;
							}
							for (int j = 0; j < len; j++) { // item
								Node tempNodeFront = nodeList.item(j);
								Node tempNodeBehind = null;
								if (j == len - 1) {
									tempNodeBehind = null;
								} else {
									tempNodeBehind = nodeList.item(j + 1);
								}
								int frontTime = 0, behindTime = 0;
								String programString = null;

								NodeList List = tempNodeFront.getChildNodes();
								for (int k = 0; k < List.getLength(); k++) {// item
																			// child
									Node tmpnode;
									Node tempNode = List.item(k);
									String tempStr = tempNode.getNodeName();
									if (tempStr.equals("time")) {
										tmpnode = tempNode.getChildNodes().item(0);
										if (tmpnode != null) {
											String[] lunars = tmpnode.getNodeValue().split(":");
											frontTime = Integer.parseInt(lunars[0])* 60+ Integer.parseInt(lunars[1]);
										} else {

										}

									} else if (tempStr.equals("program")) {
										programString = tempNode.getChildNodes().item(0).getNodeValue();
									}
								}

								if (tempNodeBehind == null) {
									behindTime = frontTime + 100;
								} else {
									List = tempNodeBehind.getChildNodes();
									for (int k = 0; k < List.getLength(); k++) {
										Node tmpnode;
										Node tempNode = List.item(k);
										String tempStr = tempNode.getNodeName();
										if (tempStr.equals("time")) {
											tmpnode = tempNode.getChildNodes().item(0);
											if (tmpnode != null) {
												String[] lunars = tmpnode.getNodeValue().split(":");
												behindTime = Integer.parseInt(lunars[0])* 60 + Integer.parseInt(lunars[1]);
											} else {

											}

										} else if (tempStr.equals("program")) {
											break;
										}
									}
								}

								if (SystemTime >= frontTime
										&& SystemTime < behindTime) {
									strCurProgram = programString;
								}

							}
						}

					}
				} catch (Exception e) {
					Log.e(Tag, "======" + e.toString() + "======");
				}
			} while (false);

			return strCurProgram;
		}

		public VodVideoAdapter() {

		}

		public int getCount() {
			if (mtvParse == null) {
				return 0;
			} else {
				return mtvParse.getItemCount();
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
			TextView vodvideo_time, tvlive_isplay;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.tvlive_widget_item,
						null);
			}

			ItemTvInfo curItem = null;
			if (mtvParse != null) {
				curItem = mtvParse.getCurInfo(position);
			}

			vodvideo_cover = (ImageView) convertView
					.findViewById(R.id.tvlive_cover);

			if (curItem != null && isExistFile(curItem.getPicPath(0))) {
				Bitmap bm = BitmapFactory.decodeFile(curItem.getPicPath(0));
				Drawable drawable = new BitmapDrawable(bm);
				if (bm == null || drawable == null) {
					vodvideo_cover.setBackgroundResource(R.drawable.video_load);
				} else {
					vodvideo_cover.setBackgroundDrawable(drawable);
				}
			} else {
				vodvideo_cover.setBackgroundResource(R.drawable.video_load);
			}

			vodvideo_title = (AlwaysMarqueeTextView) convertView
					.findViewById(R.id.tvlive_title);
			if (curItem != null && curItem.getTVName() != null
					&& curItem.getTVName().length() > 0) {
				vodvideo_title.setText(curItem.getTVName());
			} else {
				vodvideo_title.setText(R.string.vodvideo_widget_defvideo);
			}

			String isplayingString=getCurPlayProgram(curItem);
			tvlive_isplay = (TextView) convertView
					.findViewById(R.id.tvlive_isplay);
			if (isplayingString!=null) {
				tvlive_isplay.setText(getResources().getString(R.string.tvlive_widget_isplaying)+isplayingString);
			}else {
				tvlive_isplay.setText("");
			}

			return convertView;

		}

	}

}