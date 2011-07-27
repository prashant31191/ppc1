package com.smit.DeskView.commonclass;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.openims.downloader.DownloadInf;
import com.smit.DeskView.commonclass.VodVideoMoveParse.DownloadAsyncTaskPic;
import com.smit.DeskView.commonclass.VodVideoMoveParse.ItemVideoInfo;
import com.smit.EasyLauncher.R;

import android.R.integer;
import android.R.string;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TvLiveChannelParse {

	public LinkedList<ItemTvInfo> allMovieInfo = null; // 全部电影信息
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
		allMovieInfo = new LinkedList<ItemTvInfo>();

	}

	// 读取xml
	public TvLiveChannelParse(InputStream InputStream) {
		mInputStream = InputStream;
		allMovieInfo = new LinkedList<ItemTvInfo>();
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
			LinkedList<ItemTvInfo> tmpallMovieInfo = null;

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
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbf.newDocumentBuilder();
			Document doc = dbuilder.parse(mInputStream);
			// 得到当前信息
			LinkedList<ItemTvInfo> tmpallMovieInfo = null;

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

	public boolean getcurInfo(Document mdoc, LinkedList<ItemTvInfo> tmpinfo) {

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

				ItemTvInfo videoInfo = new ItemTvInfo();

				String string = item.getChildNodes().item(0).getNodeValue();
				videoInfo.tv_name = string;
				videoInfo.channelPath = SD_PATH + string +".xml";

				tmpinfo.add(videoInfo);
			}
			nRet = true;
		} while (false);
		return nRet;
	}


	/*public void downloadMoviePic() {
		int count = allMovieInfo.size();
		int piccount;
		ItemTvInfo curinfo;
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
						
						TvLiveDownloadInfo dl = new TvLiveDownloadInfo();
						dl.desPath = filepath;
						dl.id = i;
						dl.nTotalSize = 2662720;
						dl.url = picstr;
						
						DownloadAsyncTaskProgramList task = new DownloadAsyncTaskProgramList();
		            	task.execute(dl);
					} catch (Exception e) {
						Log.e(tag, "PIC URL ERROR");
					}
				}
			}
		}
	}*/
	
	public void downloadMoviePic(int index,ListView listView){
		int count=allMovieInfo.size();
		int piccount;
		ItemTvInfo curinfo;
		FileDownloadThread downthtrad;
		URL url;
		String picstr,filepath;
		if (index<count) {
			curinfo=allMovieInfo.get(index);
			piccount=curinfo.tv_channel_icon_url.size();
			for (int j = 0; j < piccount; j++) {
				filepath=curinfo.tv_channel_icon_path.get(j);
				if (!isExistFile(filepath)) {			
					picstr=curinfo.tv_channel_icon_url.get(j);
					try {
						
						TvLiveDownloadInfo dl = new TvLiveDownloadInfo();
						dl.desPath = filepath;
						dl.id = index;
						dl.nTotalSize = 2662720;
						dl.url = picstr;
						dl.listView=listView;
						
						DownloadAsyncTaskPic task = new DownloadAsyncTaskPic();
		            	task.execute(dl);	
						
					} catch (Exception e) {
						Log.e(tag, "PIC URL ERROR");
					}	
				}
			}
		}
			
	}

	public String chinatoString(String str) {
		
		String myString=null;
		try {
			myString=URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
				
		return myString;
	}

	// 下载频道节目单
	/*public void downloadChannelProgramList() {
		int count = allMovieInfo.size();
		int piccount;
		ItemTvInfo curinfo;
		FileXmlDownloadThread downthtrad;
		URL url;
		String channelstr, filepath;

		
		for (int i = 0; i < count; i++) {
			curinfo = allMovieInfo.get(i);

			filepath = curinfo.channelPath;
			try {
				channelstr = CommonDataFun.myServerAddr
						+ "live.do?type="+chinatoString("地方频道")+"&channel=" + chinatoString(curinfo.tv_name);
				
				TvLiveDownloadInfo dl = new TvLiveDownloadInfo();
				dl.desPath = filepath;
				dl.id = i;
				dl.nTotalSize = 2662720;
				dl.url = channelstr;
				
				DownloadAsyncTaskProgramList task = new DownloadAsyncTaskProgramList();
            	task.execute(dl);
				
			} catch (Exception e) {
				Log.e(tag, "PIC URL ERROR");
			}
		}
		
	}*/
	
	// 下载频道节目单
	public void downloadChannelProgramList(int index,ListView listView) {
		int count = allMovieInfo.size();
		int piccount;
		ItemTvInfo curinfo;
		FileXmlDownloadThread downthtrad;
		URL url;
		String channelstr, filepath;
		if (index<count) {
			curinfo = allMovieInfo.get(index);

			filepath = curinfo.channelPath;
			try {
				channelstr = CommonDataFun.myServerAddr
						+ "live.do?type="+chinatoString("地方频道")+"&channel=" + chinatoString(curinfo.tv_name);

				
/*				url = new URL(channelstr);
				downthtrad = new FileXmlDownloadThread(url, filepath, 0, 0);
				
				downthtrad.start();*/
				
				TvLiveDownloadInfo dl = new TvLiveDownloadInfo();
				dl.desPath = filepath;
				dl.id = index;
				dl.nTotalSize = 2662720;
				dl.url = channelstr;
				dl.listView=listView;
				
				DownloadAsyncTaskProgramList task = new DownloadAsyncTaskProgramList();
            	task.execute(dl);
				
			} catch (Exception e) {
				Log.e(tag, "PIC URL ERROR");
			}
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

	public ItemTvInfo getCurInfo(int i) {
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
	public class ItemTvInfo {
		// public LinkedList<String> movie_src_url=null;
		public LinkedList<String> tv_channel_icon_url = null;
		public LinkedList<String> tv_channel_icon_path = null;
		public String tv_name = null;
		// public String channelUrl;
		public String channelPath;

		// public String movie_descri=null;
		// public String movie_time=null;

		public ItemTvInfo() {
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
	
	
	 public class DownloadAsyncTaskProgramList extends AsyncTask<TvLiveDownloadInfo,TvLiveDownloadInfo,TvLiveDownloadInfo>{

		 
	    	public static final String Tag="DownloadAsyncTask"; 
			  
			  @Override
			protected TvLiveDownloadInfo doInBackground(TvLiveDownloadInfo... arg0) {
			// TODO Auto-generated method stub
				  StringBuffer sb = new StringBuffer();
				  TvLiveDownloadInfo dInf = arg0[0];
					try {
						URL url = new URL(dInf.url);
					    File file = new File(dInf.desPath);
						
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();
						connection.setDoOutput(true);
						connection.setUseCaches(false);// 忽略缓存

						int responseCode = connection.getResponseCode();

						Log.e(Tag, "Response code :" + connection.getResponseCode());
						if (HttpURLConnection.HTTP_OK == responseCode) {
							// 当正确响应时处理数据
							String readLine;
							BufferedReader responseReader;
							// 处理响应流，必须与服务器响应流输出的编码一致
							responseReader = new BufferedReader(new InputStreamReader(
									connection.getInputStream(), "UTF-8"));
							while ((readLine = responseReader.readLine()) != null) {
								sb.append(readLine).append("\n");
							}
							responseReader.close();

						} else {

						}
						connection.disconnect();

						FileOutputStream fileos = null;
						try {
							fileos = new FileOutputStream(file);
						} catch (FileNotFoundException e) {
							Log.e(Tag, e.toString());
						}

						try {
							byte buf[] = sb.toString().getBytes();
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
					}catch (Exception e) {
						// TODO: handle exception
					}
				  
				  
				  return dInf;
			}

				@Override
				protected void onPostExecute(TvLiveDownloadInfo result) {			
				
					TextView tvlive_isplay;
					if (result.listView!=null) {
						View view=result.listView.getChildAt(result.id-result.listView.getFirstVisiblePosition());
						if (view!=null) {
							tvlive_isplay = (TextView) view.findViewById(R.id.tvlive_isplay);
						}else {
							tvlive_isplay=null;
						}
						
					}else {
						tvlive_isplay=null;
					}
						
					String isplayingString=getCurPlayProgram(result.desPath);
					if (tvlive_isplay!=null) {
						if (isplayingString!=null) {
							tvlive_isplay.setText("正在播放:"+isplayingString);
						}else {
							tvlive_isplay.setText("");
						}
					}
					
					super.onPostExecute(result);
				}

				@Override
				protected void onProgressUpdate(TvLiveDownloadInfo... values) {
					
					super.onProgressUpdate(values);
				}
				
				
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
				public String getCurPlayProgram(String path) {
					String strCurProgram = null;
					do {
						try {
							
							InputStream is = null;
							byte[] data = null;
							String str = null;
							File file = new File(path);
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
		    	
		    }
		    
		    
		    public class DownloadAsyncTaskPic extends AsyncTask<TvLiveDownloadInfo,TvLiveDownloadInfo,TvLiveDownloadInfo>{

		    	public static final String Tag="DownloadAsyncTask"; 
		    	private static final int BUFFER_SIZE = 1024;
		    	private int downloadSize = 0;
				  
				  @Override
				protected TvLiveDownloadInfo doInBackground(TvLiveDownloadInfo... arg0) {
				// TODO Auto-generated method stub
					  StringBuffer sb = new StringBuffer();
					  TvLiveDownloadInfo dInf = arg0[0];
					  
						BufferedInputStream bis = null;  
				        RandomAccessFile fos = null;                                                 
				        byte[] buf = new byte[BUFFER_SIZE];  
				        URLConnection con = null;  
				        try {
				        	URL url = new URL(dInf.url);
						    File file = new File(dInf.desPath);
				        	
				            con = url.openConnection();  
				            con.setAllowUserInteraction(true); 
				          
				            fos = new RandomAccessFile(file, "rw");         
				            fos.seek(0); 
				            //获取下载文件的总大小  
				            int fileSize = con.getContentLength(); 
				            
				            bis = new BufferedInputStream(con.getInputStream());
				            while (true) {  
				            int len = bis.read(buf, 0, BUFFER_SIZE);                  
				            if (len == -1) {  
				                break;  
				            }  
				            fos.write(buf, 0, len);  
				            downloadSize+=len;
				        }  
		
				            bis.close();  
				            fos.close();  
				        } catch (IOException e) {  
				          Log.d(tag, e.getMessage());  
				        }  
					  
					  
					  return dInf;
				}

					@Override
					protected void onPostExecute(TvLiveDownloadInfo result) {
						
						Bitmap bm = BitmapFactory.decodeFile(result.desPath);
						Drawable drawable = new BitmapDrawable(bm);
						View view=result.listView.getChildAt(result.id-result.listView.getFirstVisiblePosition());
						ImageView vodvideo_cover = (ImageView) view.findViewById(R.id.vodvideo_cover);
						if (vodvideo_cover!=null) {
							if (bm==null||drawable==null) {
								
								vodvideo_cover.setBackgroundResource(R.drawable.tv_load);
							}else {
								vodvideo_cover.setBackgroundDrawable(drawable);	
							}
							
						}
						
						super.onPostExecute(result);
					}

					@Override
					protected void onProgressUpdate(TvLiveDownloadInfo... values) {
						super.onProgressUpdate(values);
					}
			    	
			    }
			    
			   	    
}
