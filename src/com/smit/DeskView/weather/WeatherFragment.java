package com.smit.DeskView.weather;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import com.smit.DeskView.commonclass.AlwaysMarqueeTextView;
import com.smit.DeskView.commonclass.HistoryCityContentProvider;
import com.smit.DeskView.commonclass.WeatherParse;
import com.smit.DeskView.commonclass.RequestXml;
import com.smit.DeskView.commonclass.VodVideoMoveParse;
import com.smit.DeskView.commonclass.VodVideoMoveParse.ItemVideoInfo;
import com.smit.DeskView.commonclass.WeatherParse.Currforecast;
import com.smit.DeskView.tvlive.TvProgramListActivity;
import com.smit.DeskView.vodvideo.VODVideoListFragment.VodVideoAdapter;
import com.smit.EasyLauncher.R;


import android.R.integer;
import android.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WeatherFragment extends Fragment {
	private ImageView moreImage;
	private final static int GET_VOD_VIDEO_XML = 0x0001;
	private final static int GET_WEATHER_INFO = 0x800;
	private final static String Tag = "VODVideoFragment";
	public VodVideoMoveParse mMovieParse = null;
	
	private static String CITY_ITEM_FILE_DIR ="data/data/smit.com.factorytest/files";	//测试项文件
	private static String CITY_ITEM_FILE_HISTORY ="data/data/smit.com.factorytest/files/cityhistory.xml";//测试项文件
	
	private LinearLayout backweather;
	private TextView mTextCity, mTextTemp;
	private ImageView mImageWeather;
	private View mViewpopWeatherView;
	private PopupWindow mPopupWindow=null;				//弹出窗口
	private FrameLayout mFrameChangeCity,mFrameHotCity,mFrameSearchCity;
	private Button mImageWeatherBack,mImageWeatherSearch;
	private ImageView mImageWeatherSearchpic;
	private TextView mTextChangeCity,mTextHistory,mTextHotCity;
	private LinearLayout mLinHotCity;
	private ListView mListHistory,mListHotCity,mListSearchCity;
	private EditText mAutoText;
	public  LinkedList<String> historyList=new LinkedList<String>();
	public  LinkedList<String> searchResult=new LinkedList<String>();
	private static final Uri CONTENT_URI  = Uri.parse("content://com.smit.DeskView.commonclass.HistoryCityContentProvider");
	private Cursor mCursorHistoryCity=null;
	HistoryCityContentProvider mydatabase;
	private WeatherParse wpWeatherdata = null;
	
	private static final int WidthPIX=800;
	private static final int HeightPIX=480;
	private static final int UpSpace=80;
	private static final int RightSpaceLand=295;
	private static final int RightSpacePort=250;
	

	public final static int SHOW_HISTORY = 0;
	public final static int SHOW_HOTCITY = 1;
	public final static int SHOW_SEARCHCITY = 2;

	private Timer mTimer;
	private SharedPreferences mPerferences = null; // 共享数据
	private String CITY="CITY", TEMP="TEMP", WEATHER="WEATHER";
	private String curCity, defCity = "beijing"; // 保存城市 默认北京
	private int curTemp, defTemp = 20; // 保存温度 默认温度20
	private int curWeather, defWeather = 6; // 保存天气 默认天气晴

	public static boolean existInstance = false;
	//private Animation weather_in_amation;
	
	public int forecast_fail=-1;
	public int forecast_none=0;
	public int forecast_heavyrain=1;
	public int forecast_lightrain=2;
	public int forecast_cloudy=3;
	public int forecast_thunderstorm=4;
	public int forecast_haze=5;
	public int forecast_sunny=6;
	public int forecast_overcast=7;
	public int forecast_mostly_sunny=8;
	public int forecast_storm=9;
	public int forecast_snow=10;
	
	// 天气图片
	public int forecastpic[] = { R.drawable.weather_unknow,
			R.drawable.weather_cn_heavyrain, R.drawable.weather_cn_lightrain,
			R.drawable.weather_cn_cloudy, R.drawable.weather_thunderstorm,
			R.drawable.weather_haze, R.drawable.weather_sunny,
			R.drawable.weather_cn_overcast, R.drawable.weather_mostly_sunny,
			R.drawable.weather_storm, R.drawable.weather_snow };
	
	

	/*********************** 城市 ********************************/
	//private static final int[] CITY_NAME={R.array.cityname};
	//private static final int[] CITY_PINYIN={R.array.citypinyin};
	private String city_name[];
	private String city_pinyin[];
	
	private String mStringHotCityName[];
	private String mStringHotCityPinyin[];

	/*******************************************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		existInstance=true;
		setRetainInstance(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		city_name=getResources().getStringArray(R.array.cityname);
		city_pinyin=getResources().getStringArray(R.array.citypinyin);
		
		mStringHotCityName=getResources().getStringArray(R.array.hotcityname);
		mStringHotCityPinyin=getResources().getStringArray(R.array.hotcitypinyin);
		
		setupView();
		//weather_in_amation=AnimationUtils.loadAnimation(getActivity(), R.anim.wether_alpha_translate_in);
		if (existInstance) {
			getWeather();
			showWeather();
			mTimer=new Timer();
			mTimer.schedule(task, 5000, 30000);
		}else {
			getWeather();
			showWeather();
		}
		existInstance=false;
		
		/*
		 * if(checkWifiIscon()) { requestXml(); mHandler.postDelayed(mRunnable,
		 * 700);
		 * 
		 * SetCurShow(SHOW_LAODING); }else { String str=ReadVodVideoItemXML();
		 * if (str!=null) { mMovieParse = new VodVideoMoveParse(str);
		 * mMovieParse.parseDataStr(); } if (IsExistvodMove(mMovieParse)) {
		 * SetCurShow(SHOW_LIST); showVodVideoList(); }else {
		 * SetCurShow(SHOW_FLASH); } }
		 */

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.weather_widget_home_page, container,
				false);

		// return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		
		if(mPopupWindow!=null){
			mPopupWindow.dismiss();
			mPopupWindow=null;
		}
		
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
		
		if(mTimer!=null){
			mTimer.cancel();
			mTimer.purge();
		}
		if (wpWeatherdata!=null) {
			wpWeatherdata.stopGetData();
			wpWeatherdata=null;
		}
		if (mCursorHistoryCity!=null) {
			mCursorHistoryCity.close();
			mCursorHistoryCity=null;
		}

	}

	public void setupView() {

		backweather = (LinearLayout) getView().findViewById(R.id.wetherbackid);
		backweather.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PopWeatherSet(0, 0);
				
				/* Intent intent = new Intent();
				 intent.setClass(getActivity(),TvProgramListActivity.class);
				 Bundle myBund = new Bundle();
				 myBund.putString("tvname","abc");
				 myBund.putString("tvprogramfilepath", "edf");
				 intent.putExtras(myBund);
				 startActivity(intent); */
			}
		});
		
		mTextCity = (TextView) getView().findViewById(R.id.weathercity);
		mTextTemp = (TextView) getView().findViewById(R.id.weathertemp);
		mImageWeather = (ImageView) getView().findViewById(R.id.wetherpic);
	}

	
	private TimerTask task = new TimerTask(){
        public void run() {
        	getWeather();
        	startgetWeather(curCity);
        }
    };  

    
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VOD_VIDEO_XML: {
				break;

			}
			case GET_WEATHER_INFO: {
				String str = (String) msg.obj;
				if (str != null && str.length() > 0) {
					int ret = wpWeatherdata.parseData(str);
					if (ret > 0) {
						
						Currforecast curWeather = wpWeatherdata.getCurWeather();
						if (curWeather != null && curWeather.icon != null
								&& curWeather.temp_c != 0) {

							UpdateWeather(curCity, curWeather.temp_c,
									getconditionindex(curWeather.icon));
							getWeather();
							showWeather();
						} else {

						}
					} else {

					}
				} else {

				}
				break;
			}
			}
		}
	};

	// 从iconURL中得到天气串
	public  String getcurconditionstr(String iconurl) {
		String nRet = null;
		int pos1 = -1, pos2 = -1;
		do {
			if (null == iconurl) {
				break;
			}
			pos1 = iconurl.lastIndexOf('/');
			if (pos1 < 0) {
				break;
			}
			pos2 = iconurl.indexOf('.', pos1);
			if (pos2 < 0) {
				break;
			}
			if ((pos1 + 1) == pos2) {
				break;
			}
			nRet = iconurl.substring(pos1 + 1, pos2);
		} while (false);
		return nRet;
	}
	
	
	// 得到天气索引
	public int getconditionindex(String websrcString) {
		String weatherState=null;
		
		if (null == websrcString) {
			return forecast_sunny;
		}
		weatherState=getcurconditionstr(websrcString);
		if (weatherState==null) {
			return forecast_sunny;
		}
		
		int forecastsort = forecast_none;
		if (weatherState.equals("cn_heavyrain")) {
			forecastsort = forecast_heavyrain;
		} else if (weatherState.equals("cn_lightrain")
				|| weatherState.equals("chance_of_rain")) {
			forecastsort = forecast_lightrain;
		} else if (weatherState.equals("cn_cloudy")
				|| weatherState.equals("mostly_cloudy")
				|| weatherState.equals("partly_cloudy")) {
			forecastsort = forecast_cloudy;
		} else if (weatherState.equals("thunderstorm")) {
			forecastsort = forecast_thunderstorm;
		} else if (weatherState.equals("haze") || weatherState.equals("cn_fog")) {
			forecastsort = forecast_haze;
		} else if (weatherState.equals("sunny")) {
			forecastsort = forecast_sunny;
		} else if (weatherState.equals("cn_overcast")
				|| weatherState.equals("overcast")) {
			forecastsort = forecast_overcast;
		} else if (weatherState.equals("mostly_sunny")) {
			forecastsort = forecast_mostly_sunny;
		} else if (weatherState.equals("chance_of_storm")
				|| weatherState.equals("storm")) {
			forecastsort = forecast_storm;
		} else if (weatherState.equals("cn_snow")
				|| weatherState.equals("snow")
				|| weatherState.equals("chance_of_snow")) {
			forecastsort = forecast_snow;
		} else {
			forecastsort = forecast_none;
		}
		return forecastsort;
	}

	// 开始取天气
	public void startgetWeather(String city) {
		String str;
		try {
			
			//curCity="深圳";
			
			wpWeatherdata = new WeatherParse();
			str = "http://www.google.com/ig/api?hl=zh-cn&weather=" + city;
			URL url = new URL(str);
			wpWeatherdata.startGetData(url, mHandler, GET_WEATHER_INFO);
		} catch (Exception e) {
			Log.e(Tag, e.toString());
		}
	}
	
	public String getCityName(String pinyin){
		String str=null;
		int count=city_pinyin.length;
		
		for(int i=0;i<count;++i){
			if (pinyin.equalsIgnoreCase(city_pinyin[i])) {
				str=city_name[i];
			}
		}
		
		return str;
		
	}
	
	public String getCityPinyin(String pinyin){
		String str=null;
		int count=city_pinyin.length;
		
		for(int i=0;i<count;++i){
			if (pinyin.equalsIgnoreCase(city_name[i])) {
				str=city_pinyin[i];
			}
		}
		
		return str;
		
	}

	public void showWeather() {
		if (mTextCity==null||mTextTemp==null||mImageWeather==null) {
			return;
		}
		mTextCity.setText(getCityName(curCity));
		mTextTemp.setText(curTemp + "℃");
		mImageWeather.setBackgroundResource(forecastpic[curWeather]);

	}

	// 更新天气
	public void getWeather() {
		if (getActivity()==null) {
			return;
		}
		mPerferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (mPerferences!=null) {
			curCity = mPerferences.getString(CITY, defCity);
			curTemp = mPerferences.getInt(TEMP, defTemp);
			curWeather = mPerferences.getInt(WEATHER, defWeather);
		}
	
	}

	public void UpdateWeather(String city, int temp, int weather) {
		if (getActivity()==null) {
			return;
		}
		mPerferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (mPerferences!=null) {
			SharedPreferences.Editor editor = mPerferences.edit();
			editor.putString(CITY, city);
			editor.putInt(TEMP, temp);
			editor.putInt(WEATHER, weather);
			editor.commit();
		}
		
	}
	
	
	public void setupPopView(){
		
		mFrameChangeCity=(FrameLayout)mViewpopWeatherView.findViewById(R.id.weatherframe_changecity);
		mFrameHotCity=(FrameLayout)mViewpopWeatherView.findViewById(R.id.weatherframe_hotcity);
		mFrameSearchCity=(FrameLayout)mViewpopWeatherView.findViewById(R.id.weatherframe_searchcity);
		
		mImageWeatherBack=(Button)mViewpopWeatherView.findViewById(R.id.weatherpopback);
		mImageWeatherSearch=(Button)mViewpopWeatherView.findViewById(R.id.wetherpopsearch);
		mImageWeatherSearchpic=(ImageView)mViewpopWeatherView.findViewById(R.id.wetherpopsearchpic);
		mTextChangeCity=(TextView)mViewpopWeatherView.findViewById(R.id.weatherpoptips);
		mTextHistory=(TextView)mViewpopWeatherView.findViewById(R.id.weatherpophistory);
		mLinHotCity=(LinearLayout)mViewpopWeatherView.findViewById(R.id.weatherpophotcityLinear);
		mLinHotCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SetCurShow(SHOW_HOTCITY);
			}
		});
		/*mTextHotCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SetCurShow(SHOW_HOTCITY);
			}
		});*/
		mListHistory=(ListView)mViewpopWeatherView.findViewById(R.id.weatherpophistorylist);
		mListHotCity=(ListView)mViewpopWeatherView.findViewById(R.id.weatherpophotcitylist);
		mListSearchCity=(ListView)mViewpopWeatherView.findViewById(R.id.weatherpopsearchlist);
		mAutoText=(EditText)mViewpopWeatherView.findViewById(R.id.weatherpopsearchedit);
	}
	
	public void PopWeatherSet(int x,int y)
	{
		int posx=0,posy=0;
		
		try {

			LayoutInflater inflater = LayoutInflater.from(getActivity());
			mViewpopWeatherView = inflater.inflate(R.layout.weather_pop_window_set_city, null);		
			mPopupWindow= new PopupWindow(mViewpopWeatherView);
			Drawable win_bg = getActivity().getResources().getDrawable(R.drawable.s0_city_selector_bg);
			mPopupWindow.setBackgroundDrawable(win_bg); //加这两句才能点其它地方消失 
			mPopupWindow.setFocusable(true);  
			
			if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				posx=WidthPIX-RightSpaceLand;
				posy=UpSpace;
			}else{
				posx=HeightPIX-RightSpacePort;
				posy=UpSpace;
			}		
				
			mPopupWindow.showAtLocation(getView(), Gravity.NO_GRAVITY, posx,posy);		
			mPopupWindow.update(250, 300);
			setupPopView();
			SetCurShow(SHOW_HISTORY);
			
			//mViewpopWeatherView.setAnimation(weather_in_amation);
			
			
			
		} catch (Exception e) {
			Log.e(Tag, e.toString());
		}
	}
	
	public void SetCurShow(int curStatus){
		switch (curStatus) {
		case SHOW_HISTORY:
		default:
		{		
			mFrameChangeCity.setVisibility(View.VISIBLE);
			mFrameHotCity.setVisibility(View.GONE);
			mFrameSearchCity.setVisibility(View.GONE);
			mImageWeatherBack.setVisibility(View.GONE);
			mTextChangeCity.setText(R.string.weather_pop_change_city);
			mImageWeatherSearch.setVisibility(View.VISIBLE);
			mImageWeatherSearch.setText("");
			mImageWeatherSearch.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SetCurShow(SHOW_SEARCHCITY);
				}
			});
			mImageWeatherSearchpic.setVisibility(View.VISIBLE);
			
			mListHistory.setAdapter(new HistoryCityAdapter());
			mListHistory.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
						mCursorHistoryCity.moveToPosition(arg2);
						String strCityName=mCursorHistoryCity.getString(1);	
						
						curCity=strCityName;
						startgetWeather(curCity);
						mPopupWindow.dismiss();
						
						UpdateWeather(curCity, defTemp,defWeather);
						getWeather();
						showWeather();
						
						//InsertHistoryCityDatabase(curCity);
					
				}
			});
			break;
		}
		case SHOW_HOTCITY:{
			mFrameHotCity.setVisibility(View.VISIBLE);
			mFrameChangeCity.setVisibility(View.GONE);
			mFrameSearchCity.setVisibility(View.GONE);
			
			mImageWeatherBack.setVisibility(View.VISIBLE);
			mImageWeatherBack.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SetCurShow(SHOW_HISTORY);
					
				}
			});
			mTextChangeCity.setText(R.string.weather_pop_hotcity);
			mImageWeatherSearch.setVisibility(View.VISIBLE);
			mImageWeatherSearch.setText("");
			mImageWeatherSearch.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SetCurShow(SHOW_SEARCHCITY);
				}
			});
			mImageWeatherSearchpic.setVisibility(View.VISIBLE);
			
			
			mListHotCity.setAdapter(new HotCityAdapter());
			mListHotCity.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String strCityName=mStringHotCityPinyin[arg2];		
					
						curCity=strCityName;
						startgetWeather(curCity);
						mPopupWindow.dismiss();
						
						UpdateWeather(curCity, defTemp,defWeather);
						getWeather();
						showWeather();
						
						InsertHistoryCityDatabase(curCity);
					
				}
			});
			break;
		}
		case SHOW_SEARCHCITY:{
			mFrameSearchCity.setVisibility(View.VISIBLE);
			mFrameChangeCity.setVisibility(View.GONE);
			mFrameHotCity.setVisibility(View.GONE);
			
			mImageWeatherBack.setVisibility(View.GONE);
			mTextChangeCity.setText(R.string.weather_pop_inputcity);
			mImageWeatherSearch.setText(R.string.weather_cancel);
			mImageWeatherSearch.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SetCurShow(SHOW_HISTORY);
				}
			});
			mImageWeatherSearchpic.setVisibility(View.GONE);
			
			searchResult=getSearchResult(null);
			mListSearchCity.setAdapter(new SearchCityAdapter());
			mListSearchCity.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String strCityName=searchResult.get(arg2);		
					String cityStr=getResources().getString(R.string.weather_noresult); //没搜到
					if (strCityName.equals(cityStr)) {
						//清掉edit
						mAutoText.setText("");
						searchResult=getSearchResult(null);
						mListSearchCity.setAdapter(new SearchCityAdapter());
						
					}else {
						//String currTmpCity=getCityPinyin(searchResult.get(arg2));
						curCity=strCityName;
						startgetWeather(curCity);
						mPopupWindow.dismiss();
						
						UpdateWeather(curCity, defTemp,defWeather);
						getWeather();
						showWeather();
						
						InsertHistoryCityDatabase(curCity);
					}
					
				}
			});
			
			mAutoText.addTextChangedListener(mTextWatcher);
			
			break;
		}
		
		}
	}
	
	//得到搜索结果
	public LinkedList<String> getSearchResult(String str){
		LinkedList<String> tmpResult=new LinkedList<String>();
		int count;
		if (str==null||str.length()<=0) {
			count=city_pinyin.length;
			for(int i=0;i<count; ++i){
				tmpResult.add(city_pinyin[i]);
			}
		}else {
			count=city_pinyin.length;
			String strLetter=str.toLowerCase(); //字母转为小写
			for(int i=0;i<count; ++i){
				if (city_pinyin[i].indexOf(strLetter)>=0) {
					tmpResult.add(city_pinyin[i]);
				}else {
					
				}
			}
			
			if (tmpResult.size()<=0) {
				count=city_pinyin.length;
				for(int i=0;i<count; ++i){
					if (city_name[i].indexOf(str)>=0) {
						tmpResult.add(city_pinyin[i]);
					}else {
						
					}
				}
			}
			
		}
		
		if (tmpResult.size()<=0) {
			String myString=getResources().getString(R.string.weather_noresult);
			tmpResult.add(myString);
		}
		
		return tmpResult;
	}	
	
	//写入历史记录数据库
	public void InsertHistoryCityDatabase(String str){
		Cursor mCursor;
		
		mCursor=getActivity().getContentResolver().query(CONTENT_URI, null, "_city_pinyin = ?", new String[] { str }, null);
		if (mCursor.getCount()>0) {
			getActivity().getContentResolver().delete(CONTENT_URI, "_city_pinyin = ?", new String[] { str });
		}else {		
		}
		
		ContentValues values = new ContentValues();
		values.put("_city_pinyin", str);
		getActivity().getContentResolver().insert(CONTENT_URI, values);
		
		mCursor.close();
		mCursor=null;
		
	}
	
	public class HistoryCityAdapter extends BaseAdapter {

		public HistoryCityAdapter() {
			if (mCursorHistoryCity!=null) {
				mCursorHistoryCity.close();
				mCursorHistoryCity=null;
			}
			mCursorHistoryCity=getActivity().getContentResolver().query(CONTENT_URI, null, null, null, "_id" + " DESC");	
		}

		public int getCount() {
			int count=mCursorHistoryCity.getCount();
			return count;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			TextView cityname;
			String mStringCityName;

			if (convertView == null) {
				cityname=new TextView(getActivity());
			}else {
				cityname=(TextView)convertView;
			}	
			
			mCursorHistoryCity.moveToPosition(position);
			mStringCityName=getCityName(mCursorHistoryCity.getString(1));
			cityname.setText(mStringCityName);
			cityname.setTextColor(getResources().getColor(R.color.myblack));
			cityname.setTextSize(20);
			cityname.setHeight(30);
			convertView=cityname;
			
			return convertView;

		}
		
	}
	
	public class HotCityAdapter extends BaseAdapter {

		public HotCityAdapter() {

		}

		public int getCount() {
			int count=mStringHotCityName.length;
			return count;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			TextView cityname;

			if (convertView == null) {
				cityname=new TextView(getActivity());
			}else {
				cityname=(TextView)convertView;
			}	
			cityname.setText(mStringHotCityName[position]);
			cityname.setTextColor(getResources().getColor(R.color.myblack));
			cityname.setTextSize(20);
			cityname.setHeight(30);
			convertView=cityname;
			
			return convertView;

		}
		
	}
	
	public class SearchCityAdapter extends BaseAdapter {

		public SearchCityAdapter() {
			
		}

		public int getCount() {
			int count=searchResult.size();
			return count;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			TextView cityname;
			String  pinyinStr,cityStr;
			if (convertView == null) {
				cityname=new TextView(getActivity());
				
			}else {
				cityname=(TextView)convertView;
			}
			
			pinyinStr=searchResult.get(position);
			cityStr=getCityName(pinyinStr);
			if (cityStr==null) {
				cityStr=getResources().getString(R.string.weather_noresult);
			}
			cityname.setText(cityStr);
			cityname.setTextColor(getResources().getColor(R.color.myblack));
			cityname.setTextSize(20);
			cityname.setHeight(30);
			convertView=cityname;
			
			return convertView;

		}
		
	}	
	
	 TextWatcher mTextWatcher = new TextWatcher() {
	        private CharSequence temp;
	        private int editStart ;
	        private int editEnd ;
	        @Override
	        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
	                int arg3) {
	            //temp = s;
	        }
	       
	        @Override
	        public void onTextChanged(CharSequence s, int arg1, int arg2,
	                int arg3) {
	           
	        }
	       
	        @Override
	        public void afterTextChanged(Editable s) {
	        	String str=null;
	        	str=mAutoText.getText().toString();
	        	
	        	searchResult=getSearchResult(str);
	        	mListSearchCity.setAdapter(new SearchCityAdapter());
	        	
	        }
	    };


}