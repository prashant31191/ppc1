package com.smit.DeskView.commonclass;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.R.array;
import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.text.GetChars;
import android.util.Log;

//解析天气xml文件
public class WeatherParse {
	// private SmitHomeStyle m_activity=null; //Activity引
	private static final String Tag = "WeatherParse";
	public CurrInfo currinfo = null; // 取到正确的
	public CurrInfo tmpcurrinfo = null; // 临时的
	public Currforecast currforecast = null; // 正确的
	public Currforecast tmpcurrforecast = null; // 临时
	public ArrayList<Dayforecast> dayforecastArrayList = null; // 正确
	public ArrayList<Dayforecast> tmpdayforecastArrayList = null; // 临时
	private static final int CURRINFO_ERROR = -1;
	private static final int CURRFORECAST_ERROR = -2;
	private static final int DAYSFORECAST_ERROR = -3;
	private static final int PARSE_SUCCESS = 1;
	private static final int ERROR_TMP = -8000;
	public getXmlThread thread=null;

	public WeatherParse() {
		// m_activity=activity;
		currinfo = new CurrInfo();
		tmpcurrinfo = new CurrInfo();
		currforecast = new Currforecast();
		currforecast.InitCurrInfo();
		tmpcurrforecast = new Currforecast();
		dayforecastArrayList = new ArrayList<Dayforecast>();
		tmpdayforecastArrayList = new ArrayList<Dayforecast>();

	}

	// 开始得到数据
	public void startGetData(URL url, Handler handler, int msg) {	
			thread = new getXmlThread(url, handler, msg);
			thread.start();
	}
	
	// 停止线程
	public void stopGetData() {
		if (thread!=null) {
			thread.stop();
			thread.stopThread();
		}
	}

	// 取xml数据是否正常
	public boolean DateIsRight() {
		/*
		 * if (null==getXmlThread.getInputSource()) { return false; }else {
		 * return true; }
		 */

		return true;
	}

	// 天气是否存在
	public boolean WeathIsExist() {
		if (currforecast.icon == null && currforecast.temp_c == ERROR_TMP
				&& currforecast.condition == null) {
			return false;
		} else {
			return true;
		}
	}

	public Currforecast getCurWeather(){
		return currforecast;
	}
	
	// 取当前温度
	public int Getcurtemp() {
		int curTemp;
		Dayforecast tempDayforecast = tmpdayforecastArrayList.get(0);

		if (currforecast.temp_c > tempDayforecast.high
				|| currforecast.temp_c < tempDayforecast.low) {
			curTemp = currforecast.temp_c;
		} else {
			curTemp = (tempDayforecast.high + tempDayforecast.low) / 2;
		}
		return curTemp;
	}

