package com.smit.DeskView.commonclass;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

/**
 * Copyright (C) 2010 ideasandroid 演示android多线程下载
 * 
 * 单个下载线程
 */
public class FileDownloadThread extends Thread {
	private static final int BUFFER_SIZE = 1024;
	private URL url;
	private File file;
	private int startPosition;
	private int endPosition;
	private int curPosition;
	// 用于标识当前线程是否下载完成
	private boolean finished = false;
	private int downloadSize = 0;
	private static final String tag = "FileDownloadThread";

	public FileDownloadThread(URL url, File file, int startPosition,
			int endPosition) {
		this.url = url;
		this.file = file;
		this.startPosition = startPosition;
		this.curPosition = startPosition;
		this.endPosition = endPosition;
	}

	public FileDownloadThread(URL url, String path, int startPosition,
			int endPosition) {
		this.url = url;
		this.file=new File(path);
        if (file.exists()) {
			file.delete();
		}
        this.file=new File(path);
		this.startPosition = startPosition;
		this.curPosition = startPosition;
		this.endPosition = endPosition;

	}

	@Override
	public void run() {
		 BufferedInputStream bis = null;  
	        RandomAccessFile fos = null;                                                 
	        byte[] buf = new byte[BUFFER_SIZE];  
	        URLConnection con = null;  
	        try {  
	            con = url.openConnection();  
	            con.setAllowUserInteraction(true); 
	          
	            fos = new RandomAccessFile(file, "rw");         
	            fos.seek(startPosition); 
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
	            //下载完成设为true  
	            this.finished = true;  
	            bis.close();  
	            fos.close();  
	        } catch (IOException e) {  
	          Log.d(tag, e.getMessage());  
	        }  
	}

	public boolean isFinished() {
		return finished;
	}

	public int getDownloadSize() {
		return downloadSize;
	}
}
