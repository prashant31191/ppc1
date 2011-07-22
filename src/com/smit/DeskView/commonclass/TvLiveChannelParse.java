package com.smit.DeskView.commonclass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.R.integer;
import android.R.string;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TvLiveChannelParse {

	public LinkedList<ItemVideoInfo> allMovieInfo = null; // 全部电影信息
	public String strSrc;
	public InputStream mInputStream;

	public static final String tag = "TvLiveChannelParse";
	private static String TVLIVE_CHANNEL_FILE_DIR = "data/data/com.smit.EasyLauncher/files/";// 视屏文件
	
	//public String SD_PATH = "data/data/com.smit.EasyLauncher/files/";
	public String SD_PATH = Environment.getExternalStorageDirectory()
			+ "/tflash/temp/";

	// 读取str
	public TvLiveChannelParse(String str) {
		strSrc = str;
		allMovieInfo = new LinkedList<ItemVideoInfo>();

	}

	// 读取xml
	public TvLiveChannelParse(InputStream InputStream) {
		mInputStream = InputStream;
		allMovieInfo = new LinkedList<ItemVideoInfo>();
		CreateSdcardPath();
	}

	// 创建sd卡路径
	public void CreateSdcardPath() {
		File file = new File(SD_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	// 读取url
	public TvLiveChannelParse(URL url) {

	}

	// mInputSource
	public int parseDataStr() {
		ByteArrayInputStream stream = new ByteArrayInputStream(
				strSrc.getBytes());
		InputSource mInputSource = new InputSource(stream);

		if (mInputSource == null) {
			return -1;
		}

		try {
			// mInputSource.setEncoding("GBK");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbf.newDocumentBuilder();
			Document doc = dbuilder.parse(mInputSource);
			// 得到当前信息
			LinkedList<ItemVideoInfo> tmpallMovieInfo = null;

			if (getcurInfo(doc, allMovieInfo)) {

			} else {
				return -2;
			}
		} catch (Exception e) {
			Log.e(tag, "" + e.toString());
		}
		return 1;
	}

	// mInputStream
	public int parseDataXml() {
		if (mInputStream == null) {
			return -1;
		}

		try {
			// mInputStream.setEncoding("GBK");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbf.newDocumentBuilder();
			Document doc = dbuilder.parse(mInputStream);
			// 得到当前信息
			LinkedList<ItemVideoInfo> tmpallMovieInfo = null;

			if (getcurInfo(doc, allMovieInfo)) {

			} else {
				return -2;
			}
		} catch (Exception e) {
			Log.e(tag, "" + e.toString());
		}
		return 1;
	}

	public static String getFileName(String pathandname) {

		int start = pathandname.lastIndexOf("/");
		if (start != -1) {
			return pathandname.substring(start + 1);
		} else {
			return null;
		}

	}

	public boolean getcurInfo(Document mdoc, LinkedList<ItemVideoInfo> tmpinfo) {

		boolean nRet = false;
		do {
			NodeList n = mdoc.getElementsByTagName("channel");
			int itemcount = n.getLength();
			if (itemcount <= 0) {
				break;
			}

			for (int i = 0; i < itemcount; i++) {// 所有item
				Node item = n.item(i);

				if (!item.hasChildNodes()) {
					continue;
				}

				ItemVideoInfo videoInfo = new ItemVideoInfo();

				String string = item.getChildNodes().item(0).getNodeValue();
				videoInfo.tv_name = string;
				videoInfo.channelPath = SD_PATH + string +".xml";

				/*
				 * NodeList list=item.getChildNodes(); int
				 * length=list.getLength(); if (length<=0) { continue; }
				 * 
				 * ItemVideoInfo videoInfo=new ItemVideoInfo(); for (int j = 0;
				 * j < length; j++) { Node tmpnode; Node tempNode=list.item(j);
				 * String tempStr=tempNode.getNodeName(); if
				 * (tempStr.equals("name")) {
				 * tmpnode=tempNode.getChildNodes().item(0); if (tmpnode!=null)
				 * { videoInfo.tv_name=tmpnode.getNodeValue(); }else {
				 * videoInfo.tv_name=null; }
				 * 
				 * }else if(tempStr.equals("pictures")){ NodeList
				 * childlist=tempNode.getChildNodes(); int
				 * len=childlist.getLength(); if (len<=0) { continue; } for (int
				 * k = 0; k < len; k++) { Node
				 * childtempNodechild=childlist.item(k); String
				 * childtempStr=childtempNodechild.getNodeName(); if
				 * (childtempStr.equals("picture")) { String
				 * string=childtempNodechild
				 * .getChildNodes().item(0).getNodeValue(); String filepath;
				 * String filename=getFileName(string);
				 * 
				 * filepath=SD_PATH+filename;
				 * videoInfo.movie_pic_path.add(filepath);
				 * videoInfo.movie_pic_url.add(string); } }
				 * 
				 * } } tmpinfo.add(videoInfo); }
				 */
				tmpinfo.add(videoInfo);
			}
			nRet = true;
		} while (false);
		return nRet;
	}

	// 下载电影图片
	public void downloadMoviePic() {
		int count = allMovieInfo.size();
		int piccount;
		ItemVideoInfo curinfo;
		FileDownloadThread downthtrad;
		URL url;
		String picstr, filepath;
		for (int i = 0; i < count; i++) {
			curinfo = allMovieInfo.get(i);
			piccount = curinfo.tv_channel_icon_url.size();
			for (int j = 0; j < piccount; j++) {
				filepath = curinfo.tv_channel_icon_path.get(j);
				if (!isExistFile(filepath)) {
					picstr = curinfo.tv_channel_icon_url.get(j);
					try {
						url = new URL(picstr);
						downthtrad = new FileDownloadThread(url, filepath, 0, 0);
						downthtrad.start();
					} catch (Exception e) {
						Log.e(tag, "PIC URL ERROR");
					}
				}
			}
		}
	}

	public String chinatoString(String str) {
		/*String s = str;

		try

		{

			byte tempB[] = s.getBytes("ISO-8859-1");

			s = new String(tempB);

			return s;

		}

		catch (Exception e)

		{

			return s;

		}*/
		/*String myString=null;
		
		try {
			myString=java.net.URLDecoder.decode(str,"GB2312");

			myString=new String(str.getBytes("ISO-8859-1"));

		} catch (Exception e) {
		}
		
		return myString;*/
		
	/*	 byte[] bs = Encoding.GetEncoding("GB2312").GetBytes(str);
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < bs.Length; i++)
         {
             if (bs[i] < 128)
                 sb.Append((char)bs[i]);
             else
             {
                 sb.Append("%" + bs[i++].ToString("x").PadLeft(2, '0'));
                 sb.Append("%" + bs[i].ToString("x").PadLeft(2, '0'));
             }
         }
         return sb.ToString();*/
		/*String myString=null;
		try {
			myString=new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
		}*/
		
		String myString=null;
		try {
			myString=URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
				
		return myString;
	}

	// 下载频道节目单
	public void downloadChannelProgramList() {
		int count = allMovieInfo.size();
		int piccount;
		ItemVideoInfo curinfo;
		FileXmlDownloadThread downthtrad;
		URL url;
		String channelstr, filepath;
		for (int i = 0; i < count; i++) {
			curinfo = allMovieInfo.get(i);

			filepath = curinfo.channelPath;
			// if (isExistFile(filepath)) {
			// http://localhost:8080/pring/live.do?type=地方频道&channel=BTV卫视
			try {
				channelstr = CommonDataFun.myServerAddr
						+ "live.do?type="+chinatoString("地方频道")+"&channel=" + chinatoString(curinfo.tv_name);

				
				url = new URL(channelstr);
				downthtrad = new FileXmlDownloadThread(url, filepath, 0, 0);
				downthtrad.start();
			} catch (Exception e) {
				Log.e(tag, "PIC URL ERROR");
			}
			// }

		}
	}

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

	public ItemVideoInfo getCurInfo(int i) {
		if (i < getItemCount()) {
			return allMovieInfo.get(i);
		} else {
			return null;
		}

	}

	public int getItemCount() {
		return allMovieInfo.size();
	}

	// 一项电影信息
	public class ItemVideoInfo {
		// public LinkedList<String> movie_src_url=null;
		public LinkedList<String> tv_channel_icon_url = null;
		public LinkedList<String> tv_channel_icon_path = null;
		public String tv_name = null;
		// public String channelUrl;
		public String channelPath;

		// public String movie_descri=null;
		// public String movie_time=null;

		public ItemVideoInfo() {
			tv_channel_icon_url = new LinkedList<String>();
			tv_channel_icon_path = new LinkedList<String>();
		}

		public String getTVName() {
			return tv_name;
		}

		public String getPicPath(int i) {
			if (i < tv_channel_icon_path.size()) {
				return tv_channel_icon_path.get(i);
			} else {
				return null;
			}

		}

	}
}