	// 从iconURL中得到天气串
	public static String getcurconditionstr(String iconurl) {
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
	public int getconditionindex(String weatherState) {
		/*
		 * if (null==weatherState) { return m_activity.forecast_fail; } int
		 * forecastsort=m_activity.forecast_none;
		 * if(weatherState.equals("cn_heavyrain")){
		 * forecastsort=m_activity.forecast_heavyrain; }else
		 * if(weatherState.equals("cn_lightrain" ) ||
		 * weatherState.equals("chance_of_rain" )){
		 * forecastsort=m_activity.forecast_lightrain; }else
		 * if(weatherState.equals
		 * ("cn_cloudy")||weatherState.equals("mostly_cloudy") ||
		 * weatherState.equals("partly_cloudy")){
		 * forecastsort=m_activity.forecast_cloudy; }else
		 * if(weatherState.equals("thunderstorm")){
		 * forecastsort=m_activity.forecast_thunderstorm; }else
		 * if(weatherState.equals("haze") || weatherState.equals("cn_fog")){
		 * forecastsort=m_activity.forecast_haze; }else
		 * if(weatherState.equals("sunny")){
		 * forecastsort=m_activity.forecast_sunny; }else
		 * if(weatherState.equals("cn_overcast") ||
		 * weatherState.equals("overcast")){
		 * forecastsort=m_activity.forecast_overcast; } else
		 * if(weatherState.equals("mostly_sunny")){
		 * forecastsort=m_activity.forecast_mostly_sunny; }else
		 * if(weatherState.equals("chance_of_storm") ||
		 * weatherState.equals("storm")){
		 * forecastsort=m_activity.forecast_storm; }else
		 * if(weatherState.equals("cn_snow") || weatherState.equals("snow") ||
		 * weatherState.equals("chance_of_snow")){
		 * forecastsort=m_activity.forecast_snow; }else{
		 * forecastsort=m_activity.forecast_none; } return forecastsort;
		 */

		return 1;
	}

	// 取当前天气描述 图片
	public int GetcurconditionBm() {
		return getconditionindex(getcurconditionstr(currforecast.icon));
	}

	// 取当前天气描述 文字
	public String GetcurconditionTx() {
		return currforecast.condition;
	}

	boolean getDateSucess() {
		/*
		 * if (null==getXmlThread.getInputSource()) { return false; }else {
		 * return true; }
		 */

		return true;
	}
	
	

	// 解析数据
	public int parseData(String str) {
		if (str == null || str.length() <= 0) {
			return -1;
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		InputSource ispot = new InputSource(stream);

		if (ispot == null) {
			return -1;
		}

		try {
			//ispot.setEncoding("UTF-8");
			//ispot.setEncoding("GBK");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbf.newDocumentBuilder();
			Document doc = dbuilder.parse(ispot);

			// 得到当前信息
			tmpcurrinfo.InitCurrInfo();
			if (!getCurrInfo(doc, tmpcurrinfo)) {
				return CURRINFO_ERROR;
			} else {
				currinfo.InitCurrInfo();
				currinfo.memcpy(tmpcurrinfo);
			}
			// 得到当前天气
			tmpcurrforecast.InitCurrInfo();
			if (!getCurrforecast(doc, tmpcurrforecast)) {
				return CURRFORECAST_ERROR;
			} else {
				currforecast.InitCurrInfo();
				currforecast.memcpy(tmpcurrforecast);
			}
			// 得到几天天气
			// 先移除前面已经存在天气
			while (tmpdayforecastArrayList.size() > 0) {
				tmpdayforecastArrayList.remove(0);
			}
			if (!getDayforecast(doc, tmpdayforecastArrayList)) {
				return DAYSFORECAST_ERROR;
			} else {
				// 移除原来保存的
				while (dayforecastArrayList.size() > 0) {
					dayforecastArrayList.remove(0);
				}
				// 把取到的存起来
				int length = tmpdayforecastArrayList.size();
				for (int i = 0; i < length; ++i) {
					Dayforecast daysforecast = new Dayforecast();
					daysforecast.memcpy(tmpdayforecastArrayList.get(i));
					dayforecastArrayList.add(daysforecast);
				}
			}
		} catch (Exception e) {

		}

		return PARSE_SUCCESS;
	}

	// 得到当前信息(第一部分)
	public boolean getCurrInfo(Document doc, CurrInfo currinfo) {
		boolean nRet = false;
		do {
			NodeList n = doc.getElementsByTagName("forecast_information");
			if (n.getLength() <= 0) {
				break;
			}
			Node item = n.item(0);
			if (!item.hasChildNodes()) {
				break;
			}
			NodeList list = item.getChildNodes(); // 得到所有孩子节点
			int length = list.getLength();
			if (length <= 0) {
				break;
			}
			for (int i = 0; i < length; i++) {
				Node tempNode = list.item(i);
				String tempStr = tempNode.getNodeName();
				if (tempStr.equals("city")) {
					currinfo.city = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("postal_code")) {
					currinfo.postal_code = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("latitude_e6")) {
					currinfo.latitude = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("longitude_e6")) {
					currinfo.longitude = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("forecast_date")) {
					currinfo.forecast_date = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("current_date_time")) {
					currinfo.current_date_time = tempNode.getAttributes()
							.item(0).getNodeValue();
				} else if (tempStr.equals("unit_system")) {
					currinfo.unit_system = tempNode.getAttributes().item(0)
							.getNodeValue();
				}
			}
			nRet = true;
		} while (false);
		return nRet;
	}

	// 得到当前天气（第二部分）
	public boolean getCurrforecast(Document doc, Currforecast currforecast) {
		boolean nRet = false;
		do {
			NodeList n = doc.getElementsByTagName("current_conditions");
			if (n.getLength() <= 0) {
				break;
			}
			Node item = n.item(0);
			if (!item.hasChildNodes()) {
				break;
			}
			NodeList list = item.getChildNodes(); // 得到所有孩子节点
			int length = list.getLength();
			if (length <= 0) {
				break;
			}
			for (int i = 0; i < length; i++) {
				Node tempNode = list.item(i);
				String tempStr = tempNode.getNodeName();
				if (tempStr.equals("condition")) {
					currforecast.condition = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("temp_f")) {
					currforecast.temp_f = Integer.parseInt(tempNode
							.getAttributes().item(0).getNodeValue());
				} else if (tempStr.equals("temp_c")) {
					currforecast.temp_c = Integer.parseInt(tempNode
							.getAttributes().item(0).getNodeValue());
				} else if (tempStr.equals("humidity")) {
					currforecast.humidity = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("icon")) {
					currforecast.icon = tempNode.getAttributes().item(0)
							.getNodeValue();
				} else if (tempStr.equals("wind_condition")) {
					currforecast.wind_condition = tempNode.getAttributes()
							.item(0).getNodeValue();
				} else {

				}
			}
			nRet = true;
		} while (false);
		return nRet;
	}

	// 得到后几天天气
	public boolean getDayforecast(Document doc,
			ArrayList<Dayforecast> dayforecast) {
		boolean nRet = false;
		do {
			NodeList n = doc.getElementsByTagName("forecast_conditions");
			int Listlength = n.getLength();
			if (Listlength <= 0) {
				break;
			}
			for (int i = 0; i < Listlength; ++i) {
				Node item = n.item(i);
				if (!item.hasChildNodes()) {
					break;
				}
				NodeList list = item.getChildNodes(); // 得到所有孩子节点
				int length = list.getLength();
				if (length <= 0) {
					break;
				}
				Dayforecast daysforecast = new Dayforecast();
				for (int j = 0; j < length; j++) {
					Node tempNode = list.item(j);
					String tempStr = tempNode.getNodeName();
					if (tempStr.equals("day_of_week")) {
						daysforecast.dayofweek = tempNode.getAttributes()
								.item(0).getNodeValue();
					} else if (tempStr.equals("low")) {
						daysforecast.low = Integer.parseInt(tempNode
								.getAttributes().item(0).getNodeValue());
					} else if (tempStr.equals("high")) {
						daysforecast.high = Integer.parseInt(tempNode
								.getAttributes().item(0).getNodeValue());
					} else if (tempStr.equals("icon")) {
						daysforecast.icon = tempNode.getAttributes().item(0)
								.getNodeValue();
					} else if (tempStr.equals("condition")) {
						daysforecast.condition = tempNode.getAttributes()
								.item(0).getNodeValue();
					}
				}
				dayforecast.add(daysforecast);
			}
			nRet = true;
		} while (false);
		return nRet;
	}

	// 取某天天气
	public Dayforecast getdayforecast(int index) {
		Dayforecast daytmp = null;
		do {
			int count = dayforecastArrayList.size();
			if (index >= count) {
				break;
			}
			daytmp = dayforecastArrayList.get(index);
			if (null == daytmp) {
				break;
			}
		} while (false);

		return daytmp;
	}

	// 得到天气数
	public int GetforecastCount() {
		return dayforecastArrayList.size();
	}

	// 取某天天气描述 图片
	public int GetdayconditionBm(Dayforecast daycast) {
		return getconditionindex(getcurconditionstr(daycast.icon));
	}

	// 从网上取xml数据线程
	public  class getXmlThread extends Thread {

		public URL mUrl;
		private Handler mHandle;
		private int msg;
		private final int TimeOut = 20 * 1000;
		public String getString = null;
		public Timer mTimer = null;

		public getXmlThread(URL url, Handler Handle, int message) {
			mHandle = Handle;
			mUrl = url;
			msg = message;
			mTimer = new Timer();
			TimerTask timeTask = new TimerTask() {

				@Override
				public void run() {
					Message m = new Message();
					m.what = msg;
					mHandle.sendMessage(m);
				}
			};
			mTimer.schedule(timeTask, TimeOut);// 三秒通知主线程数据已取完

		}
		
		public void stopThread(){
			if (mTimer!=null) {
				mTimer.cancel();
				mTimer.purge();
				mTimer=null;
			}
		}

		@Override
		public void run() {

			String tag = "HTTPSend:sendData";
			StringBuffer sb = new StringBuffer();

			try {

				HttpURLConnection connection = (HttpURLConnection) mUrl
						.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);// 忽略缓存
				connection.setReadTimeout(TimeOut);
				connection.setConnectTimeout(TimeOut);
				int responseCode = connection.getResponseCode();

				Log.e(tag, "Response code :" + connection.getResponseCode());
				if (HttpURLConnection.HTTP_OK == responseCode) {
					// 当正确响应时处理数据
					String readLine;
					BufferedReader responseReader;
					// 处理响应流，必须与服务器响应流输出的编码一致
					responseReader = new BufferedReader(new InputStreamReader(
							connection.getInputStream(), "GBK"));
					while ((readLine = responseReader.readLine()) != null) {
						sb.append(readLine).append("\n");
					}
					responseReader.close();

				} else {

				}
				connection.disconnect();

				getString = null;
				getString = sb.toString();
				if (mTimer != null) { // 取到数据马上返回
					mTimer.cancel();
					mTimer.purge();
					mTimer = null;
				}else {
					return;
				}

				Message m = new Message();
				m.what = msg;
				m.obj = getString;
				mHandle.sendMessage(m);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(Tag, e.toString());
			}
		}

	}

	public static Bitmap resizeBitmap(Bitmap bmp, int width, int height) {

		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		float scaleWidth = width * 1.0f / bmpWidth;
		float scaleheight = height * 1.0f / bmpHeight;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleheight);

		Bitmap image = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		return image;
	}

