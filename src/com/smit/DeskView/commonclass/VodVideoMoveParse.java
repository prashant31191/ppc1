package com.smit.DeskView.commonclass;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


public class VodVideoMoveParse {
	
  		public LinkedList<ItemVideoInfo> allMovieInfo=null;	//全部电影信息
  		public LinkedList<ItemVideoInfo> curMovieInfo=null; //
  		public String strSrc;
  		public InputStream mInputStream;
  		
  		public static final String tag="MovieParse";
  		public String SD_PATH=Environment.getExternalStorageDirectory()  
        + "/tflash/temp/";
  		
  		
  		//读取str
  		public  VodVideoMoveParse(String str){
  			strSrc=str;
			allMovieInfo= new LinkedList<ItemVideoInfo>();
			curMovieInfo= new LinkedList<ItemVideoInfo>();
			CreateSdcardPath();
			
		}
  		
  		//读取xml
		public  VodVideoMoveParse(InputStream InputStream){
			mInputStream=InputStream;
			allMovieInfo= new LinkedList<ItemVideoInfo>();
			curMovieInfo= new LinkedList<ItemVideoInfo>();
			CreateSdcardPath();
		}
		
		
  		//创建sd卡路径
  		public void CreateSdcardPath(){
  			File file =new File(SD_PATH);
  			if (!file.exists()) {
				file.mkdirs();
			}
  		}
		
		//读取url
		public  VodVideoMoveParse(URL url){
			
		}
		
		
		//mInputSource
		public int parseDataStr(){
			ByteArrayInputStream stream = new ByteArrayInputStream(strSrc.getBytes());	
			InputSource mInputSource = new InputSource(stream);
			
		if (mInputSource == null) {
			return -1;
		}

		try {
			//mInputSource.setEncoding("GBK");
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
			Log.e(tag, ""+e.toString());
		}
		return 1;
		}
		
		
		//mInputStream
		public int parseDataXml(){
		if (mInputStream == null) {
			return -1;
		}

		try {
			//mInputStream.setEncoding("GBK");
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
			Log.e(tag, ""+e.toString());
		}
		return 1;
		}
	
		public static String getFileName(String pathandname){  
	        
	        int start=pathandname.lastIndexOf("/");
	        if(start!=-1){  
	            return pathandname.substring(start+1);    
	        }else{  
	            return null;  
	        }  
	          
	    }
		
		/*//有没有后缀
		public static boolean IsExistFix(String pathandname){  
	        
	        int start=pathandname.lastIndexOf("/");
	        if(start!=-1){  
	            return pathandname.substring(start+1);    
	        }else{  
	            return null;  
	        }  
	          
	    }  */
		
