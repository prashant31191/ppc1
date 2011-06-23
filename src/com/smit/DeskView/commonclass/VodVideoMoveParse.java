package com.smit.DeskView.commonclass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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


public class VodVideoMoveParse {
	
  		public LinkedList<ItemVideoInfo> allMovieInfo=null;	//ȫ����Ӱ��Ϣ
  		public LinkedList<ItemVideoInfo> curMovieInfo=null; //
  		public String strSrc;
  		public InputStream mInputStream;
  		
  		public static final String tag="MovieParse";
  		public String SD_PATH=Environment.getExternalStorageDirectory()  
        + "/tflash/temp/";
  		
  		
  		//��ȡstr
  		public  VodVideoMoveParse(String str){
  			strSrc=str;
			allMovieInfo= new LinkedList<ItemVideoInfo>();
			curMovieInfo= new LinkedList<ItemVideoInfo>();
			CreateSdcardPath();
			
		}
  		
  		//��ȡxml
		public  VodVideoMoveParse(InputStream InputStream){
			mInputStream=InputStream;
			allMovieInfo= new LinkedList<ItemVideoInfo>();
			curMovieInfo= new LinkedList<ItemVideoInfo>();
			CreateSdcardPath();
		}
		
		
  		//����sd��·��
  		public void CreateSdcardPath(){
  			File file =new File(SD_PATH);
  			if (!file.exists()) {
				file.mkdirs();
			}
  		}
		
		//��ȡurl
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
			// �õ���ǰ��Ϣ
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
			// �õ���ǰ��Ϣ
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
		
		/*//��û�к�׺
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
	 			
	 			for (int i = 0; i < itemcount; i++) {//����item
	 				Node item = n.item(i);
	 				
	 				if (!item.hasChildNodes()) {
						continue;
					}
	 				
	 				NodeList list=item.getChildNodes();//�õ�item�ӽڵ� 
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
								videoInfo.movie_name=tmpnode.getNodeValue();  //�ڵ��ǻ������� ������ȡֵ
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
							NodeList childlist=tempNode.getChildNodes();//pictures �ӽڵ� 
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
							NodeList childlist=tempNode.getChildNodes();//urls �ӽڵ�  
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
	
		
		//���ص�ӰͼƬ
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
						url=new URL(picstr);			
						downthtrad=new FileDownloadThread(url,filepath,0,0);
						downthtrad.start();
						} catch (Exception e) {
							Log.e(tag, "PIC URL ERROR");
						}	
					}
				}
			}
		}
		
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
		
		//һ���Ӱ��Ϣ
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