	// 当前信息
	public class CurrInfo {
		String city; // 城市
		String postal_code; // 城市代码
		String latitude; // unknown
		String longitude; // unknown
		String forecast_date; // 日期
		String current_date_time;// 更新时间
		String unit_system; // unknown

		public void InitCurrInfo() {
			city = null;
			postal_code = null;
			latitude = null;
			longitude = null;
			forecast_date = null;
			current_date_time = null;
			unit_system = null;
		}

		// 对象拷贝
		public void memcpy(CurrInfo tmp) {
			this.city = tmp.city;
			this.postal_code = tmp.postal_code;
			this.latitude = tmp.latitude;
			this.longitude = tmp.longitude;
			this.forecast_date = tmp.forecast_date;
			this.current_date_time = tmp.current_date_time;
			this.unit_system = tmp.unit_system;
		}
	}

	// 当前天气
	public class Currforecast {
		public String condition; // 天气
		public int temp_f; // 当前华氏
		public int temp_c; // 当前摄氏
		public String humidity; // 湿度
		public String icon; // 天气图片
		public String wind_condition; // 风速

		public void InitCurrInfo() {
			condition = null;
			temp_f = ERROR_TMP;
			temp_c = ERROR_TMP;
			humidity = null;
			icon = null;
			wind_condition = null;
		}

		// 对象拷贝
		public void memcpy(Currforecast tmp) {
			this.condition = tmp.condition;
			this.temp_f = tmp.temp_f;
			this.temp_c = tmp.temp_c;
			this.humidity = tmp.humidity;
			this.icon = tmp.icon;
			this.wind_condition = tmp.wind_condition;
		}
	}

	// 后几天天气(包括当天信息)
	public class Dayforecast {
		String dayofweek; // 星期
		int low; // 最低温度
		int high; // 最高温度
		String icon; // 天气图片
		String condition; // 天气

		// 对象拷贝
		public void memcpy(Dayforecast tmp) {
			this.dayofweek = tmp.dayofweek;
			this.low = tmp.low;
			this.high = tmp.high;
			this.icon = tmp.icon;
			this.condition = tmp.condition;
		}
	}

}