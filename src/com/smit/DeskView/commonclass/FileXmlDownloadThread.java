package com.smit.DeskView.commonclass;

import java.io.BufferedInputStream;  
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;  
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;  
import java.io.InputStreamReader;
import java.io.RandomAccessFile;  
import java.net.HttpURLConnection;
import java.net.URL;  
import java.net.URLConnection;  
import java.net.URLEncoder;

import com.openims.downloader.DownloadInf;
   
import android.os.AsyncTask;
import android.util.Log;  
/** 
 *  Copyright (C) 2010 ideasandroid 
 *  演示android多线程下载  
 *   
 *  单个下载线程 
 */  
public class FileXmlDownloadThread extends Thread{  
    private static final int BUFFER_SIZE=1024;  
    private URL url;  
    private File file;  
    private int startPosition;  
    private int endPosition;  
    private int curPosition;  
    //用于标识当前线程是否下载完成  
    private boolean finished=false;  
    private int downloadSize=0;  
    private static final String tag="FileDownloadThread";
    public FileXmlDownloadThread(URL url,File file,int startPosition,int endPosition){  
        this.url=url;  
        this.file=file; 
        
        this.startPosition=startPosition;  
        this.curPosition=startPosition;  
        this.endPosition=endPosition;  
    }  
    
    public FileXmlDownloadThread(URL url,String path,int startPosition,int endPosition){  
        this.url=url;  
        this.file=new File(path);
        if (file.exists()) {
			file.delete();
		}
        this.file=new File(path);
        this.startPosition=startPosition;  
        this.curPosition=startPosition;  
        this.endPosition=endPosition;  
        
    }  
    @Override  
    public void run() { 
    	
		StringBuffer sb = new StringBuffer();
		try {
			
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setUseCaches(false);// 忽略缓存

			int responseCode = connection.getResponseCode();

			Log.e(tag, "Response code :" + connection.getResponseCode());
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
				Log.e(tag, e.toString());
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
				Log.e(tag, e.toString());
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
       
		
		
		/*URL url = new URL("http://localhost:8080/pring/live.do");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		 DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		 String content = "type="+URLEncoder.encode("地方频道","utf-8")+"&channel="+URLEncoder.encode("BTV卫视","utf-8");
		 out.writeBytes(content);
		 out.flush();
		 out.close();*/

    }  
   
    public boolean isFinished(){  
        return finished;  
    }  
   
    public int getDownloadSize() {  
        return downloadSize;  
    }  
    
    
}  
