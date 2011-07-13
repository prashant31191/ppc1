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


public class NewsMoveParse {
	
  		public LinkedList<ItemNewsInfo> allMovieInfo=null;	//全部电影信息
  		public LinkedList<ItemNewsInfo> curMovieInfo=null; //
  		public String strSrc;
  		public InputStream mInputStream;
  		
  		public static final String tag="NewsMoveParse";
  		public String SD_PATH=Environment.getExternalStorageDirectory()  
        + "/tflash/temp/";
  		
  		
  		//读取str
  		public  NewsMoveParse(String str){
  			strSrc=str;
			allMovieInfo= new LinkedList<ItemNewsInfo>();
			curMovieInfo= new LinkedList<ItemNewsInfo>();
			CreateSdcardPath();
			
		}
  		
  		//读取xml
		public  NewsMoveParse(InputStream InputStream){
			mInputStream=InputStream;
			allMovieInfo= new LinkedList<ItemNewsInfo>();
			curMovieInfo= new LinkedList<ItemNewsInfo>();
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
		public  NewsMoveParse(URL url){
			
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
			LinkedList<ItemNewsInfo> tmpallMovieInfo = null;

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
	
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbf.newDocumentBuilder();
			Document doc = dbuilder.parse(mInputStream);
			// 得到当前信息
			LinkedList<ItemNewsInfo> tmpallMovieInfo = null;

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
		
		public boolean getcurInfo(Document mdoc, LinkedList<ItemNewsInfo> tmpinfo){
			
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
		 			
		 			ItemNewsInfo videoInfo=new ItemNewsInfo();
		 			for (int j = 0; j < length; j++) {
		 				Node tmpnode;
		 				Node tempNode=list.item(j);
						String tempStr=tempNode.getNodeName();	
						if (tempStr.equals("title")) {
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_title=tmpnode.getNodeValue();  //节点是基本类型 就这样取值
							}else {
								videoInfo.news_title=null;
							}
							
						}else if (tempStr.equals("description")) {
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_descri=tempNode.getChildNodes().item(0).getNodeValue();  
							}else {
								videoInfo.news_descri=null;
							}
							
						}else if(tempStr.equals("link")){
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_link=tmpnode.getNodeValue();
								
							}else {
								videoInfo.news_link=null;
							}
							
						}else if(tempStr.equals("author")){
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_auth=tmpnode.getNodeValue();
								
							}else {
								videoInfo.news_auth=null;
							}
						}
						else if(tempStr.equals("comments")){
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_comments=tmpnode.getNodeValue();
								
							}else {
								videoInfo.news_comments=null;
							}
						}
						else if(tempStr.equals("category")){
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_category=tmpnode.getNodeValue();
								
							}else {
								videoInfo.news_category=null;
							}
						}
						else if(tempStr.equals("pubDate")){
							tmpnode=tempNode.getChildNodes().item(0);
							if (tmpnode!=null) {
								videoInfo.news_pubDate=tmpnode.getNodeValue();
								
							}else {
								videoInfo.news_pubDate=null;
							}
						}
						}	
		 			tmpinfo.add(videoInfo);
					}//end for j	
	 			
	 			nRet=true;
			} while (false);
			return nRet;
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
		
		
		public ItemNewsInfo getCurInfo(int i){
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
		public class ItemNewsInfo{
			
			public String news_title=null;
			public String news_descri=null;
			public String news_link=null;
			public String news_auth=null;
			public String news_comments=null;
			public String news_category=null;
			public String news_pubDate=null;
			
			public ItemNewsInfo() {
			}
			
			public String getNewsTitle(){
				return news_title;
			}
			
			public String getNewsDes(){
				return news_descri;
			}
			
			public String getNewsLink(){
				return news_link;
			}
			
			
		}
}