		public boolean getcurInfo(Document mdoc, LinkedList<ItemVideoInfo> tmpinfo){
/*			String xmlString = mdoc.toString();
			Log.i("chenyz",xmlString);*/
			
			boolean nRet=false;
			do {
	 			NodeList n = mdoc.getElementsByTagName("item");
	 			int itemcount=n.getLength();
	 			if (itemcount<=0) {
					break;
				}
	 			
	 			for (int i = 0; i < itemcount; i++) {//所有item
	 				Node item = n.item(i);
	 				
	 				if (!item.hasChildNodes()) {
						continue;
					}
	 				
	 				NodeList list=item.getChildNodes();//得到item子节点 
		 			int length=list.getLength();
		 			if (length<=0) {
						continue;
					}
		 			
		 			ItemVideoInfo videoInfo=new ItemVideoInfo();
		 			for (int j = 0; j < length; j++) {
		 				Node tmpnode;
		 				Node tempNode=list.item(j);
						String tempStr=tempNode.getNodeName();	
						if (tempStr.equals("name")) {
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.movie_name=tmpnode.getNodeValue();  //节点是基本类型 就这样取值
							}else {
								videoInfo.movie_name=null;
							}
							
						}else if (tempStr.equals("time")) {
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.movie_time=tempNode.getChildNodes().item(0).getNodeValue();  
							}else {
								videoInfo.movie_time=null;
							}
							
						}else if(tempStr.equals("description")){
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.movie_descri=tmpnode.getNodeValue();
								
							}else {
								videoInfo.movie_descri=null;
							}
							
						}else if(tempStr.equals("pictures")){
							NodeList childlist=tempNode.getChildNodes();//pictures 子节点 
				 			int len=childlist.getLength();
				 			if (len<=0) {
								continue;
							}
				 			for (int k = 0; k < len; k++) {
				 				Node childtempNodechild=childlist.item(k);
								String childtempStr=childtempNodechild.getNodeName();
								if (childtempStr.equals("picture")) {
									String string=childtempNodechild.getChildNodes().item(0).getNodeValue();
									String filepath;
									String filename=getFileName(string);
									
									filepath=SD_PATH+filename;
									videoInfo.movie_pic_path.add(filepath);
									videoInfo.movie_pic_url.add(string);				
								}					
							} //end for k
												
						}else if(tempStr.equals("urls")) {
							NodeList childlist=tempNode.getChildNodes();//urls 子节点  
				 			int len=childlist.getLength();
				 			if (len<=0) {
								continue;
							}
				 			for (int k = 0; k < len; k++) {
				 				Node childtempNodechild=childlist.item(k);
								String childtempStr=childtempNodechild.getNodeName();
								if (childtempStr.equals("url")) {
									String string=childtempNodechild.getChildNodes().item(0).getNodeValue();
									videoInfo.movie_src_url.add(string);
								}					
							}//end for k
						}
						}
		 			/*if (videoInfo.movie_name!=null
							&&videoInfo.movie_pic_url.size()>0&&videoInfo.movie_src_url.size()>0) {
						tmpinfo.add(videoInfo);
					}*/ // end for i 	
		 			tmpinfo.add(videoInfo);
					}//end for j	
	 			
	 			nRet=true;
			} while (false);
			return nRet;
		}
	
		
		//下载电影图片
		public void downloadMoviePic(){
			int count=allMovieInfo.size();
			int piccount;
			ItemVideoInfo curinfo;
			FileDownloadThread downthtrad;
			URL url;
			String picstr,filepath;
			for (int i = 0; i < count; i++) {
				curinfo=allMovieInfo.get(i);
				piccount=curinfo.movie_pic_url.size();
				for (int j = 0; j < piccount; j++) {
					filepath=curinfo.movie_pic_path.get(j);
					if (!isExistFile(filepath)) {			
						picstr=curinfo.movie_pic_url.get(j);
						try {
						//url=new URL(picstr);			
						//downthtrad=new FileDownloadThread(url,filepath,0,0);
						//downthtrad.start();
							VodVideoDownloadInfo dl = new VodVideoDownloadInfo();
							dl.desPath = filepath;
							dl.id = i;
							dl.nTotalSize = 2662720;
							dl.url = picstr;
							
							DownloadAsyncTaskPic task = new DownloadAsyncTaskPic();
			            	task.execute(dl);	
							
						} catch (Exception e) {
							Log.e(tag, "PIC URL ERROR");
						}	
					}
				}
			}
		}
		
		public void downloadMoviePic(int index,ListView listView){
			int count=allMovieInfo.size();
			int piccount;
			ItemVideoInfo curinfo;
			FileDownloadThread downthtrad;
			URL url;
			String picstr,filepath;
			if (index<count) {
				curinfo=allMovieInfo.get(index);
				piccount=curinfo.movie_pic_url.size();
				for (int j = 0; j < piccount; j++) {
					filepath=curinfo.movie_pic_path.get(j);
					if (!isExistFile(filepath)) {			
						picstr=curinfo.movie_pic_url.get(j);
						try {
							
							VodVideoDownloadInfo dl = new VodVideoDownloadInfo();
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
		
		 public class DownloadAsyncTaskPic extends AsyncTask<VodVideoDownloadInfo,VodVideoDownloadInfo,VodVideoDownloadInfo>{

		    	public static final String Tag="DownloadAsyncTask"; 
		    	private static final int BUFFER_SIZE = 1024;
		    	private int downloadSize = 0;
				  
				  @Override
				protected VodVideoDownloadInfo doInBackground(VodVideoDownloadInfo... arg0) {
				// TODO Auto-generated method stub
					  StringBuffer sb = new StringBuffer();
					  VodVideoDownloadInfo dInf = arg0[0];
					  
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
					protected void onPostExecute(VodVideoDownloadInfo result) {
						
						Bitmap bm = BitmapFactory.decodeFile(result.desPath);
						Drawable drawable = new BitmapDrawable(bm);
						ImageView vodvideo_cover;
						if (result.listView!=null) {
							View view=result.listView.getChildAt(result.id-result.listView.getFirstVisiblePosition());
							if (view!=null) {
								vodvideo_cover = (ImageView) view.findViewById(R.id.vodvideo_cover);
							}else {
								vodvideo_cover=null;
							}
							
						}else {
							vodvideo_cover=null;
						}
									
						if (vodvideo_cover!=null) {
							if (bm==null||drawable==null) {
								
								vodvideo_cover.setBackgroundResource(R.drawable.video_load);
							}else {
								vodvideo_cover.setBackgroundDrawable(drawable);	
							}
							
						}
						
						/*mDownloadTaskMap.remove(result.id);
						if(PushServiceUtil.DOWNLOAD_STOP != result.status){
							notifyClients(PushServiceUtil.MSG_DOWNLOAD,0,0,result);
						}else{
							notifyClients(PushServiceUtil.MSG_DOWNLOAD_STOP,0,0,result);
						}*/
						super.onPostExecute(result);
					}

					@Override
					protected void onProgressUpdate(VodVideoDownloadInfo... values) {
						/*Log.e(TAG, PRE+"onProgressUpdate Messager client num:"+ mClients.size());
						DownloadInf result = values[0];
						if(PushServiceUtil.DOWNLOAD_STOP != result.status){
							notifyClients(PushServiceUtil.MSG_DOWNLOAD,0,0,result);
						}else{
							notifyClients(PushServiceUtil.MSG_DOWNLOAD_STOP,0,0,result);
						}*/
						super.onProgressUpdate(values);
					}
			    	
			    }
			    
			    private void notifyClients(int what, int arg1, int arg2, Object obj){
			    	/*for (int j=mClients.size()-1; j>=0; j--) {
			            try {
			                mClients.get(j).send(Message.obtain(null,
			                		what, arg1, arg2, obj));
			            } catch (RemoteException e) {               
			                mClients.remove(j);
			            }
					} */
			    }
			    
			   /* public void notifyRosterUpdated(String jid){
			    	notifyClients(PushServiceUtil.MSG_ROSTER_UPDATED, 0, 1,jid);
			    }
			    
			    public void setOneUnreadMessage(String jid){ 		
					notifyClients(PushServiceUtil.MSG_NEW_MESSAGE,0, 0, jid);		
			    }
			    
			    public void setNewPushContent(){
			    	notifyClients(PushServiceUtil.MSG_NEW_PUSH_CONTENT,0, 0, null);
			    }*/
		
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
		
		
		public ItemVideoInfo getCurInfo(int i){
			if (i<getItemCount()) {
				return allMovieInfo.get(i); 
			}else {
				return null;
			}

		}
		
		public int getItemCount(){
			return allMovieInfo.size();
		}
		
		//一项电影信息
		public class ItemVideoInfo{
			public LinkedList<String> movie_src_url=null;
			public LinkedList<String> movie_pic_url=null;
			public LinkedList<String> movie_pic_path=null;
			public String movie_name=null;
			public String movie_descri=null;
			public String movie_time=null;
			
			public ItemVideoInfo() {
				movie_src_url=new LinkedList<String>();
				movie_pic_url=new LinkedList<String>();
				movie_pic_path=new LinkedList<String>();
			}
			
			public String getVideoName(){
				return movie_name;
			}
			
			public String getVideoDes(){
				return movie_descri;
			}
			
			public String getVideoTime(){
				return movie_time;
			}
			
			public String getPicPath(int i){
				if (i<movie_pic_path.size()) {
					return movie_pic_path.get(i);
				}else {
					return null;
				}
				
			}
			
			public String getSrcUrl(int i){
				if (i<movie_src_url.size()) {
					return movie_src_url.get(i);
				}else {
					return null;
				}
				
			}
			
		}
}
